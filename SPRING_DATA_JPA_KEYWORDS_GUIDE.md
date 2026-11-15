# Spring Data JPA — Les 29 Keywords AutoLoc

Ce guide suit les 29 exigences fournies pour le chapitre 11. Le support de cours
complet n'était pas joint. Les méthodes utilisent les propriétés réelles du projet.
Spring Data construit chaque requête à partir du nom de méthode : aucun @Query,
SQL natif ou JPQL n'est écrit pour ces exercices.

## Préparer la démonstration

Propriétés : marque, modele, immatriculation, tarifJournalier (BigDecimal), statut
(StatutVehicule), agence ; Agence contient idAgence, nom et ville.
AVAILABLE correspond à StatutVehicule.DISPONIBLE.

Les méthodes sont dans VehiculeRepository et exposées par IVehiculeService.
AgenceRepository n'a pas besoin de doublons : les résultats concernent les
véhicules. Les nouvelles routes GET /vehicule/keywords/... laissent le CRUD intact.

Les tests de repository insèrent ce jeu dans H2 et annulent chaque transaction.
Les tests HTTP utilisent une autre base H2 temporaire. Aucun test ne modifie MySQL.

| Immatriculation | Marque | Modèle | Tarif | Statut | Agence |
| --- | --- | --- | ---: | --- | --- |
| T1 | Toyota | Yaris | 50 | DISPONIBLE | A |
| T2 | Toyota | Corolla | 100 | LOUE | A |
| T3 | TOYOTA | Yaris | 75 | DISPONIBLE | B |
| R1 | Renault | Clio | 80 | DISPONIBLE | B |
| F1 | Ford | Focus | 100 | MAINTENANCE | aucune |
| B1 | BMW | Yaris | 150 | DISPONIBLE | C |
| H1 | Hyundai | i20 | 120 | LOUE | aucune |
| K1 | Kia | Rio | 80 | DISPONIBLE | A |

A = AutoLoc Centre à Tunis ; B = AutoLoc Sud à Sfax ; C = Luxe à Tunis.
Une quatrième agence, Agence Vide à Bizerte, ne possède aucun véhicule.
Les IDs sont générés : utiliser agencyA.getIdAgence() dans les tests et remplacer
`<idA>` par un véritable identifiant dans Swagger. money("80") signifie
new BigDecimal("80"). Les exemples supposent les imports java.math.BigDecimal,
java.util.List et tn.esprit.autoloc.domain.StatutVehicule.

Sans OrderBy, aucun ordre n'est garanti. Des tarifs égaux peuvent être rendus
dans un ordre interne quelconque : les tests de tri comparent les prix et les
autres tests comparent les immatriculations.

Sans IgnoreCase, égalité et recherches partielles dépendent de la collation
MySQL. Les résultats ci-dessous reposent sur H2 sensible à la casse ; IgnoreCase
exprime explicitement une comparaison sans casse. Les caractères % et _ passés
aux méthodes Containing/StartingWith/EndingWith sont traités comme du texte.

Dans findByAgence_IdAgenceAndMarque, les propriétés sont celles de Java :
agence.idAgence puis marque. Ce ne sont pas les noms de colonnes SQL.

## 1. Marque exacte

**Besoin :** Marque exacte.

**Méthode Repository :**
~~~java
List<Vehicule> findByMarque(String marque);
~~~

**Keyword et sens :** findBy. Égalité sur la propriété marque.

**Exemple d'appel :**
~~~java
repository.findByMarque("Toyota");
// Appel depuis le service :
service.findByMarque("Toyota");
~~~

**Résultat attendu sur H2 :** T1,T2.

**Endpoint :** `GET /vehicule/keywords/marque?marque=Toyota`.

**Test :** VehiculeKeywordsRepositoryTests.q01().
~~~java
assertIds(repository.findByMarque("Toyota"),"T1","T2");
~~~

## 2. Marque contenant un texte

**Besoin :** Marque contenant un texte.

**Méthode Repository :**
~~~java
List<Vehicule> findByMarqueContaining(String value);
~~~

