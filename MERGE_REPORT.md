# AutoLoc integration report — 8 November 2026

## Preservation and comparison

Original directory: `C:\Users\Eya\IdeaProjects\autoloc-api`.
Full pre-change backup, including `.git`, ignored files, and uncommitted work:
`C:\Users\Eya\IdeaProjects\autoloc-api-backup-20261108-210927`.

Original branch `main` and remote `main` both started at
`7f20c79b3215a55ddaefdf07074f7988d32b118b`. The original working tree had nine
modified domain files containing JPA relationships; those relationships were
preserved and included in the integration commit. No original project files
were deleted. The classmate's source directory was only read.

Both POMs declare Spring Boot 4.1.1 and Java 17. The original project uniquely
provided explicit vehicle constraints, named foreign keys and join table,
initialized relationship collections, and the validation dependency. The source
provided CRUD modules and Swagger annotations. Neither input had DTOs, custom
security, or sophisticated rental business services.

The one original application entry point, package `tn.esprit.autoloc`, all nine
entities, all five enums, Maven Wrapper, and existing Git history remain.

## Integrated functionality and conflict resolution

- Adapted nine repositories, nine service interfaces, nine implementations,
  and nine REST controllers from Mahdi Hamzaoui's project. All 45 existing
  classmate routes remain available under their original module prefixes.
- Kept the original entity package and database names instead of copying
  duplicate source entities or a second application entry point.
- Kept `Contrat.reservation` as the one-to-one owner, with the unique
  `id_reservation` column; the source had the opposite ownership. Clients link
  contracts through `contrat.reservation`; see README for request semantics.
- Kept the original `vehicule_equipement` join table and all explicit join
  columns. All `mappedBy` references resolve to owning entity properties.
- Omitted inverse associations from JSON responses to avoid cycles while
  retaining JPA relationships. Source contract-payment cascade behavior is
  retained, and nested payment requests now set each payment's owning contract.
- Disabled Jackson's inferred all-arguments constructor for `Contrat` so an
  omitted primitive `valide` field retains its Java default of false.
- Added HTTP 409 handling for database integrity conflicts without exposing SQL.
- Replaced incompatible source Springdoc 2.1.0 with 3.1.1 for Boot 4.1, per the
  [Springdoc compatibility matrix](https://springdoc.org/index.html).
- Kept original local database defaults while allowing `DB_URL`, `DB_USERNAME`,
  and `DB_PASSWORD` environment overrides. No source database configuration or
  credentials were copied. Ignore rules cover local secret/config files.
- Added attribution in NOTICE and operating/API instructions in README.

## Changed files

Modified: `.gitignore`, `pom.xml`, `src/main/resources/application.properties`,
the nine entity files in `src/main/java/tn/esprit/autoloc/domain/`, and
`src/test/java/tn/esprit/autoloc/AutolocApiApplicationTests.java`.

Added: nine controllers plus `ApiExceptionHandler` in the `controller` package;
nine repositories in `repository`; nine interfaces and nine implementations in
`service`; `CrudIntegrationTests.java`; test-only `application-test.properties`;
README, NOTICE, and this report. Removed: none.

## Build and test evidence

Used installed Microsoft JDK 17.0.16 and Maven Wrapper 3.9.16. System PATH's Java
11 is insufficient; system Maven is absent. No project Java/Boot upgrade was
needed. A clean rebuild removed stale incompatible class files during testing.

`mvnw.cmd clean compile` succeeded. Final
`mvnw.cmd -B clean compile test package` succeeded, producing
`target/autoloc-api-0.0.1-SNAPSHOT.jar`.

All 13 tests passed, with zero failures/errors/skips: context startup; HTTP CRUD
for nine modules; linked domain persistence and cycle-free serialization;
one-to-one uniqueness returning HTTP 409; nested payment persistence/deletion;
Swagger UI and OpenAPI coverage. Tests use isolated H2 in MySQL compatibility
mode and do not modify the user's database.

A separate read-only JDBC probe connected to the original local database and
found all nine entity tables plus `vehicule_equipement`. The local server is
MariaDB 10.4.32 exposed through the MySQL connector.

The packaged application started successfully on port 18080 against that local
database with `ddl-auto=validate` and database auto-creation disabled. All nine
`GET /{module}/getAll` endpoints, Swagger UI, and `/v3/api-docs` returned HTTP
200. Only reads were used against this database. The temporary verification
application was stopped afterward.

Hibernate warned that MariaDB reports version 5.5.5 through the selected MySQL
dialect, below that dialect's supported minimum of MySQL 8.0. Startup/schema
validation and the read checks succeeded, but a supported database server and
matching driver/dialect should be used for deployment. No database upgrade or
schema change was performed as part of these checks.

## Git and remaining considerations

Integration branch: `integration/autoloc-springboot-merge`.
Remote: `https://github.com/eyabenhassyne/AUTOLOC.git`.
No force push, reset, unrelated-history merge, or update of remote `main` is used.

Review the branch comparison, then merge a pull request into `main` through
GitHub's normal merge action. The exact commit is available in `git log -1`.

Existing CRUD semantics remain: missing lookups return null, and updates use
repository save. Parent deletes can conflict with dependent records; contract
deletion intentionally cascades its payments as in the source. Authentication,
DTOs, and advanced booking rules are absent in both inputs. Configure an
appropriate database user/password for deployment and a Java 17+ Maven runner.
