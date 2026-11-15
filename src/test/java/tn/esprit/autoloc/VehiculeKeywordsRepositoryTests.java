package tn.esprit.autoloc;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.repository.*;

import java.math.BigDecimal;
import java.util.*;

@DataJpaTest(
        properties =
                "spring.datasource.url=jdbc:h2:mem:autoloc-keywords;MODE=MySQL;DB_CLOSE_DELAY=-1")
@ActiveProfiles("test")
class VehiculeKeywordsRepositoryTests {

    @Autowired VehiculeRepository repository;
    @Autowired AgenceRepository agencies;
    @PersistenceContext EntityManager entityManager;
    private Agence agencyA, agencyB, emptyAgency;

    @BeforeEach
    void seed() {
        agencyA = agency("AutoLoc Centre", "Tunis");
        agencyB = agency("AutoLoc Sud", "Sfax");
        Agence agencyC = agency("Luxe", "Tunis");
        emptyAgency = agency("Agence Vide", "Bizerte");
        vehicle("T1", "Toyota", "Yaris", "50", StatutVehicule.DISPONIBLE, agencyA);
        vehicle("T2", "Toyota", "Corolla", "100", StatutVehicule.LOUE, agencyA);
        vehicle("T3", "TOYOTA", "Yaris", "75", StatutVehicule.DISPONIBLE, agencyB);
        vehicle("R1", "Renault", "Clio", "80", StatutVehicule.DISPONIBLE, agencyB);
        vehicle("F1", "Ford", "Focus", "100", StatutVehicule.MAINTENANCE, null);
        vehicle("B1", "BMW", "Yaris", "150", StatutVehicule.DISPONIBLE, agencyC);
        vehicle("H1", "Hyundai", "i20", "120", StatutVehicule.LOUE, null);
        vehicle("K1", "Kia", "Rio", "80", StatutVehicule.DISPONIBLE, agencyA);
        repository.flush();
        entityManager.clear();
    }

    private Agence agency(String name, String city) {
        Agence a = new Agence();
        a.setNom(name);
        a.setVille(city);
        return agencies.saveAndFlush(a);
    }

    private void vehicle(
            String registration,
            String brand,
            String model,
            String price,
            StatutVehicule status,
            Agence agency) {
        Vehicule v = new Vehicule();
        v.setImmatriculation(registration);
        v.setMarque(brand);
        v.setModele(model);
        v.setCategorie(CategorieVehicule.CITADINE);
        v.setTarifJournalier(money(price));
        v.setStatut(status);
        v.setAgence(agency);
        repository.saveAndFlush(v);
    }

    private static BigDecimal money(String value) {
        return new BigDecimal(value);
    }

    private static void assertIds(List<Vehicule> actual, String... expected) {
        assertEquals(expected.length, actual.size(), "No omitted or duplicate vehicles");
        assertEquals(
                new TreeSet<>(List.of(expected)),
                new TreeSet<>(actual.stream().map(Vehicule::getImmatriculation).toList()));
    }

