package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    // 1. Marque exacte
    List<Vehicule> findByMarque(String marque);

    // 2. Marque contenant un texte
    List<Vehicule> findByMarqueContaining(String value);

    // 3. Marque commençant par un texte
    List<Vehicule> findByMarqueStartingWith(String value);

    // 4. Marque finissant par un texte
    List<Vehicule> findByMarqueEndingWith(String value);

    // 5. Marque sans tenir compte de la casse
    List<Vehicule> findByMarqueIgnoreCase(String marque);

    // 6. Modèle exact
    List<Vehicule> findByModele(String modele);

    // 7. Vérifier une immatriculation
    boolean existsByImmatriculation(String immatriculation);

    // 8. Compter par marque
    long countByMarque(String marque);

    // 9. Tarif strictement supérieur
    List<Vehicule> findByTarifJournalierGreaterThan(BigDecimal prix);

    // 10. Tarif strictement inférieur
    List<Vehicule> findByTarifJournalierLessThan(BigDecimal prix);

    // 11. Tarif supérieur ou égal
    List<Vehicule> findByTarifJournalierGreaterThanEqual(BigDecimal prix);

    // 12. Tarif entre deux bornes incluses
    List<Vehicule> findByTarifJournalierBetween(BigDecimal minimum, BigDecimal maximum);

    // 13. Tous les tarifs croissants
    List<Vehicule> findAllByOrderByTarifJournalierAsc();

    // 14. Tous les tarifs décroissants
    List<Vehicule> findAllByOrderByTarifJournalierDesc();

    // 15. Marque puis tarif croissant
    List<Vehicule> findByMarqueOrderByTarifJournalierAsc(String marque);

    // 16. Marque ET tarif supérieur
    List<Vehicule> findByMarqueAndTarifJournalierGreaterThan(String marque, BigDecimal prix);

    // 17. Marque OU modèle
    List<Vehicule> findByMarqueOrModele(String marque, String modele);

    // 18. Marques dans une liste
    List<Vehicule> findByMarqueIn(List<String> marques);

    // 19. Marques hors de la liste
    List<Vehicule> findByMarqueNotIn(List<String> marques);

    // 20. Filtrer par statut
    List<Vehicule> findByStatut(StatutVehicule statut);

    // 21. Disponibles au tarif croissant
    List<Vehicule> findByStatutOrderByTarifJournalierAsc(StatutVehicule statut);

    // 22. Véhicules d’une agence
    List<Vehicule> findByAgence_IdAgence(Long agenceId);

    // 23. Agences dans une ville
    List<Vehicule> findByAgence_Ville(String ville);

    // 24. Nom d’agence contenant un texte
    List<Vehicule> findByAgence_NomContaining(String value);

    // 25. Agence ET marque
    List<Vehicule> findByAgence_IdAgenceAndMarque(Long agenceId, String marque);

    // 26. Sans agence
    List<Vehicule> findByAgenceIsNull();

    // 27. Avec agence
    List<Vehicule> findByAgenceIsNotNull();

    // 28. Compter les véhicules d’une agence
    long countByAgence_IdAgence(Long agenceId);

    // 29. L’agence possède-t-elle un véhicule ?
    boolean existsByAgence_IdAgence(Long agenceId);
}
