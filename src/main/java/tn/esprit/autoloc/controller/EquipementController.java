package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.service.IEquipementService;

import java.net.URI;
import java.util.List;

@Tag(name = "Gestion des équipements")
@RestController
@AllArgsConstructor
@RequestMapping("/equipement")
public class EquipementController {

    IEquipementService service;

    @Operation(description = "Cette méthode permet d'ajouter un équipement")
    @PostMapping("/create")
    public Equipement create(@Valid @RequestBody Equipement equipement) {
        return service.create(equipement);
    }

    @Operation(description = "Cette méthode permet d'obtenir un équipement par son identifiant")
    @GetMapping("/get/{id}")
    public Equipement getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(description = "Cette méthode permet de lister tous les équipements")
    @GetMapping("/getAll")
    public List<Equipement> getAll() {
        return service.getAll();
    }

    @Operation(description = "Cette méthode permet de mettre à jour un équipement")
    @PutMapping("/update")
    public Equipement update(@Valid @RequestBody Equipement equipement) {
        return service.update(equipement);
    }

    @Operation(description = "Cette méthode permet de supprimer un équipement par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Create Equipement",
            description = "Returns 201 and Location. New records must not contain an ID.")
    public ResponseEntity<Equipement> createResource(@Valid @RequestBody Equipement input) {
        Equipement saved = service.create(input);
        return ResponseEntity.created(URI.create("/equipement/" + saved.getIdEquipement()))
                .body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Equipement; returns 404 when absent")
    public Equipement getResource(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "List Equipement records")
    public List<Equipement> listResources() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Equipement")
    public Equipement updateResource(@PathVariable Long id, @Valid @RequestBody Equipement input) {
        if (input.getIdEquipement() != null && !id.equals(input.getIdEquipement()))
            throw new IllegalArgumentException("Path and body IDs must match");
        input.setIdEquipement(id);
        return service.update(input);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Equipement; returns 409 if dependent records prevent deletion")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
