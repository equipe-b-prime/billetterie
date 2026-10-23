package com.billetterie.billetterie.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Réponse représentant un événement, avec sa salle associée.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvenementResponseDto implements ResponseDto {

    private Long id;
    private String titre;
    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime horaire;
    private Boolean placementLibre;
    private SalleResponseDto salle;
}
