package tn.esprit.autoloc.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import tn.esprit.autoloc.domain.*;

import java.util.List;

public record ClientReservationsRequest(
        @NotNull @Valid Client client, @NotEmpty List<@NotNull @Valid Reservation> reservations) {}
