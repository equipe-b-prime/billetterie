package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Représente  le statut de la reservation
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "statut",uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "libelle"
        })
})

public class Statut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Le libelle est obligatoire")
    @Column(nullable = false)
    private String libelle;
}