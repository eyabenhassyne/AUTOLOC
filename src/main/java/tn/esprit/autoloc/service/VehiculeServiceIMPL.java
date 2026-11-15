package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.repository.*;

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
}
