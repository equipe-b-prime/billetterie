package com.billetterie.billetterie.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Requête de création ou de modification d'une place.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceRequestDto implements RequestDto {
    private int rang;
    private int numero;
    @NotNull(message = "La salle est obligatoire")
    private Long salleId;
    @NotNull(message = "La catégorie est obligatoire")
    private Long categorieId;
}
