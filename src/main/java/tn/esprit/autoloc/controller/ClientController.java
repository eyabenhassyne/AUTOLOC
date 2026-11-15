package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.service.IClientService;

import java.net.URI;
import java.util.List;

@Tag(name = "Gestion des clients")
@RestController
@AllArgsConstructor
@RequestMapping("/client")
public class ClientController {

    IClientService service;

    @Operation(description = "Cette méthode permet d'ajouter un client")
    @PostMapping("/create")
    public Client create(@Valid @RequestBody Client client) {
        return service.create(client);
    }

    @Operation(description = "Cette méthode permet d'obtenir un client par son identifiant")
    @GetMapping("/get/{id}")
    public Client getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(description = "Cette méthode permet de lister tous les clients")
    @GetMapping("/getAll")
    public List<Client> getAll() {
        return service.getAll();
    }

    @Operation(description = "Cette méthode permet de mettre à jour un client")
    @PutMapping("/update")
    public Client update(@Valid @RequestBody Client client) {
        return service.update(client);
    }

    @Operation(description = "Cette méthode permet de supprimer un client par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping
    @Operation(
            summary = "Create Client",
            description = "Returns 201 and Location. New records must not contain an ID.")
    public ResponseEntity<Client> createResource(@Valid @RequestBody Client input) {
        Client saved = service.create(input);
        return ResponseEntity.created(URI.create("/client/" + saved.getIdClient())).body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Client; returns 404 when absent")
    public Client getResource(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "List Client records")
    public List<Client> listResources() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Client")
    public Client updateResource(@PathVariable Long id, @Valid @RequestBody Client input) {
        if (input.getIdClient() != null && !id.equals(input.getIdClient()))
            throw new IllegalArgumentException("Path and body IDs must match");
        input.setIdClient(id);
        return service.update(input);
    }

    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Client; returns 409 if dependent records prevent deletion")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @PostMapping("/with-reservations")
    @Operation(
            summary = "Create a client and reservations atomically",
            description = "Any invalid child rolls back the complete request.")
    public ResponseEntity<tn.esprit.autoloc.dto.ClientReservationsResponse> createWithReservations(
            @Valid @RequestBody tn.esprit.autoloc.dto.ClientReservationsRequest request) {
        var saved = service.createWithReservations(request);
        return ResponseEntity.created(URI.create("/client/" + saved.client().getIdClient()))
                .body(saved);
    }

    @GetMapping("/{id}/reservations")
    @Operation(summary = "List a client's reservations")
    public List<tn.esprit.autoloc.domain.Reservation> getReservations(@PathVariable Long id) {
        return service.getReservations(id);
    }
}
