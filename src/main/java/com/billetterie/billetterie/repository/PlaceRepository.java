package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository de gestion des Places.
 */
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    // Retourne true dès qu'au moins une place est rattachée à cette salle
    boolean existsBySalleId(Long id);

    // long countBySalleId(Long salleId);
    @Query("select count (p) from Place p where p.salle.id = :salleId")
    long countBySalleId(@Param("salleId") Long salleId);


    @Query(value = "select exists (select 1 from place p where p.salle_id = :salleId and p.rang = :rang and" +
            " p.numero = :numero)", nativeQuery = true)
    boolean existsBySalleIdAndRangAndNumero(@Param("salleId") Long salleId, @Param("rang") int rang ,
                                            @Param("numero") int numero);
    @Query(value = "select exists (select 1 from place p where p.salle_id = :salleId and p.rang = :rang and" +
            " p.numero = :numero and p.id <> :placeId)", nativeQuery = true)
    boolean existsBySalleIdAndRangAndNumeroAndIdNot(@Param("salleId") Long salleId, @Param("rang") int rang ,
                                                    @Param("numero") int numero, @Param("placeId") Long placeId);

    // Requête JPQL personnalisée avec tri sur le rang puis le numéro
    @Query("SELECT p FROM Place p ORDER BY p.rang ASC, p.numero ASC")
    Page<Place> findAllSortedByRangAndNumero(Pageable pageable);

    boolean existsByRangAndNumeroAndSalleIdAndIdNot(int rang, int numero, Long salleId, Long placeId);

}
