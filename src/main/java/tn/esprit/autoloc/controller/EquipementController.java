package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.service.IEquipementService;

import java.util.List;

@Tag(name = "Gestion des équipements")
@RestController
@AllArgsConstructor
@RequestMapping("/equipement")
public class EquipementController {

    IEquipementService service;

    @Operation(description = "Cette méthode permet d'ajouter un équipement")
    @PostMapping("/create")
    public Equipement create(@RequestBody Equipement equipement) {
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
    public Equipement update(@RequestBody Equipement equipement) {
        return service.update(equipement);
    }

    @Operation(description = "Cette méthode permet de supprimer un équipement par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
