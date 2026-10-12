package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente la categorie de la place
 */
@Entity
@Table(name = "categorie")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length =30, nullable = false, unique = true)
    private String libelle;
    @Column(length = 255)
    private String description;
}