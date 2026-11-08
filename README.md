# AutoLoc API

Single Spring Boot application for car rental management, based on Eya's
original `autoloc-api` and adapted CRUD modules from Mahdi Hamzaoui's project
(see [NOTICE](NOTICE)). Java 17 and Spring Boot 4.1.1 are retained.

## Run on Windows

Use a Java 17+ JDK. For the Java 17 installation on this computer:

```powershell
$env:JAVA_HOME = 'C:\Users\Eya\.jdks\ms-17.0.16'
.\mvnw.cmd clean compile
.\mvnw.cmd test
.\mvnw.cmd package
```

Maven is supplied by the wrapper; a system Maven installation is unnecessary.
Configure IntelliJ's project SDK and Maven runner to use the same JDK. If class
version errors occur after an IDE build, run `clean` before the Maven build.

MySQL defaults to `localhost:3306/autoloc_db`, username `root`, empty password,
matching the original local configuration. Override credentials in your shell:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/autoloc_db'
$env:DB_USERNAME = 'your_database_user'
$env:DB_PASSWORD = 'your_local_password'
java -jar target\autoloc-api-0.0.1-SNAPSHOT.jar
```

Use `$env:JAVA_HOME\bin\java.exe` if the `java` command on PATH is Java 11.
Default JPA schema mode remains `update`, as in the original application; for
an existing managed database, use `--spring.jpa.hibernate.ddl-auto=validate`.
Do not commit local passwords or environment files.

Swagger UI: <http://localhost:8080/swagger-ui/index.html>

OpenAPI JSON: <http://localhost:8080/v3/api-docs>

## REST API

Each prefix supports the same original classmate routes:

| Prefix | Entity ID property |
| --- | --- |
| `/agence` | `idAgence` |
| `/employe` | `idEmploye` |
| `/vehicule` | `idVehicule` |
| `/maintenance` | `idMaintenance` |
| `/equipement` | `idEquipement` |
| `/client` | `idClient` |
| `/reservation` | `idReservation` |
| `/contrat` | `idContrat` |
| `/paiement` | `idPaiement` |

For each prefix: `POST /create`, `GET /get/{id}`, `GET /getAll`,
`PUT /update` (include the entity ID), and `DELETE /delete/{id}`.

Send associations through their owning fields, referencing existing IDs:
`employe.agence`, `vehicule.agence`, `vehicule.equipements`,
`maintenance.vehicule`, `reservation.client`, `reservation.vehicule`,
`contrat.reservation`, and `paiement.contrat`.

The original model owns the one-to-one foreign key at
`contrat.id_reservation`; create a reservation, then create/update its contract
with `{"reservation":{"idReservation":1}}`. A reservation cannot have two
contracts. The explicit `vehicule_equipement` join table is retained.

Inverse collections and `reservation.contrat` stay mapped in JPA but are omitted
from JSON to prevent recursion. Retrieve these through their module endpoints.
`contrat.paiements` accepts nested payment input, links the owning contract, and
retains the source project's cascade behavior (including deletion of payments
when their contract is deleted). It is omitted from contract responses to avoid
cycles. Other dependent records must be removed/unlinked before deleting their
parents; no broad new deletion cascades were introduced.

Vehicles retain the original required fields and unique registration constraint.
Both input projects use entities directly, with no DTO layer, authentication, or
advanced booking/payment business rules. CRUD semantics are retained, including
null responses for missing records and repository-save behavior on updates.

## Verification

Tests automatically use an isolated H2 database in MySQL compatibility mode;
they do not require or modify your MySQL database. They exercise application
startup, CRUD over HTTP for all nine modules, relationship persistence,
one-to-one uniqueness, contract payment cascading, Swagger UI, and OpenAPI.
H2 tests do not substitute for deployment verification against MySQL.

## Git integration

The integration is on `integration/autoloc-springboot-merge`. After reviewing
the GitHub comparison and green checks, merge its pull request into `main` using
GitHub's normal merge action. No force push or history rewrite is required.
