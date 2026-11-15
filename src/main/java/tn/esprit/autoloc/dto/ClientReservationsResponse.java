package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.*;

import tn.esprit.autoloc.domain.*;

import java.util.List;

public record ClientReservationsResponse(Client client, List<Reservation> reservations) {}
