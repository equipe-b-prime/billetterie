package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

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
    @NotBlank(message = "Le nom ne peut pas être vide")
    private String libelle;
    @Column(length = 255)
    private String description;
}