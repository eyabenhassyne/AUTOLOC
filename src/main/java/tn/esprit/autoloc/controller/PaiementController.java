package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.IPaiementService;

import java.util.List;

@Tag(name = "Gestion des paiements")
@RestController
@AllArgsConstructor
@RequestMapping("/paiement")
public class PaiementController {

    IPaiementService service;

    @Operation(description = "Cette méthode permet d'ajouter un paiement")
    @PostMapping("/create")
    public Paiement create(@RequestBody Paiement paiement) {
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
    public Paiement update(@RequestBody Paiement paiement) {
        return service.update(paiement);
    }

    @Operation(description = "Cette méthode permet de supprimer un paiement par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
