package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Vehicule;

import java.util.List;

public interface IVehiculeService {
    Vehicule create(Vehicule vehicule);

    Vehicule getById(Long id);

    List<Vehicule> getAll();

    Vehicule update(Vehicule vehicule);

    void delete(Long id);

    Vehicule assignEquipement(Long vehiculeId, Long equipementId);

    Vehicule removeEquipement(Long vehiculeId, Long equipementId);
}