**Keyword et sens :** Containing. Le texte peut apparaître à toute position.

**Exemple d'appel :**
~~~java
repository.findByMarqueContaining("oy");
// Appel depuis le service :
service.findByMarqueContaining("oy");
~~~

**Résultat attendu sur H2 :** T1,T2.

**Endpoint :** `GET /vehicule/keywords/marque/contains?value=oy`.

**Test :** VehiculeKeywordsRepositoryTests.q02().
~~~java
assertIds(repository.findByMarqueContaining("oy"),"T1","T2");
~~~

## 3. Marque commençant par un texte

**Besoin :** Marque commençant par un texte.

**Méthode Repository :**
~~~java
List<Vehicule> findByMarqueStartingWith(String value);
~~~

**Keyword et sens :** StartingWith. Le texte doit être au début.

**Exemple d'appel :**
~~~java
repository.findByMarqueStartingWith("Toy");
// Appel depuis le service :
service.findByMarqueStartingWith("Toy");
~~~

**Résultat attendu sur H2 :** T1,T2.

**Endpoint :** `GET /vehicule/keywords/marque/starts-with?value=Toy`.

**Test :** VehiculeKeywordsRepositoryTests.q03().
~~~java
assertIds(repository.findByMarqueStartingWith("Toy"),"T1","T2");
~~~

## 4. Marque finissant par un texte

**Besoin :** Marque finissant par un texte.

**Méthode Repository :**
~~~java
List<Vehicule> findByMarqueEndingWith(String value);
~~~

**Keyword et sens :** EndingWith. Le texte doit être à la fin.

**Exemple d'appel :**
~~~java
repository.findByMarqueEndingWith("ota");
// Appel depuis le service :
service.findByMarqueEndingWith("ota");
~~~

**Résultat attendu sur H2 :** T1,T2.

**Endpoint :** `GET /vehicule/keywords/marque/ends-with?value=ota`.

**Test :** VehiculeKeywordsRepositoryTests.q04().
~~~java
assertIds(repository.findByMarqueEndingWith("ota"),"T1","T2");
~~~

## 5. Marque sans tenir compte de la casse

**Besoin :** Marque sans tenir compte de la casse.

**Méthode Repository :**
~~~java
List<Vehicule> findByMarqueIgnoreCase(String marque);
~~~

**Keyword et sens :** IgnoreCase. Majuscules et minuscules ne sont pas distinguées.

**Exemple d'appel :**
~~~java
repository.findByMarqueIgnoreCase("toyota");
// Appel depuis le service :
service.findByMarqueIgnoreCase("toyota");
~~~

**Résultat attendu sur H2 :** T1,T2,T3.

**Endpoint :** `GET /vehicule/keywords/marque/ignore-case?marque=toyota`.

**Test :** VehiculeKeywordsRepositoryTests.q05().
~~~java
assertIds(repository.findByMarqueIgnoreCase("toyota"),"T1","T2","T3");
~~~

## 6. Modèle exact

**Besoin :** Modèle exact.

**Méthode Repository :**
~~~java
List<Vehicule> findByModele(String modele);
~~~

**Keyword et sens :** findBy. Égalité sur modele, pas sur marque.

**Exemple d'appel :**
~~~java
repository.findByModele("Yaris");
// Appel depuis le service :
service.findByModele("Yaris");
~~~

**Résultat attendu sur H2 :** T1,T3,B1.

**Endpoint :** `GET /vehicule/keywords/modele?modele=Yaris`.

**Test :** VehiculeKeywordsRepositoryTests.q06().
~~~java
assertIds(repository.findByModele("Yaris"),"T1","T3","B1");
~~~

## 7. Vérifier une immatriculation

**Besoin :** Vérifier une immatriculation.

**Méthode Repository :**
~~~java
boolean existsByImmatriculation(String immatriculation);
~~~

**Keyword et sens :** existsBy. Renvoie un booléen, pas une liste.

**Exemple d'appel :**
~~~java
repository.existsByImmatriculation("T1");
// Appel depuis le service :
service.existsByImmatriculation("T1");
~~~

