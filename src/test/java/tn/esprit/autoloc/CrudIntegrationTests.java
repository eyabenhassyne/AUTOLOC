package tn.esprit.autoloc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CrudIntegrationTests {
    @LocalServerPort
    int port;

    private final HttpClient client = HttpClient.newHttpClient();
    private final JsonMapper mapper = JsonMapper.builder().build();

    @ParameterizedTest
    @ValueSource(strings = {"Agence", "Employe", "Vehicule", "Maintenance", "Equipement",
            "Client", "Reservation", "Contrat", "Paiement"})
    void crudWorksForEveryModule(String entity) throws Exception {
        String route = entity.toLowerCase();
        String payload = entity.equals("Vehicule") ? vehicle("CRUD-TEST") : "{}";
        JsonNode created = json("POST", "/" + route + "/create", payload);
        long id = created.path("id" + entity).asLong();
        assertTrue(id > 0);
        assertEquals(id, json("GET", "/" + route + "/get/" + id, null).path("id" + entity).asLong());
        assertTrue(json("GET", "/" + route + "/getAll", null).isArray());
        assertEquals(id, json("PUT", "/" + route + "/update", created.toString())
                .path("id" + entity).asLong());
        response("DELETE", "/" + route + "/delete/" + id, null);
        JsonNode all = json("GET", "/" + route + "/getAll", null);
        for (JsonNode item : all) {
            assertNotEquals(id, item.path("id" + entity).asLong());
        }
    }

    @Test
    void relationshipsPersistAndSerializeWithoutCycles() throws Exception {
        long agence = create("agence", "{\"nom\":\"Integration agency\"}", "Agence");
        create("employe", "{\"agence\":{\"idAgence\":" + agence + "}}", "Employe");
        long equipement = create("equipement", "{\"libelle\":\"GPS\"}", "Equipement");
        String vehiculeBody = vehicle("REL-TEST");
        vehiculeBody = vehiculeBody.substring(0, vehiculeBody.length() - 1)
                + ",\"agence\":{\"idAgence\":" + agence + "},\"equipements\":[{\"idEquipement\":" + equipement + "}]}";
        long vehicule = create("vehicule", vehiculeBody, "Vehicule");
        create("maintenance", "{\"vehicule\":{\"idVehicule\":" + vehicule + "}}", "Maintenance");
        long clientId = create("client", "{\"nom\":\"Integration client\"}", "Client");
        long reservation = create("reservation", "{\"client\":{\"idClient\":" + clientId
                + "},\"vehicule\":{\"idVehicule\":" + vehicule + "}}", "Reservation");
        long contrat = create("contrat", "{\"reservation\":{\"idReservation\":" + reservation + "}}", "Contrat");
        long paiement = create("paiement", "{\"montant\":125.50,\"contrat\":{\"idContrat\":" + contrat + "}}", "Paiement");
        JsonNode loaded = json("GET", "/paiement/get/" + paiement, null);
        JsonNode booking = loaded.path("contrat").path("reservation");
        assertEquals(clientId, booking.path("client").path("idClient").asLong());
        assertEquals(agence, booking.path("vehicule").path("agence").path("idAgence").asLong());
        assertEquals(equipement, booking.path("vehicule").path("equipements").get(0).path("idEquipement").asLong());
        assertFalse(booking.has("contrat"));
        assertFalse(booking.path("vehicule").has("reservations"));
        // The unique reservation foreign key must enforce the one-to-one contract.
        HttpResponse<String> duplicate = send("POST", "/contrat/create",
                "{\"reservation\":{\"idReservation\":" + reservation + "}}");
        assertEquals(409, duplicate.statusCode());
    }

    @Test
    void contractCascadesPaymentsAndSetsTheirOwningSide() throws Exception {
        long contrat = create("contrat", "{\"paiements\":[{\"montant\":42.25}]}", "Contrat");
        JsonNode payments = json("GET", "/paiement/getAll", null);
        boolean found = false;
        for (JsonNode payment : payments) {
            if (payment.path("contrat").path("idContrat").asLong() == contrat) {
                found = true;
            }
        }
        assertTrue(found, "Nested payments must be linked to their contract");
        response("DELETE", "/contrat/delete/" + contrat, null);
        for (JsonNode payment : json("GET", "/paiement/getAll", null)) {
            assertNotEquals(contrat, payment.path("contrat").path("idContrat").asLong());
        }
    }

    @Test
    void swaggerDocumentsAllNineControllers() throws Exception {
        JsonNode paths = json("GET", "/v3/api-docs", null).path("paths");
        for (String route : new String[]{"agence", "employe", "vehicule", "maintenance", "equipement",
                "client", "reservation", "contrat", "paiement"}) {
            assertTrue(paths.has("/" + route + "/create"));
            assertTrue(paths.has("/" + route + "/getAll"));
        }
        assertTrue(response("GET", "/swagger-ui/index.html", null).body().contains("Swagger UI"));
    }

    private String vehicle(String registration) {
        return "{\"immatriculation\":\"" + registration + "\",\"marque\":\"Test\",\"modele\":\"Test\","
                + "\"categorie\":\"CITADINE\",\"tarifJournalier\":80.50,\"statut\":\"DISPONIBLE\"}";
    }

    private long create(String route, String body, String entity) throws Exception {
        return json("POST", "/" + route + "/create", body).path("id" + entity).asLong();
    }

    private JsonNode json(String method, String path, String body) throws Exception {
        return mapper.readTree(response(method, path, body).body());
    }

    private HttpResponse<String> response(String method, String path, String body) throws Exception {
        HttpResponse<String> result = send(method, path, body);
        assertEquals(200, result.statusCode(), () -> method + " " + path + ": " + result.body());
        return result;
    }

    private HttpResponse<String> send(String method, String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
