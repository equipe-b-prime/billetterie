package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente la salle de evenement
 */
@Entity
@Table(name = "salle",uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "nom","ville"
        })
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String nom;
    @Column(nullable = false)
    private String ville;
    @Column(nullable = false)
    private int capacite;
    @Column(nullable = false)
    private String adresse;
}
