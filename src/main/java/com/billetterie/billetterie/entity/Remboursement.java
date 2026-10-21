package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "remboursement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Remboursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_remboursement", nullable = false)
    private LocalDateTime dateRemboursement;

    @Column(name = "mode_remboursement")
    private String modeRemboursement; // Ex: Wave, Orange Money, Carte, Espèces

    private String motif; // Ex: "Annulation partielle de 1 billet VIP"

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
}