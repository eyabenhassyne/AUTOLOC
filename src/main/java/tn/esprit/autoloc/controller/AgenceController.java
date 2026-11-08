package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.service.IAgenceService;

import java.util.List;

@Tag(name = "Gestion des agences")
@RestController
@AllArgsConstructor
@RequestMapping("/agence")
public class AgenceController {

    IAgenceService service;

    @Operation(description = "Cette méthode permet d'ajouter une agence")
    @PostMapping("/create")
    public Agence create(@RequestBody Agence agence) {
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
    public Agence update(@RequestBody Agence agence) {
        return service.update(agence);
    }

    @Operation(description = "Cette méthode permet de supprimer une agence par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
