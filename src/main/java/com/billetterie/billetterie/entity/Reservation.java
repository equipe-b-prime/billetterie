package com.billetterie.billetterie.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Représente une réservation.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "reservation",indexes = {
        @Index(name = "idx_reservation_client", columnList = "client_id"),
        @Index(name = "idx_reservation_evenement", columnList = "evenement_id"),
        @Index(name = "idx_reservation_statut", columnList = "statut_id"),
        @Index(name = "idx_reservation_date_creation", columnList = "date_creation")
})
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
    @Column(name = "montant_total", nullable = false)
    private BigDecimal montantTotal;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;
    @ManyToOne
    @JoinColumn(name = "statut_id")
    private Statut statut;
    private Boolean estRembourse;
    @Column(name = "place_id")
    private Long placeId;

}
