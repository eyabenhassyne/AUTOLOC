package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Equipement;

import java.util.List;

public interface IEquipementService {
    Equipement create(Equipement equipement);

    Equipement getById(Long id);

    List<Equipement> getAll();

    Equipement update(Equipement equipement);

    void delete(Long id);
}
