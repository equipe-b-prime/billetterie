package com.billetterie.billetterie.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Réponse représentant une place, avec sa salle et sa catégorie associées.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceResponseDto implements ResponseDto {
    private Long id;
    private int rang;
    private int numero;
    private CategorieResponseDto categorie;
    private SalleResponseDto salle;
}
