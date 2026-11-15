package tn.esprit.autoloc.service;

import jakarta.persistence.*;
import jakarta.validation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import tn.esprit.autoloc.domain.*;

import java.util.*;

@Component
@RequiredArgsConstructor
public class EntitySupport {
    @PersistenceContext private EntityManager em;
    private final Validator validator;

    public <T> T require(Class<T> type, Long id) {
        positiveId(id);
        T value = em.find(type, id);
        if (value == null)
            throw new EntityNotFoundException(type.getSimpleName() + " " + id + " was not found");
        return value;
    }

    public <T> T lock(Class<T> type, Long id) {
        positiveId(id);
        T value = em.find(type, id, LockModeType.PESSIMISTIC_WRITE);
        if (value == null)
            throw new EntityNotFoundException(type.getSimpleName() + " " + id + " was not found");
        return value;
    }

    public void positiveId(Long id) {
        if (id == null || id <= 0)
            throw new IllegalArgumentException("A positive identifier is required");
    }

    public void checkNew(Object value) {
        if (value == null) throw new IllegalArgumentException("An entity body is required");
        if (em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(value) != null)
            throw new IllegalArgumentException("New records must not contain an identifier");
    }

    public void validate(Object value) {
        var violations = validator.validate(value);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);
        if (value instanceof Reservation r
                && r.getDateDebut() != null
                && r.getDateFin() != null
                && r.getDateFin().isBefore(r.getDateDebut()))
            throw new IllegalArgumentException("Reservation end date must not precede start date");
        if (value instanceof Maintenance m
                && m.getDateDebut() != null
                && m.getDateFin() != null
                && m.getDateFin().isBefore(m.getDateDebut()))
            throw new IllegalArgumentException("Maintenance end date must not precede start date");
    }

    public void prepare(Object value) {
        validate(value);
        if (value instanceof Employe e && e.getAgence() != null)
            e.setAgence(require(Agence.class, e.getAgence().getIdAgence()));
        if (value instanceof Vehicule v) {
            if (v.getAgence() != null)
                v.setAgence(require(Agence.class, v.getAgence().getIdAgence()));
            var unique = new LinkedHashMap<Long, Equipement>();
            if (v.getEquipements() != null)
                for (Equipement e : v.getEquipements()) {
                    if (e == null)
                        throw new IllegalArgumentException("Equipment references cannot be null");
                    Equipement saved = require(Equipement.class, e.getIdEquipement());
                    unique.put(saved.getIdEquipement(), saved);
                }
            v.setEquipements(new ArrayList<>(unique.values()));
        }
        if (value instanceof Maintenance m && m.getVehicule() != null)
            m.setVehicule(require(Vehicule.class, m.getVehicule().getIdVehicule()));
        if (value instanceof Reservation r) {
            if (r.getClient() != null)
                r.setClient(require(Client.class, r.getClient().getIdClient()));
            if (r.getVehicule() != null)
                r.setVehicule(require(Vehicule.class, r.getVehicule().getIdVehicule()));
        }
        if (value instanceof Contrat c && c.getReservation() != null)
            c.setReservation(require(Reservation.class, c.getReservation().getIdReservation()));
        if (value instanceof Paiement q && q.getContrat() != null)
            q.setContrat(require(Contrat.class, q.getContrat().getIdContrat()));
    }
}
