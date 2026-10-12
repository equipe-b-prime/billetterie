package com.billetterie.billetterie.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Requête de création ou de modification d'une catégorie.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CategorieRequestDto implements RequestDto {
    @NotBlank(message = "Le nom ne peut pas être vide")
    private String nom;
    private String description;
}
