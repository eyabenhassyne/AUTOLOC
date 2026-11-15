package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.*;

import tn.esprit.autoloc.domain.*;

public record ReservationContractResponse(Reservation reservation, Contrat contrat) {}
