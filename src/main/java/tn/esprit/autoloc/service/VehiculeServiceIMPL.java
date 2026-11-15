package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.repository.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class VehiculeServiceIMPL implements IVehiculeService {
    private final VehiculeRepository repository;
    private final EntitySupport support;

    @Override
    public Vehicule create(Vehicule input) {
        support.checkNew(input);
        support.prepare(input);

        return repository.save(input);
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicule getById(Long id) {
        return support.require(Vehicule.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> getAll() {
        return repository.findAll();
    }

    @Override
    public Vehicule update(Vehicule input) {
        Vehicule existing = getById(input.getIdVehicule());
        support.prepare(input);
        existing.setImmatriculation(input.getImmatriculation());
        existing.setMarque(input.getMarque());
        existing.setModele(input.getModele());
        existing.setCategorie(input.getCategorie());
        existing.setTarifJournalier(input.getTarifJournalier());
        existing.setStatut(input.getStatut());
        if (input.getAgence() != null) existing.setAgence(input.getAgence());

        if (input.getEquipements() != null && !input.getEquipements().isEmpty())
            existing.setEquipements(input.getEquipements());
        return existing;
    }

    @Override
    public void delete(Long id) {
        repository.delete(getById(id));
        repository.flush();
    }

    @Override
    public Vehicule assignEquipement(Long vehiculeId, Long equipementId) {
        Vehicule vehicle = support.lock(Vehicule.class, vehiculeId);
        Equipement equipment = support.require(Equipement.class, equipementId);
        if (vehicle.getEquipements().stream()
                .noneMatch(e -> e.getIdEquipement().equals(equipementId)))
            vehicle.getEquipements().add(equipment);
        return vehicle;
    }

    @Override
    public Vehicule removeEquipement(Long vehiculeId, Long equipementId) {
        Vehicule vehicle = support.lock(Vehicule.class, vehiculeId);
        support.require(Equipement.class, equipementId);
        vehicle.getEquipements().removeIf(e -> e.getIdEquipement().equals(equipementId));
        return vehicle;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByMarque(String marque) {
        requireText(marque);
        return repository.findByMarque(marque);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByMarqueContaining(String value) {
        requireText(value);
        return repository.findByMarqueContaining(value);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByMarqueStartingWith(String value) {
        requireText(value);
        return repository.findByMarqueStartingWith(value);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByMarqueEndingWith(String value) {
        requireText(value);
        return repository.findByMarqueEndingWith(value);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByMarqueIgnoreCase(String marque) {
        requireText(marque);
        return repository.findByMarqueIgnoreCase(marque);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByModele(String modele) {
        requireText(modele);
        return repository.findByModele(modele);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByImmatriculation(String immatriculation) {
        requireText(immatriculation);
        return repository.existsByImmatriculation(immatriculation);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByMarque(String marque) {
        requireText(marque);
        return repository.countByMarque(marque);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByTarifJournalierGreaterThan(BigDecimal prix) {
        requirePrice(prix);
        return repository.findByTarifJournalierGreaterThan(prix);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByTarifJournalierLessThan(BigDecimal prix) {
        requirePrice(prix);
        return repository.findByTarifJournalierLessThan(prix);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByTarifJournalierGreaterThanEqual(BigDecimal prix) {
        requirePrice(prix);
        return repository.findByTarifJournalierGreaterThanEqual(prix);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByTarifJournalierBetween(BigDecimal minimum, BigDecimal maximum) {
        requirePrice(minimum);
        requirePrice(maximum);
        if (minimum.compareTo(maximum) > 0)
            throw new IllegalArgumentException("Minimum must not exceed maximum");
        return repository.findByTarifJournalierBetween(minimum, maximum);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findAllByOrderByTarifJournalierAsc() {
        return repository.findAllByOrderByTarifJournalierAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findAllByOrderByTarifJournalierDesc() {
        return repository.findAllByOrderByTarifJournalierDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByMarqueOrderByTarifJournalierAsc(String marque) {
        requireText(marque);
        return repository.findByMarqueOrderByTarifJournalierAsc(marque);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByMarqueAndTarifJournalierGreaterThan(
            String marque, BigDecimal prix) {
        requireText(marque);
        requirePrice(prix);
        return repository.findByMarqueAndTarifJournalierGreaterThan(marque, prix);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByMarqueOrModele(String marque, String modele) {
        requireText(marque);
        requireText(modele);
        return repository.findByMarqueOrModele(marque, modele);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByMarqueIn(List<String> marques) {
        if (marques == null || marques.isEmpty())
            throw new IllegalArgumentException("At least one brand is required");
        marques.forEach(this::requireText);
        return repository.findByMarqueIn(marques);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByMarqueNotIn(List<String> marques) {
        if (marques == null || marques.isEmpty())
            throw new IllegalArgumentException("At least one brand is required");
        marques.forEach(this::requireText);
        return repository.findByMarqueNotIn(marques);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByStatut(StatutVehicule statut) {
        if (statut == null) throw new IllegalArgumentException("A status is required");
        return repository.findByStatut(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findAvailableOrderByTarifJournalierAsc() {
        return repository.findByStatutOrderByTarifJournalierAsc(StatutVehicule.DISPONIBLE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByAgence_IdAgence(Long agenceId) {
        support.positiveId(agenceId);
        return repository.findByAgence_IdAgence(agenceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByAgence_Ville(String ville) {
        requireText(ville);
        return repository.findByAgence_Ville(ville);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByAgence_NomContaining(String value) {
        requireText(value);
        return repository.findByAgence_NomContaining(value);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByAgence_IdAgenceAndMarque(Long agenceId, String marque) {
        support.positiveId(agenceId);
        requireText(marque);
        return repository.findByAgence_IdAgenceAndMarque(agenceId, marque);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByAgenceIsNull() {
        return repository.findByAgenceIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findByAgenceIsNotNull() {
        return repository.findByAgenceIsNotNull();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByAgence_IdAgence(Long agenceId) {
        support.positiveId(agenceId);
        return repository.countByAgence_IdAgence(agenceId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAgence_IdAgence(Long agenceId) {
        support.positiveId(agenceId);
        return repository.existsByAgence_IdAgence(agenceId);
    }

    private void requireText(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Search text must not be blank");
    }

    private void requirePrice(BigDecimal value) {
        if (value == null || value.signum() < 0)
            throw new IllegalArgumentException("Search price must be nonnegative");
    }
}
