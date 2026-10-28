package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.dto.ReservationExportDto;
import com.billetterie.billetterie.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository de gestion des Reservations.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Récupère une page des réservations d'un client spécifique à partir de son identifiant.
     *
     * @param clientId l'identifiant du client
     * @param pageable les paramètres de pagination et de tri
     * @return la page des réservations du client
     */
    @Query(value = "SELECT r FROM Reservation r WHERE r.client.id = :clientId",
           countQuery = "SELECT count(r) FROM Reservation r WHERE r.client.id = :clientId")
    Page<Reservation> findByClientId(@Param("clientId") Long clientId, Pageable pageable);

    /**
     * Récupère une page des réservations ayant un statut spécifique à partir de l'identifiant du statut.
     *
     * @param statutId l'identifiant du statut
     * @param pageable les paramètres de pagination et de tri
     * @return la page des réservations correspondant au statut
     */
    Page<Reservation> findByStatutId(Long statutId, Pageable pageable);

    /**
     * Récupère une page des réservations d'un événement spécifique à partir de l'identifiant de l'événement.
     *
     * @param evenementId l'identifiant de l'événement
     * @param pageable les paramètres de pagination et de tri
     * @return la page des réservations correspondant à l'événement
     */
    Page<Reservation> findByEvenementId(Long evenementId, Pageable pageable);

    boolean existsByEvenementId(long id);

//    @Query("select r from Reservation r where Lower(r.client.email) = Lower(:email)")
//    Page<Reservation> findReservationByEmailClient(@Param("email") String email,Pageable pageable);

    @EntityGraph(attributePaths = {"client", "evenement", "statut"})
    @Query("SELECT r FROM Reservation r WHERE r.client.email = :email")
    Page<Reservation> findReservationByEmailClient(@Param("email") String email, Pageable pageable);

//    @Query("update Reservation r set r.statut.id = 2 where r.statut.id = 1")
//    Reservation passerDeProvisoreApaye ()

    @Query("select r from Reservation r where r.statut.libelle = 'PROVISOIRE' and r.dateCreation <= :dateLimite ")
    List<Reservation>   findReservationExpirees(@Param("dateLimite") LocalDateTime dateLimite);

    @Query("""
    SELECT new com.billetterie.billetterie.dto.ReservationExportDto(
        COALESCE(p.rang, 0),
        COALESCE(p.numero, 0),
        COALESCE(c.libelle, 'Placement libre'),
        cli.nom,
        cli.email,
        s.libelle,
        r.dateCreation
    )
    FROM Reservation r
    JOIN r.client cli
    JOIN r.statut s
LEFT JOIN Billet b ON b.reservation.id = r.id
    LEFT JOIN Place p ON p.id = b.place.id
    LEFT JOIN p.categorie c
    WHERE r.evenement.id = :evenementId
    AND (:inclureAnnulees = true OR UPPER(s.libelle) <> 'ANNULEE')
    ORDER BY c.libelle ASC NULLS LAST, p.rang ASC, p.numero ASC
""")
    List<ReservationExportDto> findReservationsPourExport(
            @Param("evenementId") Long evenementId,
            @Param("inclureAnnulees") boolean inclureAnnulees
    );

}
