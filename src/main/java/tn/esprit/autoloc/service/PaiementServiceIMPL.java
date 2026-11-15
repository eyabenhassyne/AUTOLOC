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
public class PaiementServiceIMPL implements IPaiementService {
    private final PaiementRepository repository;
    private final EntitySupport support;

    @Override
    public Paiement create(Paiement input) {
        support.checkNew(input);
        support.prepare(input);

        return repository.save(input);
    }

    @Override
    @Transactional(readOnly = true)
    public Paiement getById(Long id) {
        return support.require(Paiement.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paiement> getAll() {
        return repository.findAll();
    }

    @Override
    public Paiement update(Paiement input) {
        Paiement existing = getById(input.getIdPaiement());
        support.prepare(input);
        existing.setMontant(input.getMontant());
        existing.setDatePaiement(input.getDatePaiement());
        existing.setModePaiement(input.getModePaiement());
        if (input.getContrat() != null) existing.setContrat(input.getContrat());

        return existing;
    }

    @Override
    public void delete(Long id) {
        repository.delete(getById(id));
        repository.flush();
    }
}