**Résultat attendu sur H2 :** true.

**Endpoint :** `GET /vehicule/keywords/immatriculation/exists?immatriculation=T1`.

**Test :** VehiculeKeywordsRepositoryTests.q07().
~~~java
assertTrue(repository.existsByImmatriculation("T1")); assertFalse(repository.existsByImmatriculation("ABSENT"));
~~~

## 8. Compter par marque

**Besoin :** Compter par marque.

**Méthode Repository :**
~~~java
long countByMarque(String marque);
~~~

**Keyword et sens :** countBy. Renvoie le nombre de véhicules correspondants.

**Exemple d'appel :**
~~~java
repository.countByMarque("Toyota");
// Appel depuis le service :
service.countByMarque("Toyota");
~~~

**Résultat attendu sur H2 :** 2.

**Endpoint :** `GET /vehicule/keywords/marque/count?marque=Toyota`.

**Test :** VehiculeKeywordsRepositoryTests.q08().
~~~java
assertEquals(2,repository.countByMarque("Toyota")); assertEquals(0,repository.countByMarque("Absent"));
~~~

## 9. Tarif strictement supérieur

**Besoin :** Tarif strictement supérieur.

**Méthode Repository :**
~~~java
List<Vehicule> findByTarifJournalierGreaterThan(BigDecimal prix);
~~~

**Keyword et sens :** GreaterThan. La valeur limite est exclue : tarif > prix.

**Exemple d'appel :**
~~~java
repository.findByTarifJournalierGreaterThan(money("100"));
// Appel depuis le service :
service.findByTarifJournalierGreaterThan(money("100"));
~~~

**Résultat attendu sur H2 :** H1,B1.

**Endpoint :** `GET /vehicule/keywords/prix/greater-than?prix=100`.

**Test :** VehiculeKeywordsRepositoryTests.q09().
~~~java
assertIds(repository.findByTarifJournalierGreaterThan(money("100")),"H1","B1");
~~~

## 10. Tarif strictement inférieur

**Besoin :** Tarif strictement inférieur.

**Méthode Repository :**
~~~java
List<Vehicule> findByTarifJournalierLessThan(BigDecimal prix);
~~~

**Keyword et sens :** LessThan. La valeur limite est exclue : tarif < prix.

**Exemple d'appel :**
~~~java
repository.findByTarifJournalierLessThan(money("80"));
// Appel depuis le service :
service.findByTarifJournalierLessThan(money("80"));
~~~

**Résultat attendu sur H2 :** T1,T3.

**Endpoint :** `GET /vehicule/keywords/prix/less-than?prix=80`.

**Test :** VehiculeKeywordsRepositoryTests.q10().
~~~java
assertIds(repository.findByTarifJournalierLessThan(money("80")),"T1","T3");
~~~

## 11. Tarif supérieur ou égal

**Besoin :** Tarif supérieur ou égal.

**Méthode Repository :**
~~~java
List<Vehicule> findByTarifJournalierGreaterThanEqual(BigDecimal prix);
~~~

**Keyword et sens :** GreaterThanEqual. La valeur limite est incluse : tarif >= prix.

**Exemple d'appel :**
~~~java
repository.findByTarifJournalierGreaterThanEqual(money("100"));
// Appel depuis le service :
service.findByTarifJournalierGreaterThanEqual(money("100"));
~~~

**Résultat attendu sur H2 :** T2,F1,H1,B1.

**Endpoint :** `GET /vehicule/keywords/prix/greater-than-equal?prix=100`.

**Test :** VehiculeKeywordsRepositoryTests.q11().
~~~java
assertIds(repository.findByTarifJournalierGreaterThanEqual(money("100")),"T2","F1","H1","B1");
~~~

## 12. Tarif entre deux bornes incluses

**Besoin :** Tarif entre deux bornes incluses.

**Méthode Repository :**
~~~java
List<Vehicule> findByTarifJournalierBetween(BigDecimal minimum, BigDecimal maximum);
~~~

