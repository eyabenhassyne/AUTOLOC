package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.service.IMaintenanceService;

import java.net.URI;
import java.util.List;

@Tag(name = "Gestion des maintenances")
@RestController
@AllArgsConstructor
@RequestMapping("/maintenance")
public class MaintenanceController {

    IMaintenanceService service;

    @Operation(description = "Cette méthode permet d'ajouter une maintenance")
    @PostMapping("/create")
    public Maintenance create(@Valid @RequestBody Maintenance maintenance) {
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
    public Maintenance update(@Valid @RequestBody Maintenance maintenance) {
        return service.update(maintenance);
    }

    @Operation(
            description = "Cette méthode permet de supprimer une maintenance par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Create Maintenance",
            description = "Returns 201 and Location. New records must not contain an ID.")
    public ResponseEntity<Maintenance> createResource(@Valid @RequestBody Maintenance input) {
        Maintenance saved = service.create(input);
        return ResponseEntity.created(URI.create("/maintenance/" + saved.getIdMaintenance()))
                .body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Maintenance; returns 404 when absent")
    public Maintenance getResource(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "List Maintenance records")
    public List<Maintenance> listResources() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Maintenance")
    public Maintenance updateResource(
            @PathVariable Long id, @Valid @RequestBody Maintenance input) {
        if (input.getIdMaintenance() != null && !id.equals(input.getIdMaintenance()))
            throw new IllegalArgumentException("Path and body IDs must match");
        input.setIdMaintenance(id);
        return service.update(input);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Maintenance; returns 409 if dependent records prevent deletion")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{maintenanceId}/vehicule/{vehiculeId}")
    @Operation(summary = "Assign existing maintenance to an existing vehicle")
    public Maintenance assignVehicule(
            @PathVariable Long maintenanceId, @PathVariable Long vehiculeId) {
        return service.assignVehicule(maintenanceId, vehiculeId);
    }
}
