package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Employe;

import java.util.List;

public interface IEmployeService {
    Employe create(Employe employe);

    Employe getById(Long id);

    List<Employe> getAll();

    Employe update(Employe employe);

    void delete(Long id);
}
