package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.repository.AgenceRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class AgenceServiceIMPL implements IAgenceService {
    AgenceRepository repository;

    @Override
    public Agence create(Agence agence) {
        return repository.save(agence);
    }

    @Override
    public Agence getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Agence> getAll() {
        return repository.findAll();
    }

    @Override
    public Agence update(Agence agence) {
        return repository.save(agence);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
