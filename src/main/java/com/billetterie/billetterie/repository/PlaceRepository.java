package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository de gestion des Places.
 */
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    // Recherche automatique des places libres pour un événement et une catégorie
    @Query("""
        SELECT p FROM Place p
        WHERE p.salle.id = :salleId
          AND p.categorie.id = :categorieId
          AND p.id NOT IN (
              SELECT b.place.id FROM Billet b
              WHERE b.reservation.evenement.id = :evenementId
                AND b.reservation.dateAnnulation IS NULL
          )
        ORDER BY p.rang ASC, p.numero ASC
    """)
    List<Place> findPlacesDisponibles(
            @Param("salleId") Long salleId,
            @Param("evenementId") Long evenementId,
            @Param("categorieId") Long categorieId,
            Pageable pageable
    );

    boolean existsBySalleId(Long id);

    @Query("select count (p) from Place p where p.salle.id = :salleId")
    long countBySalleId(@Param("salleId") Long salleId);

    @Query(value = "select exists (select 1 from place p where p.salle_id = :salleId and p.rang = :rang and p.numero = :numero)", nativeQuery = true)
    boolean existsBySalleIdAndRangAndNumero(@Param("salleId") Long salleId, @Param("rang") int rang, @Param("numero") int numero);

    @Query(value = "select exists (select 1 from place p where p.salle_id = :salleId and p.rang = :rang and p.numero = :numero and p.id <> :placeId)", nativeQuery = true)
    boolean existsBySalleIdAndRangAndNumeroAndIdNot(@Param("salleId") Long salleId, @Param("rang") int rang, @Param("numero") int numero, @Param("placeId") Long placeId);

    @Query("SELECT p FROM Place p ORDER BY p.rang ASC, p.numero ASC")
    Page<Place> findAllSortedByRangAndNumero(Pageable pageable);

    boolean existsByRangAndNumeroAndSalleIdAndIdNot(int rang, int numero, Long salleId, Long placeId);
}
