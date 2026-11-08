package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.repository.PaiementRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class PaiementServiceIMPL implements IPaiementService {
    PaiementRepository repository;

    @Override
    public Paiement create(Paiement paiement) {
        return repository.save(paiement);
    }

    @Override
    public Paiement getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Paiement> getAll() {
        return repository.findAll();
    }

    @Override
    public Paiement update(Paiement paiement) {
        return repository.save(paiement);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
