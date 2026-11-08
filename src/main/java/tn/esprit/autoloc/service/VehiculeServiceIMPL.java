package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.VehiculeRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class VehiculeServiceIMPL implements IVehiculeService {
    VehiculeRepository repository;

    @Override
    public Vehicule create(Vehicule vehicule) {
        return repository.save(vehicule);
    }

    @Override
    public Vehicule getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Vehicule> getAll() {
        return repository.findAll();
    }

    @Override
    public Vehicule update(Vehicule vehicule) {
        return repository.save(vehicule);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
