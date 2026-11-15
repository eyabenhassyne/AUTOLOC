package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Agence;

import java.util.List;

public interface IAgenceService {
    Agence create(Agence agence);

    Agence getById(Long id);

    List<Agence> getAll();

    Agence update(Agence agence);

    void delete(Long id);
}
