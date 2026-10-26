package com.billetterie.billetterie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "v_evenement_remplissage_categorie")
@Getter
public class VueRemplissageCategorie {

    @Id
    private String id;

    @Column(name = "evenement_id")
    private Long evenementId;

    @Column(name = "nom_categorie")
    private String nomCategorie;

    @Column(name = "total_places")
    private int totalPlaces;

    @Column(name = "places_provisoires")
    private int placesProvisoires;

    @Column(name = "places_payees")
    private int placesPayees;
}