package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.dto.*;
import tn.esprit.autoloc.repository.*;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ReservationServiceIMPL implements IReservationService {
    private final ReservationRepository repository;
    private final EntitySupport support;
    private final IContratService contratService;

    @Override
    public Reservation create(Reservation input) {
        support.checkNew(input);
        support.prepare(input);

        return repository.save(input);
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation getById(Long id) {
        return support.require(Reservation.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getAll() {
        return repository.findAll();
    }

    @Override
    public Reservation update(Reservation input) {
        Reservation existing = getById(input.getIdReservation());
        support.prepare(input);
        existing.setDateDebut(input.getDateDebut());
        existing.setDateFin(input.getDateFin());
        existing.setStatut(input.getStatut());
        if (input.getClient() != null) existing.setClient(input.getClient());
        if (input.getVehicule() != null) existing.setVehicule(input.getVehicule());

        return existing;
    }

    @Override
    public void delete(Long id) {
        Reservation reservation = getById(id);
        if (reservation.getContrat() != null) {
            throw new org.springframework.dao.DataIntegrityViolationException(
                    "Delete the associated contract before deleting the reservation");
        }
        repository.delete(reservation);
        repository.flush();
    }

    @Override
    public ReservationContractResponse createWithContract(ReservationContractRequest request) {
        support.checkNew(request.contrat());
        if (request.contrat().getReservation() != null)
            throw new IllegalArgumentException("The contract inherits the new reservation");
        Reservation saved = create(request.reservation());
        request.contrat().setReservation(saved);
        Contrat contrat = contratService.create(request.contrat());
        saved.setContrat(contrat);
        return new ReservationContractResponse(saved, contrat);
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat getContract(Long id) {
        Contrat c = getById(id).getContrat();
        if (c == null)
            throw new jakarta.persistence.EntityNotFoundException(
                    "The reservation has no contract");
        return c;
    }
}
