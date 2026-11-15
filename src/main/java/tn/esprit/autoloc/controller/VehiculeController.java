package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IVehiculeService;

import java.net.URI;
import java.util.List;

@Tag(name = "Gestion des véhicules")
@RestController
@AllArgsConstructor
@RequestMapping("/vehicule")
public class VehiculeController {

    IVehiculeService service;

    @Operation(description = "Cette méthode permet d'ajouter un véhicule")
    @PostMapping("/create")
    public Vehicule create(@Valid @RequestBody Vehicule vehicule) {
        return service.create(vehicule);
    }

    @Operation(description = "Cette méthode permet d'obtenir un véhicule par son identifiant")
    @GetMapping("/get/{id}")
    public Vehicule getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(description = "Cette méthode permet de lister tous les véhicules")
    @GetMapping("/getAll")
    public List<Vehicule> getAll() {
        return service.getAll();
    }

    @Operation(description = "Cette méthode permet de mettre à jour un véhicule")
    @PutMapping("/update")
    public Vehicule update(@Valid @RequestBody Vehicule vehicule) {
        return service.update(vehicule);
    }

    @Operation(description = "Cette méthode permet de supprimer un véhicule par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Create Vehicule",
            description = "Returns 201 and Location. New records must not contain an ID.")
    public ResponseEntity<Vehicule> createResource(@Valid @RequestBody Vehicule input) {
        Vehicule saved = service.create(input);
        return ResponseEntity.created(URI.create("/vehicule/" + saved.getIdVehicule())).body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Vehicule; returns 404 when absent")
    public Vehicule getResource(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "List Vehicule records")
    public List<Vehicule> listResources() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Vehicule")
    public Vehicule updateResource(@PathVariable Long id, @Valid @RequestBody Vehicule input) {
        if (input.getIdVehicule() != null && !id.equals(input.getIdVehicule()))
            throw new IllegalArgumentException("Path and body IDs must match");
        input.setIdVehicule(id);
        return service.update(input);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Vehicule; returns 409 if dependent records prevent deletion")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{vehiculeId}/equipement/{equipementId}")
    @Operation(summary = "Assign existing equipment idempotently")
    public Vehicule assignEquipement(
            @PathVariable Long vehiculeId, @PathVariable Long equipementId) {
        return service.assignEquipement(vehiculeId, equipementId);
    }

    @DeleteMapping("/{vehiculeId}/equipement/{equipementId}")
    @Operation(summary = "Unlink equipment without deleting either record")
    public Vehicule removeEquipement(
            @PathVariable Long vehiculeId, @PathVariable Long equipementId) {
        return service.removeEquipement(vehiculeId, equipementId);
    }
}
