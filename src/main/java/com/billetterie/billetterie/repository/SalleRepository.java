package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Categorie;
import com.billetterie.billetterie.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository de gestion des Salles.
 */
@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {
    @Query("SELECT s FROM Salle s WHERE s.nom = :nom")
    Optional<Salle> findByNom(@Param("nom") String nom);

    @Query("SELECT count(s) > 0 FROM Salle s WHERE lower(s.ville) = lower(:ville) and lower(s.nom) = lower(:nom)")
    boolean existsByNomAndVilles(@Param("nom") String nom, @Param("ville") String ville);

    @Query(value = "SELECT exists (SELECT 1 FROM salle WHERE lower(nom) = lower(:nom) " +
            "and lower(ville) = lower(:ville))", nativeQuery = true)
    boolean existsByNomAndVille(@Param("nom") String nom, @Param("ville") String ville);
}