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
public class EquipementServiceIMPL implements IEquipementService {
    private final EquipementRepository repository;
    private final EntitySupport support;

    @Override
    public Equipement create(Equipement input) {
        support.checkNew(input);
        support.prepare(input);

        return repository.save(input);
    }

    @Override
    @Transactional(readOnly = true)
    public Equipement getById(Long id) {
        return support.require(Equipement.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipement> getAll() {
        return repository.findAll();
    }

    @Override
    public Equipement update(Equipement input) {
        Equipement existing = getById(input.getIdEquipement());
        support.prepare(input);
        existing.setLibelle(input.getLibelle());

        return existing;
    }

    @Override
    public void delete(Long id) {
        repository.delete(getById(id));
        repository.flush();
    }
}
