package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Reservation;

import java.util.List;

public interface IReservationService {
    Reservation create(Reservation reservation);

    Reservation getById(Long id);

    List<Reservation> getAll();

    Reservation update(Reservation reservation);

    void delete(Long id);

    tn.esprit.autoloc.dto.ReservationContractResponse createWithContract(
            tn.esprit.autoloc.dto.ReservationContractRequest request);

    tn.esprit.autoloc.domain.Contrat getContract(Long id);
}
