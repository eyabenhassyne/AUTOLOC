package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.service.IClientService;

import java.util.List;

@Tag(name = "Gestion des clients")
@RestController
@AllArgsConstructor
@RequestMapping("/client")
public class ClientController {

    IClientService service;

    @Operation(description = "Cette méthode permet d'ajouter un client")
    @PostMapping("/create")
    public Client create(@RequestBody Client client) {
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
    public Client update(@RequestBody Client client) {
        return service.update(client);
    }

    @Operation(description = "Cette méthode permet de supprimer un client par son identifiant")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
