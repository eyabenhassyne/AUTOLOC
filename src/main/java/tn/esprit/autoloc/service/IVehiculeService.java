package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;
import java.util.List;

public interface IVehiculeService {
    Vehicule create(Vehicule vehicule);

    Vehicule getById(Long id);

    List<Vehicule> getAll();

    Vehicule update(Vehicule vehicule);

    void delete(Long id);

    Vehicule assignEquipement(Long vehiculeId, Long equipementId);

    Vehicule removeEquipement(Long vehiculeId, Long equipementId);

    List<Vehicule> findByMarque(String marque);

    List<Vehicule> findByMarqueContaining(String value);

    List<Vehicule> findByMarqueStartingWith(String value);

    List<Vehicule> findByMarqueEndingWith(String value);

    List<Vehicule> findByMarqueIgnoreCase(String marque);

    List<Vehicule> findByModele(String modele);

    boolean existsByImmatriculation(String immatriculation);

    long countByMarque(String marque);

    List<Vehicule> findByTarifJournalierGreaterThan(BigDecimal prix);

    List<Vehicule> findByTarifJournalierLessThan(BigDecimal prix);

    List<Vehicule> findByTarifJournalierGreaterThanEqual(BigDecimal prix);

    List<Vehicule> findByTarifJournalierBetween(BigDecimal minimum, BigDecimal maximum);

    List<Vehicule> findAllByOrderByTarifJournalierAsc();

    List<Vehicule> findAllByOrderByTarifJournalierDesc();

    List<Vehicule> findByMarqueOrderByTarifJournalierAsc(String marque);

    List<Vehicule> findByMarqueAndTarifJournalierGreaterThan(String marque, BigDecimal prix);

    List<Vehicule> findByMarqueOrModele(String marque, String modele);

    List<Vehicule> findByMarqueIn(List<String> marques);

    List<Vehicule> findByMarqueNotIn(List<String> marques);

    List<Vehicule> findByStatut(StatutVehicule statut);

    List<Vehicule> findAvailableOrderByTarifJournalierAsc();

    List<Vehicule> findByAgence_IdAgence(Long agenceId);

    List<Vehicule> findByAgence_Ville(String ville);

    List<Vehicule> findByAgence_NomContaining(String value);

    List<Vehicule> findByAgence_IdAgenceAndMarque(Long agenceId, String marque);

    List<Vehicule> findByAgenceIsNull();

    List<Vehicule> findByAgenceIsNotNull();

    long countByAgence_IdAgence(Long agenceId);

    boolean existsByAgence_IdAgence(Long agenceId);
}
