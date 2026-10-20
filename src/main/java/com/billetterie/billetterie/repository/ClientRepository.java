package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Client;
import com.billetterie.billetterie.entity.ClientStatsView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository de gestion des Clients.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Récupère les statistiques agrégées d'un client à partir de la vue SQL.
     *
     * @param clientId l'identifiant du client
     * @return les statistiques du client, vide si ce client n'existe pas
     */
    @Query("SELECT v FROM ClientStatsView v WHERE v.clientId = :clientId")
    Optional<ClientStatsView> findStatsByClientId(@Param("clientId") Long clientId);
}
