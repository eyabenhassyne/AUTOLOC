package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.IPaiementService;

import java.net.URI;
import java.util.List;

@Tag(name = "Gestion des paiements")
@RestController
@AllArgsConstructor
@RequestMapping("/paiement")
public class PaiementController {

    IPaiementService service;

    @Operation(description = "Cette méthode permet d'ajouter un paiement")
    @PostMapping("/create")
    public Paiement create(@Valid @RequestBody Paiement paiement) {
        return service.create(paiement);
    }

    @Operation(description = "Cette méthode permet d'obtenir un paiement par son identifiant")
    @GetMapping("/get/{id}")
    public Paiement getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(description = "Cette méthode permet de lister tous les paiements")
    @GetMapping("/getAll")
    public List<Paiement> getAll() {
        return service.getAll();
    }

    @Operation(description = "Cette méthode permet de mettre à jour un paiement")
    @PutMapping("/update")
    public Paiement update(@Valid @RequestBody Paiement paiement) {
        return service.update(paiement);
    }

    @Operation(description = "Cette méthode permet de supprimer un paiement par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Create Paiement",
            description = "Returns 201 and Location. New records must not contain an ID.")
    public ResponseEntity<Paiement> createResource(@Valid @RequestBody Paiement input) {
        Paiement saved = service.create(input);
        return ResponseEntity.created(URI.create("/paiement/" + saved.getIdPaiement())).body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Paiement; returns 404 when absent")
    public Paiement getResource(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "List Paiement records")
    public List<Paiement> listResources() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Paiement")
    public Paiement updateResource(@PathVariable Long id, @Valid @RequestBody Paiement input) {
        if (input.getIdPaiement() != null && !id.equals(input.getIdPaiement()))
            throw new IllegalArgumentException("Path and body IDs must match");
        input.setIdPaiement(id);
        return service.update(input);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Paiement; returns 409 if dependent records prevent deletion")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