**Keyword et sens :** Between. Les deux bornes sont incluses : minimum <= tarif <= maximum.

**Exemple d'appel :**
~~~java
repository.findByTarifJournalierBetween(money("75"), money("100"));
// Appel depuis le service :
service.findByTarifJournalierBetween(money("75"), money("100"));
~~~

**Résultat attendu sur H2 :** T3,R1,K1,T2,F1.

**Endpoint :** `GET /vehicule/keywords/prix/between?minimum=75&maximum=100`.

**Test :** VehiculeKeywordsRepositoryTests.q12().
~~~java
assertIds(repository.findByTarifJournalierBetween(money("75"), money("100")),"T3","R1","K1","T2","F1");
~~~

## 13. Tous les tarifs croissants

**Besoin :** Tous les tarifs croissants.

**Méthode Repository :**
~~~java
List<Vehicule> findAllByOrderByTarifJournalierAsc();
~~~

**Keyword et sens :** OrderBy, Asc. Trie du plus petit au plus grand tarif.

**Exemple d'appel :**
~~~java
repository.findAllByOrderByTarifJournalierAsc();
// Appel depuis le service :
service.findAllByOrderByTarifJournalierAsc();
~~~

**Résultat attendu sur H2 :** 50,75,80,80,100,100,120,150 (tarifs dans cet ordre).

**Endpoint :** `GET /vehicule/keywords/prix/asc`.

**Test :** VehiculeKeywordsRepositoryTests.q13().
~~~java
assertPrices(repository.findAllByOrderByTarifJournalierAsc(),"50","75","80","80","100","100","120","150");
~~~

## 14. Tous les tarifs décroissants

**Besoin :** Tous les tarifs décroissants.

**Méthode Repository :**
~~~java
List<Vehicule> findAllByOrderByTarifJournalierDesc();
~~~

**Keyword et sens :** OrderBy, Desc. Trie du plus grand au plus petit tarif.

**Exemple d'appel :**
~~~java
repository.findAllByOrderByTarifJournalierDesc();
// Appel depuis le service :
service.findAllByOrderByTarifJournalierDesc();
~~~

**Résultat attendu sur H2 :** 150,120,100,100,80,80,75,50 (tarifs dans cet ordre).

**Endpoint :** `GET /vehicule/keywords/prix/desc`.

**Test :** VehiculeKeywordsRepositoryTests.q14().
~~~java
assertPrices(repository.findAllByOrderByTarifJournalierDesc(),"150","120","100","100","80","80","75","50");
~~~

## 15. Marque puis tarif croissant

**Besoin :** Marque puis tarif croissant.

**Méthode Repository :**
~~~java
List<Vehicule> findByMarqueOrderByTarifJournalierAsc(String marque);
~~~

**Keyword et sens :** OrderBy, Asc. Filtre la marque avant de trier les véhicules retenus.

**Exemple d'appel :**
~~~java
repository.findByMarqueOrderByTarifJournalierAsc("Toyota");
// Appel depuis le service :
service.findByMarqueOrderByTarifJournalierAsc("Toyota");
~~~

**Résultat attendu sur H2 :** T1,T2.

**Endpoint :** `GET /vehicule/keywords/marque/prix-asc?marque=Toyota`.

**Test :** VehiculeKeywordsRepositoryTests.q15().
~~~java
assertIds(repository.findByMarqueOrderByTarifJournalierAsc("Toyota"),"T1","T2"); assertPrices(repository.findByMarqueOrderByTarifJournalierAsc("Toyota"),"50","100");
~~~

## 16. Marque ET tarif supérieur

**Besoin :** Marque ET tarif supérieur.

**Méthode Repository :**
~~~java
List<Vehicule> findByMarqueAndTarifJournalierGreaterThan(String marque, BigDecimal prix);
~~~

**Keyword et sens :** And, GreaterThan. Chaque véhicule doit satisfaire les deux conditions.

**Exemple d'appel :**
~~~java
repository.findByMarqueAndTarifJournalierGreaterThan("Toyota", money("50"));
// Appel depuis le service :
service.findByMarqueAndTarifJournalierGreaterThan("Toyota", money("50"));
~~~

