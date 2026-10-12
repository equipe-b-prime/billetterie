package com.billetterie.billetterie.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PastOrPresent;
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
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @PastOrPresent(message = "la Date de creation ne doit pas etre une date dans le futur")
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    @FutureOrPresent(message = "la Date  ne  doit pas etre une date dans le passé")
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
