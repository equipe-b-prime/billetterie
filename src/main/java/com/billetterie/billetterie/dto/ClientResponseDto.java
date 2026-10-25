package com.billetterie.billetterie.dto;

import lombok.*;

/**
 * Réponse représentant un client.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponseDto {
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
}
