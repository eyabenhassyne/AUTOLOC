package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Contrat;

import java.util.List;

public interface IContratService {
    Contrat create(Contrat contrat);

    Contrat getById(Long id);

    List<Contrat> getAll();

    Contrat update(Contrat contrat);

    void delete(Long id);
}
