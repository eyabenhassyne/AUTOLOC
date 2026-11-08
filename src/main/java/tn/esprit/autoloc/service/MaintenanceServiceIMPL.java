package tn.esprit.autoloc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.repository.MaintenanceRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class MaintenanceServiceIMPL implements IMaintenanceService {
    MaintenanceRepository repository;

    @Override
    public Maintenance create(Maintenance maintenance) {
        return repository.save(maintenance);
    }

    @Override
    public Maintenance getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Maintenance> getAll() {
        return repository.findAll();
    }

    @Override
    public Maintenance update(Maintenance maintenance) {
        return repository.save(maintenance);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
