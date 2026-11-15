package tn.esprit.autoloc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties =
                "spring.datasource.url=jdbc:h2:mem:autoloc-keywords-http;MODE=MySQL;DB_CLOSE_DELAY=-1")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehiculeKeywordsHttpTests {
    @LocalServerPort int port;
    private final HttpClient http = HttpClient.newHttpClient();
    private final JsonMapper mapper = JsonMapper.builder().build();
    private long agencyA;

    @BeforeAll
    void seedThroughExistingCrud() throws Exception {
        agencyA = createAgency("AutoLoc Centre", "Tunis");
        long b = createAgency("AutoLoc Sud", "Sfax"), c = createAgency("Luxe", "Tunis");
        createAgency("Agence Vide", "Bizerte");
        seedVehicle("T1", "Toyota", "Yaris", "50", "DISPONIBLE", agencyA);
        seedVehicle("T2", "Toyota", "Corolla", "100", "LOUE", agencyA);
        seedVehicle("T3", "TOYOTA", "Yaris", "75", "DISPONIBLE", b);
        seedVehicle("R1", "Renault", "Clio", "80", "DISPONIBLE", b);
        seedVehicle("F1", "Ford", "Focus", "100", "MAINTENANCE", null);
        seedVehicle("B1", "BMW", "Yaris", "150", "DISPONIBLE", c);
        seedVehicle("H1", "Hyundai", "i20", "120", "LOUE", null);
        seedVehicle("K1", "Kia", "Rio", "80", "DISPONIBLE", agencyA);
    }

    static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of("marque?marque=Toyota", "T1,T2", "vehicles"),
                Arguments.of("marque/contains?value=oy", "T1,T2", "vehicles"),
                Arguments.of("marque/starts-with?value=Toy", "T1,T2", "vehicles"),
                Arguments.of("marque/ends-with?value=ota", "T1,T2", "vehicles"),
                Arguments.of("marque/ignore-case?marque=toyota", "T1,T2,T3", "vehicles"),
                Arguments.of("modele?modele=Yaris", "T1,T3,B1", "vehicles"),
                Arguments.of("immatriculation/exists?immatriculation=T1", "true", "boolean"),
                Arguments.of("marque/count?marque=Toyota", "2", "long"),
                Arguments.of("prix/greater-than?prix=100", "H1,B1", "vehicles"),
                Arguments.of("prix/less-than?prix=80", "T1,T3", "vehicles"),
                Arguments.of("prix/greater-than-equal?prix=100", "T2,F1,H1,B1", "vehicles"),
                Arguments.of("prix/between?minimum=75&maximum=100", "T3,R1,K1,T2,F1", "vehicles"),
                Arguments.of("prix/asc", "50,75,80,80,100,100,120,150", "prices"),
                Arguments.of("prix/desc", "150,120,100,100,80,80,75,50", "prices"),
                Arguments.of("marque/prix-asc?marque=Toyota", "T1,T2", "vehicles"),
                Arguments.of("marque/prix-greater-than?marque=Toyota&prix=50", "T2", "vehicles"),
                Arguments.of(
                        "marque-or-modele?marque=Toyota&modele=Yaris", "T1,T2,T3,B1", "vehicles"),
                Arguments.of("marques/in?marques=Toyota,Ford", "T1,T2,F1", "vehicles"),
                Arguments.of("marques/not-in?marques=Toyota,Ford", "T3,R1,B1,H1,K1", "vehicles"),
                Arguments.of("statut?statut=DISPONIBLE", "T1,T3,R1,B1,K1", "vehicles"),
                Arguments.of("disponibles/prix-asc", "T1,T3,R1,K1,B1", "vehicles"),
                Arguments.of("agence?agenceId=<idA>", "T1,T2,K1", "vehicles"),
                Arguments.of("agence/ville?ville=Tunis", "T1,T2,K1,B1", "vehicles"),
                Arguments.of("agence/nom/contains?value=AutoLoc", "T1,T2,T3,R1,K1", "vehicles"),
                Arguments.of("agence/marque?agenceId=<idA>&marque=Toyota", "T1,T2", "vehicles"),
                Arguments.of("agence/is-null", "F1,H1", "vehicles"),
                Arguments.of("agence/is-not-null", "T1,T2,T3,R1,B1,K1", "vehicles"),
                Arguments.of("agence/count?agenceId=<idA>", "3", "long"),
                Arguments.of("agence/exists?agenceId=<idA>", "true", "boolean"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void all29RoutesReachTheCorrectServiceQuery(String path, String expected, String type)
            throws Exception {
        JsonNode result =
                json(
                        200,
                        "GET",
                        "/vehicule/keywords/" + path.replace("<idA>", Long.toString(agencyA)),
                        null);
        if (type.equals("boolean")) {
            assertEquals(Boolean.parseBoolean(expected), result.asBoolean());
            return;
        }
        if (type.equals("long")) {
            assertEquals(Long.parseLong(expected), result.asLong());
            return;
        }
        assertTrue(result.isArray());
        String[] values = expected.split(",");
        assertEquals(values.length, result.size());
        if (type.equals("prices")) {
            for (int i = 0; i < values.length; i++)
                assertEquals(
                        0,
                        new BigDecimal(result.get(i).path("tarifJournalier").toString())
                                .compareTo(new BigDecimal(values[i])));
        } else {
            Set<String> actual = new TreeSet<>();
            for (JsonNode v : result) actual.add(v.path("immatriculation").asString());
            assertEquals(new TreeSet<>(List.of(values)), actual);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "marque?marque=",
                "marque",
                "prix/greater-than?prix=-1",
                "prix/between?minimum=100&maximum=50",
                "prix/between?minimum=bad&maximum=100",
                "marques/in?marques=",
                "marques/not-in?marques=",
                "statut?statut=AVAILABLE",
                "agence?agenceId=0",
                "agence?agenceId=abc"
            })
    void invalidParametersReturn400(String path) throws Exception {
        assertEquals(
                400, json(400, "GET", "/vehicule/keywords/" + path, null).path("status").asInt());
    }

    @Test
    void swaggerDocumentsAll29RoutesAndExistingCrudStillWorks() throws Exception {
        JsonNode paths = json(200, "GET", "/v3/api-docs", null).path("paths");
        long keywordPaths = 0;
        for (String name : paths.propertyNames())
            if (name.startsWith("/vehicule/keywords/")) keywordPaths++;
        assertEquals(29, keywordPaths);
        assertTrue(paths.has("/vehicule/getAll"));
        assertTrue(paths.has("/vehicule/{id}"));
        assertEquals(8, json(200, "GET", "/vehicule/getAll", null).size());
        expect(200, "GET", "/swagger-ui/index.html", null);
    }

    @Test
    void absentAgencyAndNoMatchesReturnEmptyListZeroAndFalse() throws Exception {
        assertEquals(
                0,
                json(200, "GET", "/vehicule/keywords/agence?agenceId=9223372036854775806", null)
                        .size());
        assertEquals(
                0,
                json(
                                200,
                                "GET",
                                "/vehicule/keywords/agence/count?agenceId=9223372036854775806",
                                null)
                        .asLong());
        assertFalse(
                json(
                                200,
                                "GET",
                                "/vehicule/keywords/agence/exists?agenceId=9223372036854775806",
                                null)
                        .asBoolean());
        assertEquals(0, json(200, "GET", "/vehicule/keywords/marque?marque=Absent", null).size());
    }

    private long createAgency(String name, String city) throws Exception {
        return json(201, "POST", "/agence", "{\"nom\":\"" + name + "\",\"ville\":\"" + city + "\"}")
                .path("idAgence")
                .asLong();
    }

    private void seedVehicle(
            String registration,
            String brand,
            String model,
            String price,
            String status,
            Long agency)
            throws Exception {
        String body =
                "{\"immatriculation\":\""
                        + registration
                        + "\",\"marque\":\""
                        + brand
                        + "\",\"modele\":\""
                        + model
                        + "\",\"categorie\":\"CITADINE\",\"tarifJournalier\":"
                        + price
                        + ",\"statut\":\""
                        + status
                        + "\""
                        + (agency == null ? "" : ",\"agence\":{\"idAgence\":" + agency + "}}");
        if (agency == null) body += "}";
        json(201, "POST", "/vehicule", body);
    }

    private JsonNode json(int status, String method, String path, String body) throws Exception {
        return mapper.readTree(expect(status, method, path, body).body());
    }

    private HttpResponse<String> expect(int status, String method, String path, String body)
            throws Exception {
        HttpRequest request =
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .timeout(Duration.ofSeconds(30))
                        .header("Content-Type", "application/json")
                        .method(
                                method,
                                body == null
                                        ? HttpRequest.BodyPublishers.noBody()
                                        : HttpRequest.BodyPublishers.ofString(body))
                        .build();
        var response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(
                status, response.statusCode(), () -> method + " " + path + ": " + response.body());
        return response;
    }
}
