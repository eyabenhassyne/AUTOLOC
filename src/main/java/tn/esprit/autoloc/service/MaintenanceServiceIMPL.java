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
public class MaintenanceServiceIMPL implements IMaintenanceService {
    private final MaintenanceRepository repository;
    private final EntitySupport support;

    @Override
    public Maintenance create(Maintenance input) {
        support.checkNew(input);
        support.prepare(input);

        return repository.save(input);
    }

    @Override
    @Transactional(readOnly = true)
    public Maintenance getById(Long id) {
        return support.require(Maintenance.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> getAll() {
        return repository.findAll();
    }

    @Override
    public Maintenance update(Maintenance input) {
        Maintenance existing = getById(input.getIdMaintenance());
        support.prepare(input);
        existing.setDateDebut(input.getDateDebut());
        existing.setDateFin(input.getDateFin());
        existing.setDescription(input.getDescription());
        if (input.getVehicule() != null) existing.setVehicule(input.getVehicule());

        return existing;
    }

    @Override
    public void delete(Long id) {
        repository.delete(getById(id));
        repository.flush();
    }

    @Override
    public Maintenance assignVehicule(Long maintenanceId, Long vehiculeId) {
        Maintenance maintenance = getById(maintenanceId);
        maintenance.setVehicule(support.require(Vehicule.class, vehiculeId));
        return maintenance;
    }
}
