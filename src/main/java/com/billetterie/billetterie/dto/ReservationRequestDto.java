package com.billetterie.billetterie.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Requête de création ou de modification d'une réservation.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDto implements RequestDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreation;
    @FutureOrPresent(message = "La date de paiement ne doit pas être dans le passé")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datePaiement;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateAnnulation;
    @NotNull(message = "Le client est obligatoire")
    private Long clientId;
    @NotNull(message = "Le montant total est obligatoire")
    @Positive(message = "Le montant total doit être positif")
    private BigDecimal montantTotal;
    @PositiveOrZero(message = "Le montant remboursé doit être positif ou nul")
    private BigDecimal montantRembourse;
    @NotNull(message = "L'événement est obligatoire")
    private Long evenementId;
    @NotNull(message = "Le statut est obligatoire")
    private Long statutId;
}
