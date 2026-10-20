package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente la salle de evenement
 */
@Entity
@Table(name = "salle"
        ,uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nom","ville"})
}
//,indexes = {
//        @Index(name = "idx_salle_nom",columnList = "nom")
//}
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nom;
    @Column(nullable = false, length = 50)
    private String ville;
    @Column(nullable = false, length = 255)
    private String adresse;
    private int capacite;
}
