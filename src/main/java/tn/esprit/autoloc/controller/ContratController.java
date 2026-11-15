package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.service.IContratService;

import java.net.URI;
import java.util.List;

@Tag(name = "Gestion des contrats")
@RestController
@AllArgsConstructor
@RequestMapping("/contrat")
public class ContratController {

    IContratService service;

    @Operation(description = "Cette méthode permet d'ajouter un contrat")
    @PostMapping("/create")
    public Contrat create(@Valid @RequestBody Contrat contrat) {
        return service.create(contrat);
    }

    @Operation(description = "Cette méthode permet d'obtenir un contrat par son identifiant")
    @GetMapping("/get/{id}")
    public Contrat getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(description = "Cette méthode permet de lister tous les contrats")
    @GetMapping("/getAll")
    public List<Contrat> getAll() {
        return service.getAll();
    }

    @Operation(description = "Cette méthode permet de mettre à jour un contrat")
    @PutMapping("/update")
    public Contrat update(@Valid @RequestBody Contrat contrat) {
        return service.update(contrat);
    }

    @Operation(description = "Cette méthode permet de supprimer un contrat par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Create Contrat",
            description = "Returns 201 and Location. New records must not contain an ID.")
    public ResponseEntity<Contrat> createResource(@Valid @RequestBody Contrat input) {
        Contrat saved = service.create(input);
        return ResponseEntity.created(URI.create("/contrat/" + saved.getIdContrat())).body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Contrat; returns 404 when absent")
    public Contrat getResource(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "List Contrat records")
    public List<Contrat> listResources() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Contrat")
    public Contrat updateResource(@PathVariable Long id, @Valid @RequestBody Contrat input) {
        if (input.getIdContrat() != null && !id.equals(input.getIdContrat()))
            throw new IllegalArgumentException("Path and body IDs must match");
        input.setIdContrat(id);
        return service.update(input);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Contrat; returns 409 if dependent records prevent deletion")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/paiements")
    @Operation(summary = "List a contract's payments")
    public List<tn.esprit.autoloc.domain.Paiement> getPayments(@PathVariable Long id) {
        return service.getPayments(id);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping("/{id}/paiements")
    @Operation(summary = "Create and link a new contract payment")
    public ResponseEntity<tn.esprit.autoloc.domain.Paiement> addPayment(
            @PathVariable Long id, @Valid @RequestBody tn.esprit.autoloc.domain.Paiement payment) {
        var saved = service.addPayment(id, payment);
        return ResponseEntity.created(URI.create("/paiement/" + saved.getIdPaiement())).body(saved);
    }
}
