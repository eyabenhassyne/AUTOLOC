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
public class ClientServiceIMPL implements IClientService {
    private final ClientRepository repository;
    private final EntitySupport support;
    private final IReservationService reservationService;

    @Override
    public Client create(Client input) {
        support.checkNew(input);
        support.prepare(input);

        return repository.save(input);
    }

    @Override
    @Transactional(readOnly = true)
    public Client getById(Long id) {
        return support.require(Client.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> getAll() {
        return repository.findAll();
    }

    @Override
    public Client update(Client input) {
        Client existing = getById(input.getIdClient());
        support.prepare(input);
        existing.setNom(input.getNom());
        existing.setPrenom(input.getPrenom());
        existing.setEmail(input.getEmail());
        existing.setTelephone(input.getTelephone());
        existing.setNumPermis(input.getNumPermis());
        existing.setDateInscription(input.getDateInscription());

        return existing;
    }

    @Override
    public void delete(Long id) {
        repository.delete(getById(id));
        repository.flush();
    }

    @Override
    public ClientReservationsResponse createWithReservations(ClientReservationsRequest request) {
        Client saved = create(request.client());
        var reservations = new java.util.ArrayList<Reservation>();
        for (Reservation reservation : request.reservations()) {
            support.checkNew(reservation);
            if (reservation.getClient() != null)
                throw new IllegalArgumentException("Nested reservations inherit the new client");
            reservation.setClient(saved);
            reservations.add(reservationService.create(reservation));
        }
        return new ClientReservationsResponse(saved, reservations);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservations(Long id) {
        return List.copyOf(getById(id).getReservations());
    }
}
