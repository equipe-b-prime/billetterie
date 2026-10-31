package com.billetterie.billetterie.dto;

import com.billetterie.billetterie.service.AbstractClean;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Requête de création ou de modification d'une catégorie.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategorieRequestDto extends AbstractClean implements RequestDto {
    @NotBlank(message = "Le nom ne peut pas être vide")
    private String nom;
    private String description;

    @JsonSetter
    public void setNom(String nom) {
        this.nom = cleanText(nom);
    }

    @JsonSetter
    public void setDescription(String description) {
        this.description = cleanText(description);
    }
}
