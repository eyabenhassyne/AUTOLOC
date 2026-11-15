package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Client;

import java.util.List;

public interface IClientService {
    Client create(Client client);

    Client getById(Long id);

    List<Client> getAll();

    Client update(Client client);

    void delete(Long id);

    tn.esprit.autoloc.dto.ClientReservationsResponse createWithReservations(
            tn.esprit.autoloc.dto.ClientReservationsRequest request);

    List<tn.esprit.autoloc.domain.Reservation> getReservations(Long id);
}