    private static void assertPrices(List<Vehicule> actual, String... expected) {
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++)
            assertEquals(
                    0,
                    actual.get(i).getTarifJournalier().compareTo(money(expected[i])),
                    "price at index " + i);
    }

    @Test
    void q01() {
        assertIds(repository.findByMarque("Toyota"), "T1", "T2");
    }

    @Test
    void q02() {
        assertIds(repository.findByMarqueContaining("oy"), "T1", "T2");
    }

    @Test
    void q03() {
        assertIds(repository.findByMarqueStartingWith("Toy"), "T1", "T2");
    }

    @Test
    void q04() {
        assertIds(repository.findByMarqueEndingWith("ota"), "T1", "T2");
    }

    @Test
    void q05() {
        assertIds(repository.findByMarqueIgnoreCase("toyota"), "T1", "T2", "T3");
    }

    @Test
    void q06() {
        assertIds(repository.findByModele("Yaris"), "T1", "T3", "B1");
    }

    @Test
    void q07() {
        assertTrue(repository.existsByImmatriculation("T1"));
        assertFalse(repository.existsByImmatriculation("ABSENT"));
    }

    @Test
    void q08() {
        assertEquals(2, repository.countByMarque("Toyota"));
        assertEquals(0, repository.countByMarque("Absent"));
    }

    @Test
    void q09() {
        assertIds(repository.findByTarifJournalierGreaterThan(money("100")), "H1", "B1");
    }

    @Test
    void q10() {
        assertIds(repository.findByTarifJournalierLessThan(money("80")), "T1", "T3");
    }

    @Test
    void q11() {
        assertIds(
                repository.findByTarifJournalierGreaterThanEqual(money("100")),
                "T2",
                "F1",
                "H1",
                "B1");
    }

    @Test
    void q12() {
        assertIds(
                repository.findByTarifJournalierBetween(money("75"), money("100")),
                "T3",
                "R1",
                "K1",
                "T2",
                "F1");
    }

    @Test
    void q13() {
        assertPrices(
                repository.findAllByOrderByTarifJournalierAsc(),
                "50",
                "75",
                "80",
                "80",
                "100",
                "100",
                "120",
                "150");
    }

    @Test
    void q14() {
        assertPrices(
                repository.findAllByOrderByTarifJournalierDesc(),
                "150",
                "120",
                "100",
                "100",
                "80",
                "80",
                "75",
                "50");
    }

    @Test
    void q15() {
        assertIds(repository.findByMarqueOrderByTarifJournalierAsc("Toyota"), "T1", "T2");
        assertPrices(repository.findByMarqueOrderByTarifJournalierAsc("Toyota"), "50", "100");
    }

    @Test
    void q16() {
        assertIds(
                repository.findByMarqueAndTarifJournalierGreaterThan("Toyota", money("50")), "T2");
    }

    @Test
    void q17() {
        assertIds(repository.findByMarqueOrModele("Toyota", "Yaris"), "T1", "T2", "T3", "B1");
    }

    @Test
    void q18() {
        assertIds(repository.findByMarqueIn(List.of("Toyota", "Ford")), "T1", "T2", "F1");
    }

    @Test
    void q19() {
        assertIds(
                repository.findByMarqueNotIn(List.of("Toyota", "Ford")),
                "T3",
                "R1",
                "B1",
                "H1",
                "K1");
    }

    @Test
    void q20() {
        assertIds(repository.findByStatut(StatutVehicule.DISPONIBLE), "T1", "T3", "R1", "B1", "K1");
        assertIds(repository.findByStatut(StatutVehicule.LOUE), "T2", "H1");
        assertIds(repository.findByStatut(StatutVehicule.MAINTENANCE), "F1");
    }

    @Test
    void q21() {
        assertIds(
                repository.findByStatutOrderByTarifJournalierAsc(StatutVehicule.DISPONIBLE),
                "T1",
                "T3",
                "R1",
                "K1",
                "B1");
        assertPrices(
                repository.findByStatutOrderByTarifJournalierAsc(StatutVehicule.DISPONIBLE),
                "50",
                "75",
                "80",
                "80",
                "150");
    }

    @Test
    void q22() {
        assertIds(repository.findByAgence_IdAgence(agencyA.getIdAgence()), "T1", "T2", "K1");
        assertIds(repository.findByAgence_IdAgence(emptyAgency.getIdAgence()));
    }

    @Test
    void q23() {
        assertIds(repository.findByAgence_Ville("Tunis"), "T1", "T2", "K1", "B1");
    }

    @Test
    void q24() {
        assertIds(repository.findByAgence_NomContaining("AutoLoc"), "T1", "T2", "T3", "R1", "K1");
    }

    @Test
    void q25() {
        assertIds(
                repository.findByAgence_IdAgenceAndMarque(agencyA.getIdAgence(), "Toyota"),
                "T1",
                "T2");
        assertIds(repository.findByAgence_IdAgenceAndMarque(agencyB.getIdAgence(), "Toyota"));
    }

    @Test
    void q26() {
        assertIds(repository.findByAgenceIsNull(), "F1", "H1");
    }

    @Test
    void q27() {
        assertIds(repository.findByAgenceIsNotNull(), "T1", "T2", "T3", "R1", "B1", "K1");
    }

    @Test
    void q28() {
        assertEquals(3, repository.countByAgence_IdAgence(agencyA.getIdAgence()));
        assertEquals(0, repository.countByAgence_IdAgence(emptyAgency.getIdAgence()));
    }

    @Test
    void q29() {
        assertTrue(repository.existsByAgence_IdAgence(agencyA.getIdAgence()));
        assertFalse(repository.existsByAgence_IdAgence(emptyAgency.getIdAgence()));
    }

    @Test
    void emptyMatchesAreListsNotNullAndCountsAreZero() {
        assertIds(repository.findByMarque("Absent"));
        assertIds(repository.findByMarqueContaining("xyz"));
        assertIds(repository.findByMarqueStartingWith("xyz"));
        assertIds(repository.findByMarqueEndingWith("xyz"));
        assertIds(repository.findByMarqueIgnoreCase("xyz"));
        assertIds(repository.findByModele("Absent"));
        assertIds(repository.findByTarifJournalierGreaterThan(money("999")));
        assertIds(repository.findByTarifJournalierLessThan(money("0")));
        assertIds(repository.findByTarifJournalierGreaterThanEqual(money("999")));
        assertIds(repository.findByTarifJournalierBetween(money("200"), money("300")));
        assertIds(repository.findByMarqueOrderByTarifJournalierAsc("Absent"));
        assertIds(repository.findByMarqueAndTarifJournalierGreaterThan("Toyota", money("999")));
        assertIds(repository.findByMarqueOrModele("Absent", "Absent"));
        assertIds(repository.findByMarqueIn(List.of("Absent")));
        assertEquals(8, repository.findByMarqueNotIn(List.of("Absent")).size());
        assertIds(repository.findByAgence_Ville("Paris"));
        assertIds(repository.findByAgence_NomContaining("Absent"));
        assertIds(repository.findByAgence_IdAgence(Long.MAX_VALUE));
        assertEquals(0, repository.countByAgence_IdAgence(Long.MAX_VALUE));
        assertFalse(repository.existsByAgence_IdAgence(Long.MAX_VALUE));
    }

    @Test
    void betweenIncludesEqualBounds() {
        assertIds(
                repository.findByTarifJournalierBetween(money("80.00"), money("80.00")),
                "R1",
                "K1");
    }

    @Test
    void partialMatchingEscapesPercentAndUnderscore() {
        vehicle("S1", "100%_Auto", "Special", "60", StatutVehicule.DISPONIBLE, null);
        assertIds(repository.findByMarqueContaining("%_"), "S1");
        assertIds(repository.findByMarqueStartingWith("100%"), "S1");
        assertIds(repository.findByMarqueEndingWith("_Auto"), "S1");
    }

    @Test
    void queriesAlsoHandleAnEmptyDatabase() {
        repository.deleteAll();
        repository.flush();
        entityManager.clear();
        assertIds(repository.findAllByOrderByTarifJournalierAsc());
        assertIds(repository.findAllByOrderByTarifJournalierDesc());
        assertIds(repository.findByStatut(StatutVehicule.DISPONIBLE));
        assertIds(repository.findByStatutOrderByTarifJournalierAsc(StatutVehicule.DISPONIBLE));
        assertIds(repository.findByAgenceIsNull());
        assertIds(repository.findByAgenceIsNotNull());
        assertEquals(0, repository.countByMarque("Toyota"));
        assertFalse(repository.existsByImmatriculation("T1"));
    }
}
