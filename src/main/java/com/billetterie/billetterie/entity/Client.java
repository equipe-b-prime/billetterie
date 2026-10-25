package com.billetterie.billetterie.entity;

import jakarta.persistence.*;
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
        )},indexes = {
        @Index(name = "idx_client_nom_prenom", columnList = "nom, prenom")
})
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false)
    private String nom;
    @Column(length = 50, nullable = false)
    private String prenom;
    @Column(length = 20,unique = true)
    private String telephone;
    @Column(length = 20,unique = true)
    private String email;

}
