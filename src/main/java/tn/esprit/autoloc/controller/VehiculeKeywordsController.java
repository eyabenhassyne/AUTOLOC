package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.service.IVehiculeService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicule/keywords")
@Tag(name = "Véhicules — Keywords JPA", description = "Les 29 exercices de requêtes dérivées")
public class VehiculeKeywordsController {
    private final IVehiculeService service;

    @GetMapping("/marque")
    @Operation(summary = "1. Marque exacte", description = "findByMarque — findBy")
    public List<Vehicule> query01(@RequestParam String marque) {
        return service.findByMarque(marque);
    }

    @GetMapping("/marque/contains")
    @Operation(
            summary = "2. Marque contenant un texte",
            description = "findByMarqueContaining — Containing")
    public List<Vehicule> query02(@RequestParam String value) {
        return service.findByMarqueContaining(value);
    }

    @GetMapping("/marque/starts-with")
    @Operation(
            summary = "3. Marque commençant par un texte",
            description = "findByMarqueStartingWith — StartingWith")
    public List<Vehicule> query03(@RequestParam String value) {
        return service.findByMarqueStartingWith(value);
    }

    @GetMapping("/marque/ends-with")
    @Operation(
            summary = "4. Marque finissant par un texte",
            description = "findByMarqueEndingWith — EndingWith")
    public List<Vehicule> query04(@RequestParam String value) {
        return service.findByMarqueEndingWith(value);
    }

    @GetMapping("/marque/ignore-case")
    @Operation(
            summary = "5. Marque sans tenir compte de la casse",
            description = "findByMarqueIgnoreCase — IgnoreCase")
    public List<Vehicule> query05(@RequestParam String marque) {
        return service.findByMarqueIgnoreCase(marque);
    }

    @GetMapping("/modele")
    @Operation(summary = "6. Modèle exact", description = "findByModele — findBy")
    public List<Vehicule> query06(@RequestParam String modele) {
        return service.findByModele(modele);
    }

    @GetMapping("/immatriculation/exists")
    @Operation(
            summary = "7. Vérifier une immatriculation",
            description = "existsByImmatriculation — existsBy")
    public boolean query07(@RequestParam String immatriculation) {
        return service.existsByImmatriculation(immatriculation);
    }

    @GetMapping("/marque/count")
    @Operation(summary = "8. Compter par marque", description = "countByMarque — countBy")
    public long query08(@RequestParam String marque) {
        return service.countByMarque(marque);
    }

    @GetMapping("/prix/greater-than")
    @Operation(
            summary = "9. Tarif strictement supérieur",
            description = "findByTarifJournalierGreaterThan — GreaterThan")
    public List<Vehicule> query09(@RequestParam BigDecimal prix) {
        return service.findByTarifJournalierGreaterThan(prix);
    }

    @GetMapping("/prix/less-than")
    @Operation(
            summary = "10. Tarif strictement inférieur",
            description = "findByTarifJournalierLessThan — LessThan")
    public List<Vehicule> query10(@RequestParam BigDecimal prix) {
        return service.findByTarifJournalierLessThan(prix);
    }

    @GetMapping("/prix/greater-than-equal")
    @Operation(
            summary = "11. Tarif supérieur ou égal",
            description = "findByTarifJournalierGreaterThanEqual — GreaterThanEqual")
    public List<Vehicule> query11(@RequestParam BigDecimal prix) {
        return service.findByTarifJournalierGreaterThanEqual(prix);
    }

    @GetMapping("/prix/between")
    @Operation(
            summary = "12. Tarif entre deux bornes incluses",
            description = "findByTarifJournalierBetween — Between")
    public List<Vehicule> query12(
            @RequestParam BigDecimal minimum, @RequestParam BigDecimal maximum) {
        return service.findByTarifJournalierBetween(minimum, maximum);
    }

    @GetMapping("/prix/asc")
    @Operation(
            summary = "13. Tous les tarifs croissants",
            description = "findAllByOrderByTarifJournalierAsc — OrderBy, Asc")
    public List<Vehicule> query13() {
        return service.findAllByOrderByTarifJournalierAsc();
    }

    @GetMapping("/prix/desc")
    @Operation(
            summary = "14. Tous les tarifs décroissants",
            description = "findAllByOrderByTarifJournalierDesc — OrderBy, Desc")
    public List<Vehicule> query14() {
        return service.findAllByOrderByTarifJournalierDesc();
    }

