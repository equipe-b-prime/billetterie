package com.billetterie.billetterie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = "Le rang de la place est obligatoire")
    @Positive(message = "Le rang doit être strictement supérieur à 0")
    private Integer rang;

    @NotNull(message = "Le numéro de la place est obligatoire")
    @Positive(message = "Le numéro doit être strictement supérieur à 0")
    private Integer numero;

    @NotNull(message = "La salle de la place est obligatoire")
    @Positive(message = "L'identifiant de la salle doit être un entier positif")
    private Long salleId;

    @NotNull(message = "La catégorie de la place est obligatoire")
    @Positive(message = "L'identifiant de la catégorie doit être un entier positif")
    private Long categorieId;
}