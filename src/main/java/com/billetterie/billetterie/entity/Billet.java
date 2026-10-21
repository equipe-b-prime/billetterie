package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "billet",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_billet_reservation_place",
                        columnNames = {"reservation_id", "place_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "prix_applique", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixApplique;

    @Column(name = "code_billet", unique = true, length = 100)
    private String codeBillet;
}