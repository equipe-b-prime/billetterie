package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Client;
import com.billetterie.billetterie.entity.Evenement;
import com.billetterie.billetterie.entity.EvenementDetailView;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository de gestion des Evenements.
 */
@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    // Verrou pessimiste pour empêcher les réservations concurrentes simultanées
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Evenement e WHERE e.id = :id")
    Optional<Evenement> findByIdForUpdate(@Param("id") Long id);

    /**
     * Récupère le détail d'un événement à partir de la vue SQL.
     *
     * @param id l'identifiant de l'événement
     * @return le détail de l'événement, s'il existe
     */
    @Query("SELECT v FROM EvenementDetailView v WHERE v.id = :id")
    Optional<EvenementDetailView> findDetailViewById(@Param("id") Long id);

    /**
     * Récupère tous les détails d'événements depuis la vue SQL.
     *
     * @return la liste complète des détails d'événements
     */
    @Query("SELECT v FROM EvenementDetailView v")
    List<EvenementDetailView> findAllDetailViews();

    /**
     * Récupère une page de détails d'événements depuis la vue SQL.
     *
     * @param pageable les paramètres de pagination
     * @return la page des détails d'événements
     */
    @Query("SELECT e FROM Evenement e")
    Page<Evenement> findDetailViews(Pageable pageable);

    boolean existsBySalleId(Long id);

    List<Evenement> findBySalleId(Long salleId);

    @Query("SELECT e FROM Evenement e WHERE e.horaire between :dateDebut AND :dateFin ORDER BY e.horaire ASC")
    Page<Evenement> findByHoraireBetweenOrderByHoraireAsc(@Param("dateDebut") LocalDateTime dateDebut,
                                                                             @Param("dateFin") LocalDateTime dateFin, Pageable pageable
    );
    @Query("SELECT MIN(e.horaire) FROM Evenement e")
    LocalDateTime findPremiereDate();

    @Query("SELECT MAX(e.horaire) FROM Evenement e")
    LocalDateTime findDerniereDate();
}
