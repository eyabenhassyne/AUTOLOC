package tn.esprit.autoloc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkflowIntegrationTests {
    @LocalServerPort int port;
    private final HttpClient http =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final JsonMapper mapper = JsonMapper.builder().build();

    @ParameterizedTest
    @ValueSource(
            strings = {
                "Agence",
                "Client",
                "Contrat",
                "Employe",
                "Equipement",
                "Maintenance",
                "Paiement",
                "Reservation",
                "Vehicule"
            })
    void conventionalCrudAndErrorsForEveryEntity(String entity) throws Exception {
        String base = "/" + entity.toLowerCase(Locale.ROOT);
        String body = entity.equals("Vehicule") ? vehicle() : "{}";
        HttpResponse<String> created = expect(201, "POST", base, body);
        JsonNode saved = mapper.readTree(created.body());
        long id = saved.path("id" + entity).asLong();
        assertTrue(id > 0);
        assertEquals(base + "/" + id, created.headers().firstValue("Location").orElseThrow());
        expect(200, "GET", base + "/" + id, null);
        assertTrue(json(200, "GET", base, null).isArray());
        expect(200, "PUT", base + "/" + id, saved.toString());
        expect(400, "POST", base, saved.toString());
        expect(400, "PUT", base + "/" + (id + 1), saved.toString());
        String missingBody = entity.equals("Vehicule") ? body : "{}";
        expect(404, "PUT", base + "/9223372036854775806", missingBody);
        expect(204, "DELETE", base + "/" + id, null);
        expect(404, "GET", base + "/" + id, null);
        expect(404, "GET", base + "/get/" + id, null);
        expect(404, "DELETE", base + "/" + id, null);
        expect(400, "GET", base + "/0", null);
    }

    @Test
    void clientReservationsPersistWithOwningClientAndRollBackAsAUnit() throws Exception {
        long vehicle = id(201, "POST", "/vehicule", vehicle(), "Vehicule");
        JsonNode result =
                json(
                        201,
                        "POST",
                        "/client/with-reservations",
                        """
                        {"client":{"nom":"Workflow client","email":"client@example.com"},
                         "reservations":[{"dateDebut":"2026-11-15","dateFin":"2026-11-17",
                         "vehicule":{"idVehicule":%d}},{"statut":"EN_ATTENTE"}]}
                        """
                                .formatted(vehicle));
        long client = result.path("client").path("idClient").asLong();
        assertEquals(2, result.path("reservations").size());
        JsonNode persisted = json(200, "GET", "/client/" + client + "/reservations", null);
        assertEquals(2, persisted.size());
        for (JsonNode r : persisted) {
            JsonNode loaded =
                    json(200, "GET", "/reservation/" + r.path("idReservation").asLong(), null);
            assertEquals(client, loaded.path("client").path("idClient").asLong());
        }
        // Updating scalar fields must not erase inverse children.
        expect(200, "PUT", "/client/" + client, "{\"nom\":\"Updated\"}");
        assertEquals(2, json(200, "GET", "/client/" + client + "/reservations", null).size());
        int clients = count("client"), reservations = count("reservation");
        expect(
                404,
                "POST",
                "/client/with-reservations",
                """
                {"client":{"nom":"Must roll back"},"reservations":[{},
                  {"vehicule":{"idVehicule":9223372036854775806}}]}
                """);
        assertEquals(clients, count("client"));
        assertEquals(reservations, count("reservation"));
        expect(409, "DELETE", "/client/" + client, null);
        assertEquals(2, json(200, "GET", "/client/" + client + "/reservations", null).size());
    }

    @Test
    void reservationContractAndPaymentsPersistWithoutChangingExistingPaymentFields()
            throws Exception {
        long payment =
                id(
                        201,
                        "POST",
                        "/paiement",
                        "{\"montant\":75.50,\"datePaiement\":\"2026-11-15\",\"modePaiement\":\"CARTE\"}",
                        "Paiement");
        JsonNode result =
                json(
                        201,
                        "POST",
                        "/reservation/with-contract",
                        """
                        {"reservation":{"dateDebut":"2026-11-15","dateFin":"2026-11-18"},
                         "contrat":{"valide":true,"montantTotal":100,
                         "paiements":[{"idPaiement":%d},{"montant":24.50}]}}
                        """
                                .formatted(payment));
        long reservation = result.path("reservation").path("idReservation").asLong();
        long contract = result.path("contrat").path("idContrat").asLong();
        assertEquals(
                reservation,
                result.path("contrat").path("reservation").path("idReservation").asLong());
        assertFalse(result.path("reservation").has("contrat"));
        assertEquals(
                contract,
                json(200, "GET", "/reservation/" + reservation + "/contrat", null)
                        .path("idContrat")
                        .asLong());
        JsonNode oldPayment = json(200, "GET", "/paiement/" + payment, null);
        assertEquals(75.5, oldPayment.path("montant").asDouble());
        assertEquals("CARTE", oldPayment.path("modePaiement").asText());
        assertEquals("2026-11-15", oldPayment.path("datePaiement").asText());
        assertEquals(2, json(200, "GET", "/contrat/" + contract + "/paiements", null).size());
        expect(200, "PUT", "/contrat/" + contract, "{\"valide\":false,\"montantTotal\":100}");
        assertEquals(2, json(200, "GET", "/contrat/" + contract + "/paiements", null).size());
        assertEquals(
                reservation,
                json(200, "GET", "/contrat/" + contract, null)
                        .path("reservation")
                        .path("idReservation")
                        .asLong());
        long added =
                id(
                        201,
                        "POST",
                        "/contrat/" + contract + "/paiements",
                        "{\"montant\":10}",
                        "Paiement");
        assertEquals(
                contract,
                json(200, "GET", "/paiement/" + added, null)
                        .path("contrat")
                        .path("idContrat")
                        .asLong());
        expect(
                409,
                "POST",
                "/contrat",
                "{\"reservation\":{\"idReservation\":" + reservation + "}}");
        expect(409, "DELETE", "/reservation/" + reservation, null);
        int contracts = count("contrat");
        expect(409, "POST", "/contrat", "{\"paiements\":[{\"idPaiement\":" + payment + "}]}");
        assertEquals(contracts, count("contrat"));
        assertEquals(
                contract,
                json(200, "GET", "/paiement/" + payment, null)
                        .path("contrat")
                        .path("idContrat")
                        .asLong());
        // Preserve the original application's contract -> payment deletion cascade.
        expect(204, "DELETE", "/contrat/" + contract, null);
        expect(404, "GET", "/paiement/" + payment, null);
        expect(404, "GET", "/paiement/" + added, null);
        expect(200, "GET", "/reservation/" + reservation, null);
        expect(404, "GET", "/reservation/" + reservation + "/contrat", null);
        expect(204, "DELETE", "/reservation/" + reservation, null);
    }

    @Test
    void invalidNestedPaymentsRollBackReservationContractAndNewPayments() throws Exception {
        int reservations = count("reservation"),
                contracts = count("contrat"),
                payments = count("paiement");
        expect(
                400,
                "POST",
                "/reservation/with-contract",
                """
                {"reservation":{},"contrat":{"paiements":[{"montant":5},{"montant":-1}]}}
                """);
        assertEquals(reservations, count("reservation"));
        assertEquals(contracts, count("contrat"));
        assertEquals(payments, count("paiement"));
        expect(
                404,
                "POST",
                "/reservation/with-contract",
                """
                {"reservation":{},"contrat":{"paiements":[{"idPaiement":9223372036854775806}]}}
                """);
        assertEquals(reservations, count("reservation"));
        assertEquals(contracts, count("contrat"));
    }

    @Test
    void assignmentsArePersistentIdempotentAndRejectMissingTargets() throws Exception {
        long agency = id(201, "POST", "/agence", "{\"nom\":\"Assignments\"}", "Agence");
        long employee = id(201, "POST", "/employe", "{\"nom\":\"Employee\"}", "Employe");
        String employeeRoute = "/employe/" + employee + "/agence/";
        expect(200, "PUT", employeeRoute + agency, null);
        expect(404, "PUT", employeeRoute + "9223372036854775806", null);
        assertEquals(
                agency,
                json(200, "GET", "/employe/" + employee, null)
                        .path("agence")
                        .path("idAgence")
                        .asLong());
        expect(200, "PUT", "/employe/" + employee, "{\"nom\":\"Updated\"}");
        assertEquals(
                agency,
                json(200, "GET", "/employe/" + employee, null)
                        .path("agence")
                        .path("idAgence")
                        .asLong());
        expect(409, "DELETE", "/agence/" + agency, null);
        long vehicle = id(201, "POST", "/vehicule", vehicle(), "Vehicule");
        long equipment = id(201, "POST", "/equipement", "{\"libelle\":\"GPS\"}", "Equipement");
        String equipmentRoute = "/vehicule/" + vehicle + "/equipement/" + equipment;
        expect(200, "PUT", equipmentRoute, null);
        expect(200, "PUT", equipmentRoute, null);
        List<CompletableFuture<HttpResponse<String>>> parallel = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            parallel.add(
                    http.sendAsync(
                            request("PUT", equipmentRoute, null),
                            HttpResponse.BodyHandlers.ofString()));
        for (var future : parallel) assertEquals(200, future.join().statusCode());
        assertEquals(1, json(200, "GET", "/vehicule/" + vehicle, null).path("equipements").size());
        expect(404, "PUT", "/vehicule/" + vehicle + "/equipement/9223372036854775806", null);
        long maintenance = id(201, "POST", "/maintenance", "{}", "Maintenance");
        expect(200, "PUT", "/maintenance/" + maintenance + "/vehicule/" + vehicle, null);
        expect(404, "PUT", "/maintenance/" + maintenance + "/vehicule/9223372036854775806", null);
        assertEquals(
                vehicle,
                json(200, "GET", "/maintenance/" + maintenance, null)
                        .path("vehicule")
                        .path("idVehicule")
                        .asLong());
        expect(200, "DELETE", equipmentRoute, null);
        assertEquals(0, json(200, "GET", "/vehicule/" + vehicle, null).path("equipements").size());
        expect(200, "GET", "/equipement/" + equipment, null);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "/client|{\"email\":\"invalid\"}",
                "/vehicule|{}",
                "/paiement|{\"montant\":-1}",
                "/contrat|{\"montantTotal\":-1}",
                "/reservation|{\"dateDebut\":\"2026-11-20\",\"dateFin\":\"2026-11-15\"}",
                "/maintenance|{\"dateDebut\":\"2026-11-20\",\"dateFin\":\"2026-11-15\"}",
                "/client/with-reservations|{\"client\":{},\"reservations\":[]}",
                "/client/with-reservations|{\"client\":{},\"reservations\":[null]}",
                "/reservation/with-contract|{\"reservation\":{}}",
                "/reservation|{\"statut\":\"INVALID\"}",
                "/client|{"
            })
    void invalidRequestsReturnProblemDetails(String value) throws Exception {
        String[] parts = value.split("\\|", 2);
        JsonNode problem = json(400, "POST", parts[0], parts[1]);
        assertEquals(400, problem.path("status").asInt());
        assertFalse(problem.has("trace"));
    }

    @Test
    void swaggerIncludesNewOperationsAndResponseStatuses() throws Exception {
        JsonNode paths = json(200, "GET", "/v3/api-docs", null).path("paths");
        for (String path :
                new String[] {
                    "/client/with-reservations",
                    "/reservation/with-contract",
                    "/employe/{employeId}/agence/{agenceId}",
                    "/vehicule/{vehiculeId}/equipement/{equipementId}",
                    "/maintenance/{maintenanceId}/vehicule/{vehiculeId}",
                    "/contrat/{id}/paiements"
                }) assertTrue(paths.has(path), path);
        assertTrue(paths.path("/client/with-reservations").path("post").has("requestBody"));
        assertTrue(paths.path("/client").path("post").path("responses").has("201"));
    }

    private String vehicle() {
        return "{\"immatriculation\":\""
                + UUID.randomUUID().toString().substring(0, 18)
                + "\",\"marque\":\"Test\",\"modele\":\"Test\","
                + "\"categorie\":\"CITADINE\",\"tarifJournalier\":45.50,\"statut\":\"DISPONIBLE\"}";
    }

    private int count(String route) throws Exception {
        return json(200, "GET", "/" + route, null).size();
    }

    private long id(int status, String method, String path, String body, String entity)
            throws Exception {
        long id = json(status, method, path, body).path("id" + entity).asLong();
        assertTrue(id > 0);
        return id;
    }

    private JsonNode json(int status, String method, String path, String body) throws Exception {
        return mapper.readTree(expect(status, method, path, body).body());
    }

    private HttpResponse<String> expect(int status, String method, String path, String body)
            throws Exception {
        var response = http.send(request(method, path, body), HttpResponse.BodyHandlers.ofString());
        assertEquals(
                status, response.statusCode(), () -> method + " " + path + ": " + response.body());
        return response;
    }

    private HttpRequest request(String method, String path, String body) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .method(
                        method,
                        body == null
                                ? HttpRequest.BodyPublishers.noBody()
                                : HttpRequest.BodyPublishers.ofString(body))
                .build();
    }
}
