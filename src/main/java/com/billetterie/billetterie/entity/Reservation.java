package com.billetterie.billetterie.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité représentant une réservation.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @Column(name = "date_annulation")
    private LocalDateTime dateAnnulation;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "montant_total", nullable = false)
    private BigDecimal montantTotal;

    @Column(name = "montant_rembourse")
    private BigDecimal montantRembourse;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @ManyToOne
    @JoinColumn(name = "statut_id")
    private Statut statut;
}
