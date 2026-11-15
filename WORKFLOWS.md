# AutoLoc workflows

The application still uses Java 17, Spring Boot 4.1.1, the same packages and
database configuration. Existing /create, /get/{id}, /getAll, /update and
/delete/{id} routes remain available with their successful HTTP 200 responses.

Each singular module prefix also supports POST / (201 with Location), GET /
(200), GET /{id} (200), PUT /{id} (200) and DELETE /{id} (204). Do not include a
record ID on creation. Updates require an existing record; an optional body ID
must match the path. Missing records/references return 404, invalid input 400,
and unique/foreign-key conflicts 409 as application/problem+json.

## Create a client with reservations

POST /client/with-reservations returns 201:

~~~json
{
  "client": {"nom": "Example", "email": "client@example.com"},
  "reservations": [
    {"dateDebut": "2026-11-16", "dateFin": "2026-11-18", "statut": "EN_ATTENTE"},
    {"dateDebut": "2026-11-20", "dateFin": "2026-11-21"}
  ]
}
~~~

The response contains client and reservations, including generated IDs.
Reservations inherit the new client; do not provide their client field or IDs.
An optional vehicule must reference an existing idVehicule.
GET /client/{id}/reservations retrieves the persisted children.

## Create a reservation with its contract

POST /reservation/with-contract returns 201:

~~~json
{
  "reservation": {
    "dateDebut": "2026-11-16", "dateFin": "2026-11-18", "statut": "CONFIRMEE"
  },
  "contrat": {
    "dateSignature": "2026-11-15", "montantTotal": 150, "valide": true,
    "paiements": [{"montant": 50, "modePaiement": "CARTE"}]
  }
}
~~~

The response contains reservation and contrat. The service sets the contract's
owning reservation foreign key; do not supply contrat.reservation.
GET /reservation/{id}/contrat retrieves the contract. Reservation client and
vehicle references, if supplied, must already exist.

Both nested creation operations are transactional: an invalid child rolls back
the entire operation. No delete cascade was added to clients or reservations.
A reservation with an existing contract cannot be deleted until the contract
is deleted or unlinked through domain operations.

## Assign existing records

PUT /employe/{employeId}/agence/{agenceId}

PUT /vehicule/{vehiculeId}/equipement/{equipementId}

DELETE /vehicule/{vehiculeId}/equipement/{equipementId}

PUT /maintenance/{maintenanceId}/vehicule/{vehiculeId}

These requests have no body and return the updated entity with HTTP 200.
Both records must exist. Equipment assignment is idempotent, including concurrent
requests; a vehicle row lock serializes link changes. Unlinking equipment keeps
both records. Reassigning an employee or maintenance record replaces its current
owner. Failed assignment leaves the original owner unchanged.

## Contract payments

GET /contrat/{id}/paiements lists payments.

POST /contrat/{id}/paiements creates a payment (201 with Location):

~~~json
{"montant": 25.50, "datePaiement": "2026-11-15", "modePaiement": "CARTE"}
~~~

Nested contrat.paiements input can contain new payments or an existing unassigned
payment reference such as {"idPaiement": 1}. Existing payment scalar fields are
preserved; a payment already belonging to another contract returns 409. Use the
payment's ordinary update endpoint to change its fields.

The original contract-to-payment cascade is retained: deleting a contract deletes
its linked payments. It does not delete its reservation. No cascade deletes were
added to vehicles, equipment, agencies, employees, maintenance or clients.

## Validation and serialization

Vehicle required fields, lengths and price precision are validated before SQL.
Email format, positive payment amounts, nonnegative contract totals, positive IDs
and chronological date ranges are checked. Optional legacy scalar fields remain
optional. Blank/missing bodies, invalid JSON/enums, inconsistent IDs, unknown
association references and invalid nested records are rejected.

Inverse fields remain omitted from entity JSON to prevent recursion. Compound
workflow responses and association GET routes expose the records needed by
clients without changing the original entity representation.

Updates replace scalar fields using the supplied body. Omitted/null owning
associations are preserved. An empty equipment array also preserves current links;
use the explicit DELETE association route to remove links. Nested contract payment
input adds/links payments; omission never deletes previous payments.

## Verify

Run the Maven Wrapper with a Java 17+ JDK:
~~~powershell
.\mvnw.cmd clean compile
.\mvnw.cmd test
.\mvnw.cmd package
~~~

The original tests remain unchanged. WorkflowIntegrationTests uses the existing
isolated H2 profile and verifies persistence, rollback, errors, legacy/canonical
CRUD, payment preservation/deletion, concurrent assignments and Swagger coverage.

Swagger UI is at http://localhost:8080/swagger-ui/index.html; OpenAPI is at
/v3/api-docs. A deployment still needs its own database credentials and an
appropriate supported MySQL server. Existing development database settings are
unchanged.