**Résultat attendu sur H2 :** T2.

**Endpoint :** `GET /vehicule/keywords/marque/prix-greater-than?marque=Toyota&prix=50`.

**Test :** VehiculeKeywordsRepositoryTests.q16().
~~~java
assertIds(repository.findByMarqueAndTarifJournalierGreaterThan("Toyota", money("50")),"T2");
~~~

## 17. Marque OU modèle

**Besoin :** Marque OU modèle.

**Méthode Repository :**
~~~java
List<Vehicule> findByMarqueOrModele(String marque, String modele);
~~~

**Keyword et sens :** Or. Une condition suffit ; une ligne satisfaisant les deux ne se répète pas.

**Exemple d'appel :**
~~~java
repository.findByMarqueOrModele("Toyota", "Yaris");
// Appel depuis le service :
service.findByMarqueOrModele("Toyota", "Yaris");
~~~

**Résultat attendu sur H2 :** T1,T2,T3,B1.

**Endpoint :** `GET /vehicule/keywords/marque-or-modele?marque=Toyota&modele=Yaris`.

**Test :** VehiculeKeywordsRepositoryTests.q17().
~~~java
assertIds(repository.findByMarqueOrModele("Toyota", "Yaris"),"T1","T2","T3","B1");
~~~

## 18. Marques dans une liste

**Besoin :** Marques dans une liste.

**Méthode Repository :**
~~~java
List<Vehicule> findByMarqueIn(List<String> marques);
~~~

**Keyword et sens :** In. La marque doit être égale à une des valeurs de la liste.

**Exemple d'appel :**
~~~java
repository.findByMarqueIn(List.of("Toyota", "Ford"));
// Appel depuis le service :
service.findByMarqueIn(List.of("Toyota", "Ford"));
~~~

**Résultat attendu sur H2 :** T1,T2,F1.

**Endpoint :** `GET /vehicule/keywords/marques/in?marques=Toyota,Ford`.

**Test :** VehiculeKeywordsRepositoryTests.q18().
~~~java
assertIds(repository.findByMarqueIn(List.of("Toyota", "Ford")),"T1","T2","F1");
~~~

## 19. Marques hors de la liste

**Besoin :** Marques hors de la liste.

**Méthode Repository :**
~~~java
List<Vehicule> findByMarqueNotIn(List<String> marques);
~~~

**Keyword et sens :** NotIn. La marque ne doit appartenir à aucune valeur de la liste.

**Exemple d'appel :**
~~~java
repository.findByMarqueNotIn(List.of("Toyota", "Ford"));
// Appel depuis le service :
service.findByMarqueNotIn(List.of("Toyota", "Ford"));
~~~

**Résultat attendu sur H2 :** T3,R1,B1,H1,K1.

**Endpoint :** `GET /vehicule/keywords/marques/not-in?marques=Toyota,Ford`.

**Test :** VehiculeKeywordsRepositoryTests.q19().
~~~java
assertIds(repository.findByMarqueNotIn(List.of("Toyota", "Ford")),"T3","R1","B1","H1","K1");
~~~

## 20. Filtrer par statut

**Besoin :** Filtrer par statut.

**Méthode Repository :**
~~~java
List<Vehicule> findByStatut(StatutVehicule statut);
~~~

**Keyword et sens :** findBy. Compare une valeur enum StatutVehicule.

**Exemple d'appel :**
~~~java
repository.findByStatut(StatutVehicule.DISPONIBLE);
// Appel depuis le service :
service.findByStatut(StatutVehicule.DISPONIBLE);
~~~

**Résultat attendu sur H2 :** T1,T3,R1,B1,K1.

**Endpoint :** `GET /vehicule/keywords/statut?statut=DISPONIBLE`.

**Test :** VehiculeKeywordsRepositoryTests.q20().
~~~java
assertIds(repository.findByStatut(StatutVehicule.DISPONIBLE),"T1","T3","R1","B1","K1"); assertIds(repository.findByStatut(StatutVehicule.LOUE),"T2","H1"); assertIds(repository.findByStatut(StatutVehicule.MAINTENANCE),"F1");
~~~

