package com.billetterie.billetterie.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Réponse représentant les statistiques agrégées des réservations d'un client.
 * Un client connu sans aucune réservation obtient des compteurs et des montants à zéro.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientStatsResponseDto implements ResponseDto {
    private Long clientId;
    private Long nbReservations;
    private Long nbValidees;
    private Long nbAnnulees;
    private Long nbRemboursees;
    private BigDecimal caBrut;
    private BigDecimal caHorsAnnulees;
    private BigDecimal totalRembourse;
    private BigDecimal net;
    private Long nbEvenements;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime premiereReservation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime derniereReservation;
}
