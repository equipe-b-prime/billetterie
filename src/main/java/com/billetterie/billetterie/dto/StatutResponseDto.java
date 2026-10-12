package com.billetterie.billetterie.dto;

import lombok.*;

/**
 * Réponse représentant un statut.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatutResponseDto {
    private Long id;
    private String libelle;
}
