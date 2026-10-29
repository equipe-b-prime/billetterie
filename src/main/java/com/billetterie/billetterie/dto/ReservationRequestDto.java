package com.billetterie.billetterie.dto;

import com.billetterie.billetterie.service.AbstractClean;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Requête de création ou de modification d'une réservation.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDto extends AbstractClean implements RequestDto {
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
    @NotNull(message = "L'événement est obligatoire")
    private Long evenementId;
    @NotNull(message = "Le statut est obligatoire")
    private Long statutId;
    @NotEmpty(message = "Sélectionnez au moins une catégorie")
    @Valid
    private List<LigneReservationDto> lignes;
}
