package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository de gestion des Statuts.
 */
@Repository
public interface StatutRepository extends JpaRepository<Statut, Long> {
    Optional<Statut> findByLibelle(String libelle);


    Optional<Statut> findByLibelleIgnoreCase(String libelle);
}
