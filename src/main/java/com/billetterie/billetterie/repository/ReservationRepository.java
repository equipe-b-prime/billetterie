package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository de gestion des Reservations.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
