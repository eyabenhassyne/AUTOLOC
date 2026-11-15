package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.service.IAgenceService;

import java.net.URI;
import java.util.List;

@Tag(name = "Gestion des agences")
@RestController
@AllArgsConstructor
@RequestMapping("/agence")
public class AgenceController {

    IAgenceService service;

    @Operation(description = "Cette méthode permet d'ajouter une agence")
    @PostMapping("/create")
    public Agence create(@Valid @RequestBody Agence agence) {
        return service.create(agence);
    }

    @Operation(description = "Cette méthode permet d'obtenir une agence par son identifiant")
    @GetMapping("/get/{id}")
    public Agence getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(description = "Cette méthode permet de lister toutes les agences")
    @GetMapping("/getAll")
    public List<Agence> getAll() {
        return service.getAll();
    }

    @Operation(description = "Cette méthode permet de mettre à jour une agence")
    @PutMapping("/update")
    public Agence update(@Valid @RequestBody Agence agence) {
        return service.update(agence);
    }

    @Operation(description = "Cette méthode permet de supprimer une agence par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Create Agence",
            description = "Returns 201 and Location. New records must not contain an ID.")
    public ResponseEntity<Agence> createResource(@Valid @RequestBody Agence input) {
        Agence saved = service.create(input);
        return ResponseEntity.created(URI.create("/agence/" + saved.getIdAgence())).body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Agence; returns 404 when absent")
    public Agence getResource(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "List Agence records")
    public List<Agence> listResources() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Agence")
    public Agence updateResource(@PathVariable Long id, @Valid @RequestBody Agence input) {
        if (input.getIdAgence() != null && !id.equals(input.getIdAgence()))
            throw new IllegalArgumentException("Path and body IDs must match");
        input.setIdAgence(id);
        return service.update(input);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Agence; returns 409 if dependent records prevent deletion")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
