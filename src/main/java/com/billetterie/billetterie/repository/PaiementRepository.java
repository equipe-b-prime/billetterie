package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Paiement;
import com.billetterie.billetterie.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository de gestion des Categories.
 */
@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {


    boolean existsByReservationId(long id);
    Optional<Paiement>findByReservationId(Long id);
}

