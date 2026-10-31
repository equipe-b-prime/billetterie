package com.billetterie.billetterie.dto;

import com.billetterie.billetterie.service.AbstractClean;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Requête de création ou de modification d'une salle.
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalleRequestDto extends AbstractClean implements RequestDto {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    @NotBlank(message = "La ville est obligatoire")
    private String ville;
    @Min(value = 1, message = "La capacité doit être d'au moins 1")
    private int capacite;
    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @JsonSetter
    public void setNom(String nom) {
        this.nom = cleanText(nom);
    }

    @JsonSetter
    public void setVille(String ville) {
        this.ville = cleanText(ville);
    }

    @JsonSetter
    public void setAdresse(String adresse) {
        this.adresse = cleanText(adresse);
    }
}
