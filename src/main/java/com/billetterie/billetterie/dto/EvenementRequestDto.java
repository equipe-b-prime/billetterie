package com.billetterie.billetterie.dto;

import com.billetterie.billetterie.service.AbstractClean;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Requête de création ou de modification d'un événement.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EvenementRequestDto extends AbstractClean implements RequestDto {
    private Long id;
    @NotBlank(message = "Le titre est obligatoire")
    private String titre;
    private String description;
    @NotNull(message = "L'horaire est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaire;
    private Boolean placementLibre;
    @NotNull(message = "La salle est obligatoire")
    private Long salleId;

    public void setTitre(String titre) {
        this.titre = cleanText(titre);
    }

    public void setDescription(String description) {
        this.description = cleanText(description);
    }
}
