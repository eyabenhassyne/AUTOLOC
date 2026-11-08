package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Maintenance;

import java.util.List;

public interface IMaintenanceService {
    Maintenance create(Maintenance maintenance);

    Maintenance getById(Long id);

    List<Maintenance> getAll();

    Maintenance update(Maintenance maintenance);

    void delete(Long id);
}
