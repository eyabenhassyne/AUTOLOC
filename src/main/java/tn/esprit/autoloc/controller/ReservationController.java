package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.service.IReservationService;

import java.util.List;

@Tag(name = "Gestion des réservations")
@RestController
@AllArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    IReservationService service;

    @Operation(description = "Cette méthode permet d'ajouter une réservation")
    @PostMapping("/create")
    public Reservation create(@RequestBody Reservation reservation) {
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
    public Reservation update(@RequestBody Reservation reservation) {
        return service.update(reservation);
    }

    @Operation(description = "Cette méthode permet de supprimer une réservation par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
