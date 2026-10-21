package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.TarifEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface TarifEvenementRepository extends JpaRepository<TarifEvenement, Long> {

    @Query(value = """
        SELECT DISTINCT t.prix 
        FROM tarif_evenement t 
        INNER JOIN evenement e ON t.evenement_id = e.id 
        INNER JOIN place p ON p.salle_id = e.salle_id 
        WHERE t.evenement_id = :evenementId 
          AND p.categorie_id = :categorieId
    """, nativeQuery = true)
    Optional<BigDecimal> findPrixParEvenementEtCategorie(
            @Param("evenementId") Long evenementId,
            @Param("categorieId") Long categorieId
    );


}
