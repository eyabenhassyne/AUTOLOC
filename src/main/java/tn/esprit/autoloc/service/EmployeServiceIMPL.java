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
public class EmployeServiceIMPL implements IEmployeService {
    private final EmployeRepository repository;
    private final EntitySupport support;

    @Override
    public Employe create(Employe input) {
        support.checkNew(input);
        support.prepare(input);

        return repository.save(input);
    }

    @Override
    @Transactional(readOnly = true)
    public Employe getById(Long id) {
        return support.require(Employe.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employe> getAll() {
        return repository.findAll();
    }

    @Override
    public Employe update(Employe input) {
        Employe existing = getById(input.getIdEmploye());
        support.prepare(input);
        existing.setNom(input.getNom());
        existing.setPrenom(input.getPrenom());
        existing.setRole(input.getRole());
        if (input.getAgence() != null) existing.setAgence(input.getAgence());

        return existing;
    }

    @Override
    public void delete(Long id) {
        repository.delete(getById(id));
        repository.flush();
    }

    @Override
    public Employe assignAgence(Long employeId, Long agenceId) {
        Employe employee = getById(employeId);
        employee.setAgence(support.require(Agence.class, agenceId));
        return employee;
    }
}
