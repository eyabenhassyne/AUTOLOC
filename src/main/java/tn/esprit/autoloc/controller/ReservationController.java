package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.service.IReservationService;

import java.net.URI;
import java.util.List;

@Tag(name = "Gestion des réservations")
@RestController
@AllArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    IReservationService service;

    @Operation(description = "Cette méthode permet d'ajouter une réservation")
    @PostMapping("/create")
    public Reservation create(@Valid @RequestBody Reservation reservation) {
        return service.create(reservation);
    }

    @Operation(description = "Cette méthode permet d'obtenir une réservation par son identifiant")
    @GetMapping("/get/{id}")
    public Reservation getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(description = "Cette méthode permet de lister toutes les réservations")
    @GetMapping("/getAll")
    public List<Reservation> getAll() {
        return service.getAll();
    }

    @Operation(description = "Cette méthode permet de mettre à jour une réservation")
    @PutMapping("/update")
    public Reservation update(@Valid @RequestBody Reservation reservation) {
        return service.update(reservation);
    }

    @Operation(
            description = "Cette méthode permet de supprimer une réservation par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Create Reservation",
            description = "Returns 201 and Location. New records must not contain an ID.")
    public ResponseEntity<Reservation> createResource(@Valid @RequestBody Reservation input) {
        Reservation saved = service.create(input);
        return ResponseEntity.created(URI.create("/reservation/" + saved.getIdReservation()))
                .body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Reservation; returns 404 when absent")
    public Reservation getResource(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "List Reservation records")
    public List<Reservation> listResources() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Reservation")
    public Reservation updateResource(
            @PathVariable Long id, @Valid @RequestBody Reservation input) {
        if (input.getIdReservation() != null && !id.equals(input.getIdReservation()))
            throw new IllegalArgumentException("Path and body IDs must match");
        input.setIdReservation(id);
        return service.update(input);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Reservation; returns 409 if dependent records prevent deletion")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping("/with-contract")
    @Operation(
            summary = "Create a reservation and its contract atomically",
            description = "Nested payments can be new or existing unassigned payment IDs.")
    public ResponseEntity<tn.esprit.autoloc.dto.ReservationContractResponse> createWithContract(
            @Valid @RequestBody tn.esprit.autoloc.dto.ReservationContractRequest request) {
        var saved = service.createWithContract(request);
        return ResponseEntity.created(
                        URI.create("/reservation/" + saved.reservation().getIdReservation()))
                .body(saved);
    }

    @GetMapping("/{id}/contrat")
    @Operation(summary = "Get a reservation's contract")
    public tn.esprit.autoloc.domain.Contrat getContract(@PathVariable Long id) {
        return service.getContract(id);
    }
}
