package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.service.IContratService;

import java.util.List;

@Tag(name = "Gestion des contrats")
@RestController
@AllArgsConstructor
@RequestMapping("/contrat")
public class ContratController {

    IContratService service;

    @Operation(description = "Cette méthode permet d'ajouter un contrat")
    @PostMapping("/create")
    public Contrat create(@RequestBody Contrat contrat) {
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
    public Contrat update(@RequestBody Contrat contrat) {
        return service.update(contrat);
    }

    @Operation(description = "Cette méthode permet de supprimer un contrat par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
