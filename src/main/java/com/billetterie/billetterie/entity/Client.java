package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Représente le client qui fait une reservation por l'evenement
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client" ,uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nom","prenom"}
        )})
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 10, nullable = false)
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    @Column(length = 20)
    @NotBlank(message = "Le prenom est obligatoire")
    private String prenom;
    @Column(length = 20,unique = true)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(
            regexp = "^0[1257][0-9]{8}$",
            message = "Format de numéro  invalide (ex: 0700000000)"
    )
    private String telephone;
}
