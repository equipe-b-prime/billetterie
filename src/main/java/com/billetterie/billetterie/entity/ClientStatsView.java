package com.billetterie.billetterie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité mappée en lecture seule sur la vue v_client_stats.
 * Elle porte les statistiques agrégées des réservations d'un client.
 * La vue doit exister en base : elle est créée à la main, comme v_evenement_details
 * (définition et procédure dans VUES.md).
 */
@Entity
@Table(name = "v_client_stats")
@Immutable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientStatsView {

    @Id
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "nb_reservations")
    private Long nbReservations;

    @Column(name = "nb_validees")
    private Long nbValidees;

    @Column(name = "nb_annulees")
    private Long nbAnnulees;

    @Column(name = "nb_remboursees")
    private Long nbRemboursees;

    @Column(name = "ca_brut")
    private BigDecimal caBrut;

    @Column(name = "ca_hors_annulees")
    private BigDecimal caHorsAnnulees;

    @Column(name = "total_rembourse")
    private BigDecimal totalRembourse;

    @Column(name = "net")
    private BigDecimal net;

    @Column(name = "nb_evenements")
    private Long nbEvenements;

    @Column(name = "premiere_reservation")
    private LocalDateTime premiereReservation;

    @Column(name = "derniere_reservation")
    private LocalDateTime derniereReservation;
}
