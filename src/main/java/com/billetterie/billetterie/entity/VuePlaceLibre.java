package com.billetterie.billetterie.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Immutable;
@Entity
@Immutable
@Table(name = "vue_places_libres")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VuePlaceLibre {
    @Id
    private String id;
    private Long evenementId;
    private String categorie;
    private Long placesDisponibles;
}
