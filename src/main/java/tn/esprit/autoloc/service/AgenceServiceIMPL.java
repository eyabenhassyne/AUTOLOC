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
public class AgenceServiceIMPL implements IAgenceService {
    private final AgenceRepository repository;
    private final EntitySupport support;

    @Override
    public Agence create(Agence input) {
        support.checkNew(input);
        support.prepare(input);

        return repository.save(input);
    }

    @Override
    @Transactional(readOnly = true)
    public Agence getById(Long id) {
        return support.require(Agence.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agence> getAll() {
        return repository.findAll();
    }

    @Override
    public Agence update(Agence input) {
        Agence existing = getById(input.getIdAgence());
        support.prepare(input);
        existing.setNom(input.getNom());
        existing.setVille(input.getVille());
        existing.setAdresse(input.getAdresse());
        existing.setTelephone(input.getTelephone());

        return existing;
    }

    @Override
    public void delete(Long id) {
        repository.delete(getById(id));
        repository.flush();
    }
}