    @GetMapping("/marque/prix-asc")
    @Operation(
            summary = "15. Marque puis tarif croissant",
            description = "findByMarqueOrderByTarifJournalierAsc — OrderBy, Asc")
    public List<Vehicule> query15(@RequestParam String marque) {
        return service.findByMarqueOrderByTarifJournalierAsc(marque);
    }

    @GetMapping("/marque/prix-greater-than")
    @Operation(
            summary = "16. Marque ET tarif supérieur",
            description = "findByMarqueAndTarifJournalierGreaterThan — And, GreaterThan")
    public List<Vehicule> query16(@RequestParam String marque, @RequestParam BigDecimal prix) {
        return service.findByMarqueAndTarifJournalierGreaterThan(marque, prix);
    }

    @GetMapping("/marque-or-modele")
    @Operation(summary = "17. Marque OU modèle", description = "findByMarqueOrModele — Or")
    public List<Vehicule> query17(@RequestParam String marque, @RequestParam String modele) {
        return service.findByMarqueOrModele(marque, modele);
    }

    @GetMapping("/marques/in")
    @Operation(summary = "18. Marques dans une liste", description = "findByMarqueIn — In")
    public List<Vehicule> query18(@RequestParam List<String> marques) {
        return service.findByMarqueIn(marques);
    }

    @GetMapping("/marques/not-in")
    @Operation(summary = "19. Marques hors de la liste", description = "findByMarqueNotIn — NotIn")
    public List<Vehicule> query19(@RequestParam List<String> marques) {
        return service.findByMarqueNotIn(marques);
    }

    @GetMapping("/statut")
    @Operation(summary = "20. Filtrer par statut", description = "findByStatut — findBy")
    public List<Vehicule> query20(@RequestParam StatutVehicule statut) {
        return service.findByStatut(statut);
    }

    @GetMapping("/disponibles/prix-asc")
    @Operation(
            summary = "21. Disponibles au tarif croissant",
            description = "findByStatutOrderByTarifJournalierAsc — OrderBy, Asc")
    public List<Vehicule> query21() {
        return service.findAvailableOrderByTarifJournalierAsc();
    }

    @GetMapping("/agence")
    @Operation(
            summary = "22. Véhicules d’une agence",
            description = "findByAgence_IdAgence — findBy, _")
    public List<Vehicule> query22(@RequestParam Long agenceId) {
        return service.findByAgence_IdAgence(agenceId);
    }

    @GetMapping("/agence/ville")
    @Operation(
            summary = "23. Agences dans une ville",
            description = "findByAgence_Ville — findBy, _")
    public List<Vehicule> query23(@RequestParam String ville) {
        return service.findByAgence_Ville(ville);
    }

    @GetMapping("/agence/nom/contains")
    @Operation(
            summary = "24. Nom d’agence contenant un texte",
            description = "findByAgence_NomContaining — _, Containing")
    public List<Vehicule> query24(@RequestParam String value) {
        return service.findByAgence_NomContaining(value);
    }

    @GetMapping("/agence/marque")
    @Operation(
            summary = "25. Agence ET marque",
            description = "findByAgence_IdAgenceAndMarque — _, And")
    public List<Vehicule> query25(@RequestParam Long agenceId, @RequestParam String marque) {
        return service.findByAgence_IdAgenceAndMarque(agenceId, marque);
    }

    @GetMapping("/agence/is-null")
    @Operation(summary = "26. Sans agence", description = "findByAgenceIsNull — IsNull")
    public List<Vehicule> query26() {
        return service.findByAgenceIsNull();
    }

    @GetMapping("/agence/is-not-null")
    @Operation(summary = "27. Avec agence", description = "findByAgenceIsNotNull — IsNotNull")
    public List<Vehicule> query27() {
        return service.findByAgenceIsNotNull();
    }

    @GetMapping("/agence/count")
    @Operation(
            summary = "28. Compter les véhicules d’une agence",
            description = "countByAgence_IdAgence — countBy, _")
    public long query28(@RequestParam Long agenceId) {
        return service.countByAgence_IdAgence(agenceId);
    }

    @GetMapping("/agence/exists")
    @Operation(
            summary = "29. L’agence possède-t-elle un véhicule ?",
            description = "existsByAgence_IdAgence — existsBy, _")
    public boolean query29(@RequestParam Long agenceId) {
        return service.existsByAgence_IdAgence(agenceId);
    }
}
