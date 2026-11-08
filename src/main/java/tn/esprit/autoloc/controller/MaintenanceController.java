package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.service.IMaintenanceService;

import java.util.List;

@Tag(name = "Gestion des maintenances")
@RestController
@AllArgsConstructor
@RequestMapping("/maintenance")
public class MaintenanceController {

    IMaintenanceService service;

    @Operation(description = "Cette méthode permet d'ajouter une maintenance")
    @PostMapping("/create")
    public Maintenance create(@RequestBody Maintenance maintenance) {
        return service.create(maintenance);
    }

    @Operation(description = "Cette méthode permet d'obtenir une maintenance par son identifiant")
    @GetMapping("/get/{id}")
    public Maintenance getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(description = "Cette méthode permet de lister toutes les maintenances")
    @GetMapping("/getAll")
    public List<Maintenance> getAll() {
        return service.getAll();
    }

    @Operation(description = "Cette méthode permet de mettre à jour une maintenance")
    @PutMapping("/update")
    public Maintenance update(@RequestBody Maintenance maintenance) {
        return service.update(maintenance);
    }

    @Operation(description = "Cette méthode permet de supprimer une maintenance par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
