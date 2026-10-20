package com.billetterie.billetterie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

/**
 * Entité mappée en lecture seule sur la vue v_evenement_details.
 */
@Entity
@Table(name = "v_evenement_details")
@Immutable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDetailView {

    @Id
    private Long id;

    private String titre;
    private String description;
    private LocalDateTime horaire;

    @Column(name = "placement_libre")
    private Boolean placementLibre;

    @Column(name = "salle_id")
    private Long salleId;

    @Column(name = "salle_nom")
    private String salleNom;

    @Column(name = "salle_ville")
    private String salleVille;

    @Column(name = "salle_capacite")
    private Integer salleCapacite;

    @Column(name = "salle_adresse")
    private String salleAdresse;
}