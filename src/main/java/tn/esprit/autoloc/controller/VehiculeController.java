package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IVehiculeService;

import java.util.List;

@Tag(name = "Gestion des véhicules")
@RestController
@AllArgsConstructor
@RequestMapping("/vehicule")
public class VehiculeController {

    IVehiculeService service;

    @Operation(description = "Cette méthode permet d'ajouter un véhicule")
    @PostMapping("/create")
    public Vehicule create(@RequestBody Vehicule vehicule) {
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
    public Vehicule update(@RequestBody Vehicule vehicule) {
        return service.update(vehicule);
    }

    @Operation(description = "Cette méthode permet de supprimer un véhicule par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