## 21. Disponibles au tarif croissant

**Besoin :** Disponibles au tarif croissant.

**Méthode Repository :**
~~~java
List<Vehicule> findByStatutOrderByTarifJournalierAsc(StatutVehicule statut);
~~~

**Keyword et sens :** OrderBy, Asc. Filtre le statut avant de trier ; AVAILABLE est nommé DISPONIBLE ici.

**Exemple d'appel :**
~~~java
repository.findByStatutOrderByTarifJournalierAsc(StatutVehicule.DISPONIBLE);
// Appel depuis le service :
service.findAvailableOrderByTarifJournalierAsc();
~~~

**Résultat attendu sur H2 :** T1,T3,R1,K1,B1.

Le nom dérivé ne fixe pas la valeur enum DISPONIBLE. Le Repository reçoit un statut ; le service fixe ce paramètre à StatutVehicule.DISPONIBLE. L’ordre des deux véhicules à 80 reste libre.

**Endpoint :** `GET /vehicule/keywords/disponibles/prix-asc`.

**Test :** VehiculeKeywordsRepositoryTests.q21().
~~~java
assertIds(repository.findByStatutOrderByTarifJournalierAsc(StatutVehicule.DISPONIBLE),"T1","T3","R1","K1","B1"); assertPrices(repository.findByStatutOrderByTarifJournalierAsc(StatutVehicule.DISPONIBLE),"50","75","80","80","150");
~~~

## 22. Véhicules d’une agence

**Besoin :** Véhicules d’une agence.

**Méthode Repository :**
~~~java
List<Vehicule> findByAgence_IdAgence(Long agenceId);
~~~

**Keyword et sens :** findBy, _. Parcourt agence.idAgence ; le soulignement explicite la relation.

**Exemple d'appel :**
~~~java
repository.findByAgence_IdAgence(agencyA.getIdAgence());
// Appel depuis le service :
service.findByAgence_IdAgence(agencyA.getIdAgence());
~~~

**Résultat attendu sur H2 :** T1,T2,K1.

**Endpoint :** `GET /vehicule/keywords/agence?agenceId=<idA>`.

**Test :** VehiculeKeywordsRepositoryTests.q22().
~~~java
assertIds(repository.findByAgence_IdAgence(agencyA.getIdAgence()),"T1","T2","K1"); assertIds(repository.findByAgence_IdAgence(emptyAgency.getIdAgence()));
~~~

## 23. Agences dans une ville

**Besoin :** Agences dans une ville.

**Méthode Repository :**
~~~java
List<Vehicule> findByAgence_Ville(String ville);
~~~

**Keyword et sens :** findBy, _. Parcourt agence.ville : plusieurs agences peuvent partager la ville.

**Exemple d'appel :**
~~~java
repository.findByAgence_Ville("Tunis");
// Appel depuis le service :
service.findByAgence_Ville("Tunis");
~~~

**Résultat attendu sur H2 :** T1,T2,K1,B1.

**Endpoint :** `GET /vehicule/keywords/agence/ville?ville=Tunis`.

**Test :** VehiculeKeywordsRepositoryTests.q23().
~~~java
assertIds(repository.findByAgence_Ville("Tunis"),"T1","T2","K1","B1");
~~~

## 24. Nom d’agence contenant un texte

**Besoin :** Nom d’agence contenant un texte.

**Méthode Repository :**
~~~java
List<Vehicule> findByAgence_NomContaining(String value);
~~~

**Keyword et sens :** _, Containing. Cherche le texte dans agence.nom, pas dans la marque du véhicule.

**Exemple d'appel :**
~~~java
repository.findByAgence_NomContaining("AutoLoc");
// Appel depuis le service :
service.findByAgence_NomContaining("AutoLoc");
~~~

**Résultat attendu sur H2 :** T1,T2,T3,R1,K1.

