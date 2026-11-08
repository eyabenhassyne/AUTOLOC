package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.service.IEmployeService;

import java.util.List;

@Tag(name = "Gestion des employés")
@RestController
@AllArgsConstructor
@RequestMapping("/employe")
public class EmployeController {

    IEmployeService service;

    @Operation(description = "Cette méthode permet d'ajouter un employé")
    @PostMapping("/create")
    public Employe create(@RequestBody Employe employe) {
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
    public Employe update(@RequestBody Employe employe) {
        return service.update(employe);
    }

    @Operation(description = "Cette méthode permet de supprimer un employé par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
