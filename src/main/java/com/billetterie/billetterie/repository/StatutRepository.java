package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository de gestion des Statuts.
 */
@Repository
public interface StatutRepository extends JpaRepository<Statut, Long> {
}