**Endpoint :** `GET /vehicule/keywords/agence/nom/contains?value=AutoLoc`.

**Test :** VehiculeKeywordsRepositoryTests.q24().
~~~java
assertIds(repository.findByAgence_NomContaining("AutoLoc"),"T1","T2","T3","R1","K1");
~~~

## 25. Agence ET marque

**Besoin :** Agence ET marque.

**Méthode Repository :**
~~~java
List<Vehicule> findByAgence_IdAgenceAndMarque(Long agenceId, String marque);
~~~

**Keyword et sens :** _, And. Les deux conditions doivent être satisfaites.

**Exemple d'appel :**
~~~java
repository.findByAgence_IdAgenceAndMarque(agencyA.getIdAgence(), "Toyota");
// Appel depuis le service :
service.findByAgence_IdAgenceAndMarque(agencyA.getIdAgence(), "Toyota");
~~~

**Résultat attendu sur H2 :** T1,T2.

**Endpoint :** `GET /vehicule/keywords/agence/marque?agenceId=<idA>&marque=Toyota`.

**Test :** VehiculeKeywordsRepositoryTests.q25().
~~~java
assertIds(repository.findByAgence_IdAgenceAndMarque(agencyA.getIdAgence(), "Toyota"),"T1","T2"); assertIds(repository.findByAgence_IdAgenceAndMarque(agencyB.getIdAgence(),"Toyota"));
~~~

## 26. Sans agence

**Besoin :** Sans agence.

**Méthode Repository :**
~~~java
List<Vehicule> findByAgenceIsNull();
~~~

**Keyword et sens :** IsNull. Teste l’absence de relation sans paramètre null.

**Exemple d'appel :**
~~~java
repository.findByAgenceIsNull();
// Appel depuis le service :
service.findByAgenceIsNull();
~~~

**Résultat attendu sur H2 :** F1,H1.

**Endpoint :** `GET /vehicule/keywords/agence/is-null`.

**Test :** VehiculeKeywordsRepositoryTests.q26().
~~~java
assertIds(repository.findByAgenceIsNull(),"F1","H1");
~~~

## 27. Avec agence

**Besoin :** Avec agence.

**Méthode Repository :**
~~~java
List<Vehicule> findByAgenceIsNotNull();
~~~

**Keyword et sens :** IsNotNull. Teste la présence de la relation vers une agence.

**Exemple d'appel :**
~~~java
repository.findByAgenceIsNotNull();
// Appel depuis le service :
service.findByAgenceIsNotNull();
~~~

**Résultat attendu sur H2 :** T1,T2,T3,R1,B1,K1.

**Endpoint :** `GET /vehicule/keywords/agence/is-not-null`.

**Test :** VehiculeKeywordsRepositoryTests.q27().
~~~java
assertIds(repository.findByAgenceIsNotNull(),"T1","T2","T3","R1","B1","K1");
~~~

## 28. Compter les véhicules d’une agence

**Besoin :** Compter les véhicules d’une agence.

**Méthode Repository :**
~~~java
long countByAgence_IdAgence(Long agenceId);
~~~

**Keyword et sens :** countBy, _. Compte les véhicules liés à cet identifiant.

**Exemple d'appel :**
~~~java
repository.countByAgence_IdAgence(agencyA.getIdAgence());
// Appel depuis le service :
service.countByAgence_IdAgence(agencyA.getIdAgence());
~~~

**Résultat attendu sur H2 :** 3.

**Endpoint :** `GET /vehicule/keywords/agence/count?agenceId=<idA>`.

**Test :** VehiculeKeywordsRepositoryTests.q28().
~~~java
assertEquals(3,repository.countByAgence_IdAgence(agencyA.getIdAgence())); assertEquals(0,repository.countByAgence_IdAgence(emptyAgency.getIdAgence()));
~~~

## 29. L’agence possède-t-elle un véhicule ?

**Besoin :** L’agence possède-t-elle un véhicule ?.

**Méthode Repository :**
~~~java
boolean existsByAgence_IdAgence(Long agenceId);
~~~

