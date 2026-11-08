package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Client;

import java.util.List;

public interface IClientService {
    Client create(Client client);

    Client getById(Long id);

    List<Client> getAll();

    Client update(Client client);

    void delete(Long id);
}
