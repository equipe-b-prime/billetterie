package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository de gestion des Salles.
 */
@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {
}
