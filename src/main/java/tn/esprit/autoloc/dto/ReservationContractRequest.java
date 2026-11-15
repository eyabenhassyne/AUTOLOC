package tn.esprit.autoloc.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import tn.esprit.autoloc.domain.*;

public record ReservationContractRequest(
        @NotNull @Valid Reservation reservation, @NotNull @Valid Contrat contrat) {}