**Keyword et sens :** existsBy, _. Teste la présence de véhicules, et non la présence de l’agence elle-même.

**Exemple d'appel :**
~~~java
repository.existsByAgence_IdAgence(agencyA.getIdAgence());
// Appel depuis le service :
service.existsByAgence_IdAgence(agencyA.getIdAgence());
~~~

**Résultat attendu sur H2 :** true.

**Endpoint :** `GET /vehicule/keywords/agence/exists?agenceId=<idA>`.

**Test :** VehiculeKeywordsRepositoryTests.q29().
~~~java
assertTrue(repository.existsByAgence_IdAgence(agencyA.getIdAgence())); assertFalse(repository.existsByAgence_IdAgence(emptyAgency.getIdAgence()));
~~~

## Exécuter et présenter

~~~powershell
$env:JAVA_HOME = 'C:\Users\Eya\.jdks\ms-17.0.16'
.\mvnw.cmd -Dtest=VehiculeKeywordsRepositoryTests test
.\mvnw.cmd clean compile test package
~~~

Les tests de repository vérifient aussi : aucun résultat, bornes égales de
Between, %/_ littéraux, et base vide. Les tests HTTP vérifient les 29 routes,
les paramètres erronés et Swagger. Les anciens tests restent inchangés.

Démarrer l'application avec la configuration locale habituelle, puis ouvrir
http://localhost:8080/swagger-ui/index.html et le groupe Véhicules — Keywords JPA.
Les résultats HTTP dépendent de vos données, pas du jeu de test H2.

Pour IN/NOT IN, passer marques=Toyota,Ford ou répéter le paramètre marques.
Le service refuse une liste vide (400), pour éviter les différences entre bases.
Un identifiant d'agence positif absent renvoie [] / 0 / false : ce n'est pas une
lecture directe d'agence. Les prix sont non négatifs ; minimum <= maximum.
Un statut inconnu, texte vide ou identifiant non positif renvoie 400.

## Références techniques

[Requêtes JPA dérivées](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
et [Keywords officiels](https://docs.spring.io/spring-data/commons/reference/repositories/query-keywords-reference.html).

## Bilan vérifié — 15 novembre 2026

| Exercices | Sujet | Résultat |
| --- | --- | --- |
| 1 à 8 | Textes, existence, comptage | 8/8 validés |
| 9 à 19 | Prix, bornes, tris, ET/OU, listes | 11/11 validés |
| 20 à 21 | Statuts et disponibles triés | 2/2 validés |
| 22 à 29 | Relations avec les agences | 8/8 validés |

La compilation et le packaging ont réussi avec le Maven Wrapper et le JDK 17.
Le build complet a exécuté 112 tests, sans échec, erreur ni test ignoré :
33 tests Repository (dont q01 à q29), 41 tests HTTP des Keywords et 38 tests
préexistants inchangés. Aucune assertion existante n'a été supprimée ou affaiblie.

Le JAR packagé a démarré sur le port de vérification 18080 avec la base locale,
ddl-auto=validate et création automatique de base désactivée. Les 29 endpoints
Keywords et Swagger ont répondu 200 ; OpenAPI contient les 29 nouvelles routes.
Les vérifications sur cette base étaient uniquement des lectures. L'application
temporaire a été arrêtée ensuite. L'avertissement Hibernate sur l'ancienne version
MySQL annoncée par le serveur local subsiste ; aucun changement de serveur ou de
schéma n'a été effectué.

Fichiers modifiés : VehiculeRepository.java, IVehiculeService.java,
VehiculeServiceIMPL.java. Fichiers ajoutés : VehiculeKeywordsController.java,
VehiculeKeywordsRepositoryTests.java, VehiculeKeywordsHttpTests.java et ce guide.
AgenceRepository, les entités, le contrôleur CRUD existant, le POM, la configuration
de base de données et les anciens tests sont inchangés.

Branche de livraison : feature/autoloc-jpa-keywords, issue du travail précédent
conservé au commit 707da06. Aucun historique n'a été réécrit et main est inchangée.
