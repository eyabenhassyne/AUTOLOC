package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.service.IEmployeService;

import java.net.URI;
import java.util.List;

@Tag(name = "Gestion des employés")
@RestController
@AllArgsConstructor
@RequestMapping("/employe")
public class EmployeController {

    IEmployeService service;

    @Operation(description = "Cette méthode permet d'ajouter un employé")
    @PostMapping("/create")
    public Employe create(@Valid @RequestBody Employe employe) {
        return service.create(employe);
    }

    @Operation(description = "Cette méthode permet d'obtenir un employé par son identifiant")
    @GetMapping("/get/{id}")
    public Employe getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(description = "Cette méthode permet de lister tous les employés")
    @GetMapping("/getAll")
    public List<Employe> getAll() {
        return service.getAll();
    }

    @Operation(description = "Cette méthode permet de mettre à jour un employé")
    @PutMapping("/update")
    public Employe update(@Valid @RequestBody Employe employe) {
        return service.update(employe);
    }

    @Operation(description = "Cette méthode permet de supprimer un employé par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Create Employe",
            description = "Returns 201 and Location. New records must not contain an ID.")
    public ResponseEntity<Employe> createResource(@Valid @RequestBody Employe input) {
        Employe saved = service.create(input);
        return ResponseEntity.created(URI.create("/employe/" + saved.getIdEmploye())).body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Employe; returns 404 when absent")
    public Employe getResource(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "List Employe records")
    public List<Employe> listResources() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Employe")
    public Employe updateResource(@PathVariable Long id, @Valid @RequestBody Employe input) {
        if (input.getIdEmploye() != null && !id.equals(input.getIdEmploye()))
            throw new IllegalArgumentException("Path and body IDs must match");
        input.setIdEmploye(id);
        return service.update(input);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Employe; returns 409 if dependent records prevent deletion")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{employeId}/agence/{agenceId}")
    @Operation(summary = "Assign an existing employee to an existing agency")
    public Employe assignAgence(@PathVariable Long employeId, @PathVariable Long agenceId) {
        return service.assignAgence(employeId, agenceId);
    }
}
