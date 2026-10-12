package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository de gestion des Evenements.
 */
@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {
}
