package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.service.EvenementService;
import com.billetterie.billetterie.service.ReservationService;
import com.billetterie.billetterie.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Contrôleur REST exposant les endpoints de gestion des événements.
 */
@Tag(name = "Gestion des Événements", description = "API de gestion de la programmation des événements")
@RestController
@RequestMapping("/evenements")
public class EvenementController {

    private final EvenementService evenementService;
    private final ReservationService reservationService;

    public EvenementController(EvenementService evenementService, ReservationService reservationService) {
        this.evenementService = evenementService;
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "Lister les événements XXXXX",
            description = "Retourne la liste paginée des événements sous un format de réponse dédié."
    )
    @GetMapping("/ok")
    public Response<EvenementResponseDto> getAll(
            @Parameter(description = "Numéro de page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return new Response<>(evenementService.getAll(page, size));
    }

    @Operation(
            summary = "Obtenir un événement par ID",
            description = "Retourne les détails d'un événement spécifique à partir de son identifiant."
    )

    @GetMapping("/{id}")
    public Response<ResponseDto> getById(
            @Parameter(description = "Identifiant de l'événement", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(evenementService.getById(id));
    }

    @Operation(
            summary = "Créer un événement",
            description = "Ajoute un nouvel événement dans la programmation."
    )

    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody EvenementRequestDto evenementRequest) {
        return new Response<>(evenementService.create(evenementRequest));
    }

    @Operation(
            summary = "Modifier un événement",
            description = "Met à jour les détails d'un événement existant."
    )

    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant de l'événement à modifier", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody EvenementRequestDto evenementRequest) {
        return new Response<>(evenementService.update(id, evenementRequest));
    }

    @Operation(
            summary = "Supprimer un événement",
            description = "Supprime un événement du système."
    )

    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant de l'événement à supprimer", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(evenementService.delete(id));
    }

    @Operation(
            summary = "Rechercher des événements par plage horaire",
            description = "Retourne la liste paginée des événements compris entre deux dates, triée par ordre chronologique."
    )

    @GetMapping("/recherche")
    public Response<EvenementResponseDto> findByHoraireBetweenOrderByHoraireAsc(
            @Parameter(description = "Date/Heure de début (dd-MM-yyyy HH:mm:ss)", example = "01-01-2026 00:00:00", required = true)
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime du,
            @Parameter(description = "Date/Heure de fin (dd-MM-yyyy HH:mm:ss)", example = "31-12-2026 23:59:59", required = true)
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime au,
            @Parameter(description = "Numéro de page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return new Response<>(evenementService.findByHoraireBetweenOrderByHoraireAsc(du, au, page, size));
    }

    @Operation(
            summary = "Lister les événements (avec filtres optionnels)",
            description = "Récupère les événements avec possibilité de filtrer sur une plage horaire."
    )
    @GetMapping
    public Response<EvenementResponseDto> getEvenements(
            @Parameter(description = "Numéro de page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Date de début optionnelle (dd-MM-yyyy HH:mm:ss)", example = "01-01-2026 00:00:00")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime du,
            @Parameter(description = "Date de fin optionnelle (dd-MM-yyyy HH:mm:ss)", example = "31-12-2026 23:59:59")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime au) {
        return new Response<>(evenementService.getEvenements(page, size, du, au));
    }

    @Operation(
            summary = "Obtenir le nombre de places libres",
            description = "Retourne le nombre de places encore disponibles pour un événement donné."
    )

    @GetMapping("/{id}/places-libres")
    public Response<VuePlaceLibreResponseDto> placeLibre(
            @Parameter(description = "Identifiant de l'événement", example = "1", required = true)
            @PathVariable Long id) {
        return new Response<>(evenementService.placeLibre(id));
    }

    @Operation(
            summary = "Obtenir le taux de remplissage",
            description = "Calcule et retourne le taux de remplissage d'un événement par catégorie de place."
    )
    @GetMapping("/{id}/remplissage")
    public Response<RemplissageResponseDto> getTauxRemplissage(
            @Parameter(description = "Identifiant de l'événement", example = "1", required = true)
            @PathVariable Long id) {
        return new Response<>(evenementService.calculerTauxRemplissage(id));
    }

    @Operation(
            summary = "Exporter les réservations en CSV (User Story F19)",
            description = "Génère et télécharge un fichier CSV contenant la liste des réservations d'un événement pour le contrôle d'accès."
    )
    @GetMapping("/{id}/reservations/export")
    public ResponseEntity<byte[]> exporterReservations(
            @Parameter(description = "Identifiant de l'événement", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Inclure les réservations annulées dans l'export", example = "false")
            @RequestParam(defaultValue = "false") boolean inclureAnnulees) {

        byte[] csvContent = reservationService.exporterReservationsCsv(id, inclureAnnulees);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reservations_evenement_" + id + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvContent);
    }
}