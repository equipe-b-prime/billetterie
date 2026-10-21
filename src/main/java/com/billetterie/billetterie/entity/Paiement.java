package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Représente le paiement d'une reservation
 */
@Entity
@Table(name = "paiement")
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal montant;
    @FutureOrPresent(message = "la Date  ne  doit pas etre une date dans le passé")
    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement;

    @Column(length = 50)
    private String modePaiement; // ex: 'Carte', 'Espèces', 'Mobile Money'

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
}