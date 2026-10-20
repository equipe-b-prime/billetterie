package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente la place dans la salle pour l'evenement
 */
@Entity
@Table(name = "place",uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "salle_id","rang","numero"
        })
},indexes = @Index(name = "idx_place_salle_rang_numero", columnList = "salle_id, rang, numero")
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rang;
    private int numero;
    @ManyToOne()
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;
    @ManyToOne
    @JoinColumn(name = "categorie_id",nullable = false)
    private Categorie categorie;
}
