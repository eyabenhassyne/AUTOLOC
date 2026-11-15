package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.repository.*;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ContratServiceIMPL implements IContratService {
    private final ContratRepository repository;
    private final EntitySupport support;
    private final PaiementRepository paiementRepository;

    @Override
    public Contrat create(Contrat input) {
        support.checkNew(input);
        support.prepare(input);
        attachPayments(input, input);
        return repository.save(input);
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat getById(Long id) {
        return support.require(Contrat.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> getAll() {
        return repository.findAll();
    }

    @Override
    public Contrat update(Contrat input) {
        Contrat existing = getById(input.getIdContrat());
        support.prepare(input);
        existing.setDateSignature(input.getDateSignature());
        existing.setMontantTotal(input.getMontantTotal());
        existing.setValide(input.isValide());
        if (input.getReservation() != null) existing.setReservation(input.getReservation());
        attachPayments(input, existing);

        return existing;
    }

    @Override
    public void delete(Long id) {
        Contrat contract = getById(id);
        if (contract.getReservation() != null) {
            contract.getReservation().setContrat(null);
            contract.setReservation(null);
        }
        repository.delete(contract);
        repository.flush();
    }

    private void attachPayments(Contrat input, Contrat target) {
        var requested =
                input.getPaiements() == null
                        ? List.<Paiement>of()
                        : new java.util.ArrayList<>(input.getPaiements());
        if (input == target) target.setPaiements(new java.util.ArrayList<>());
        for (Paiement payment : requested) {
            if (payment == null) throw new IllegalArgumentException("Payments cannot be null");
            if (payment.getIdPaiement() != null) {
                payment = support.lock(Paiement.class, payment.getIdPaiement());
                if (payment.getContrat() != null && payment.getContrat() != target)
                    throw new org.springframework.dao.DataIntegrityViolationException(
                            "Payment is already assigned");
            } else support.validate(payment);
            payment.setContrat(target);
            if (!target.getPaiements().contains(payment)) target.getPaiements().add(payment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paiement> getPayments(Long id) {
        getById(id);
        return paiementRepository.findByContrat_IdContrat(id);
    }

    @Override
    public Paiement addPayment(Long id, Paiement payment) {
        Contrat contract = getById(id);
        support.checkNew(payment);
        support.validate(payment);
        if (payment.getContrat() != null && !id.equals(payment.getContrat().getIdContrat()))
            throw new IllegalArgumentException("Payment contract must match the route");
        payment.setContrat(contract);
        contract.getPaiements().add(payment);
        return paiementRepository.save(payment);
    }
}
