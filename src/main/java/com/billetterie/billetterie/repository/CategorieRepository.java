package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository de gestion des Categories.
 */
@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {


    @Query("SELECT c FROM Categorie c WHERE c.libelle = :libelle")
    Optional<Categorie> findByLibelle(@Param("libelle") String libelle);
}
