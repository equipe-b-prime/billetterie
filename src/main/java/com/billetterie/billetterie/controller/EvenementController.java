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

    /**
     * Constructeur injectant les services nécessaires pour la gestion des événements.
     *
     * @param evenementService   le service métier des événements
     * @param reservationService le service métier des réservations
     */
    public EvenementController(EvenementService evenementService, ReservationService reservationService) {
        this.evenementService = evenementService;
        this.reservationService = reservationService;
    }

    /**
     * Récupère la liste paginée de tous les événements avec projection détaillée.
     *
     * @param page le numéro de page (index à 0)
     * @param size le nombre d'éléments par page
     * @return Response<EvenementResponseDto> la page des événements encapsulée
     */
    @Operation(
            summary = "Lister les événements (vue paginée)",
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

    /**
     * Récupère un événement à partir de son identifiant unique.
     *
     * @param id l'identifiant de l'événement
     * @return Response<ResponseDto> les détails de l'événement
     * @throws Throwable si l'événement n'existe pas
     */
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

    /**
     * Crée un nouvel événement.
     * <p>Remarque sur les dates : Le champ <code>horaire</code> dans le corps JSON doit respecter le format <code>yyyy-MM-dd HH:mm:ss</code>.</p>
     *
     * @param evenementRequest les données de l'événement à créer
     * @return Response<ResponseDto> les informations de l'événement créé
     */
    @Operation(
            summary = "Créer un événement",
            description = "Ajoute un nouvel événement dans la programmation. L'horaire dans le corps JSON doit suivre le format 'yyyy-MM-dd HH:mm:ss' (ex: 2026-12-25 20:00:00)."
    )
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody EvenementRequestDto evenementRequest) {
        return new Response<>(evenementService.create(evenementRequest));
    }

    /**
     * Met à jour les détails d'un événement existant.
     *
     * @param id               l'identifiant de l'événement à modifier
     * @param evenementRequest les nouvelles données de l'événement
     * @return Response<ResponseDto> les détails de l'événement mis à jour
     */
    @Operation(
            summary = "Modifier un événement",
            description = "Met à jour les détails d'un événement existant (report vers le futur, modification titre/description)."
    )
    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant de l'événement à modifier", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody EvenementRequestDto evenementRequest) {
        return new Response<>(evenementService.update(id, evenementRequest));
    }

    /**
     * Supprime un événement du système.
     *
     * @param id l'identifiant de l'événement à supprimer
     * @return Response<ResponseDto> les détails de l'événement supprimé
     * @throws Throwable si l'événement n'existe pas ou contient des réservations
     */
    @Operation(
            summary = "Supprimer un événement",
            description = "Supprime un événement du système. Rejeté si des réservations lui sont rattachées."
    )
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant de l'événement à supprimer", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(evenementService.delete(id));
    }

    /**
     * Recherche des événements compris entre deux bornes temporelles.
     *
     * @param du   date et heure de début au format dd-MM-yyyy HH:mm:ss
     * @param au   date et heure de fin au format dd-MM-yyyy HH:mm:ss
     * @param page numéro de page (index à 0)
     * @param size nombre d'éléments par page
     * @return Response<EvenementResponseDto> la liste chronologique des événements
     */
    @Operation(
            summary = "Rechercher des événements par plage horaire",
            description = "Retourne la liste paginée des événements compris entre deux dates au format 'dd-MM-yyyy HH:mm:ss', triée par ordre chronologique."
    )
    @GetMapping("/recherche")
    public Response<EvenementResponseDto> findByHoraireBetweenOrderByHoraireAsc(
            @Parameter(description = "Date/Heure de début (format: dd-MM-yyyy HH:mm:ss)", example = "01-01-2026 00:00:00", required = true)
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime du,
            @Parameter(description = "Date/Heure de fin (format: dd-MM-yyyy HH:mm:ss)", example = "31-12-2026 23:59:59", required = true)
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime au,
            @Parameter(description = "Numéro de page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return new Response<>(evenementService.findByHoraireBetweenOrderByHoraireAsc(du, au, page, size));
    }

    /**
     * Liste les événements avec filtrage temporel optionnel.
     *
     * @param page le numéro de page
     * @param size le nombre d'éléments par page
     * @param du   date de début optionnelle (format: dd-MM-yyyy HH:mm:ss)
     * @param au   date de fin optionnelle (format: dd-MM-yyyy HH:mm:ss)
     * @return Response<EvenementResponseDto> la liste des événements
     */
    @Operation(
            summary = "Lister les événements (avec filtres optionnels)",
            description = "Récupère les événements avec possibilité de filtrer sur une plage horaire au format 'dd-MM-yyyy HH:mm:ss'."
    )
    @GetMapping
    public Response<EvenementResponseDto> getEvenements(
            @Parameter(description = "Numéro de page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Date de début optionnelle (format: dd-MM-yyyy HH:mm:ss)", example = "01-01-2026 00:00:00")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime du,
            @Parameter(description = "Date de fin optionnelle (format: dd-MM-yyyy HH:mm:ss)", example = "31-12-2026 23:59:59")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime au) {
        return new Response<>(evenementService.getEvenements(page, size, du, au));
    }

    /**
     * Récupère le décompte des places libres par catégorie pour un événement.
     *
     * @param id l'identifiant de l'événement
     * @return Response<VuePlaceLibreResponseDto> le décompte des places libres
     */
    @Operation(
            summary = "Obtenir le nombre de places libres",
            description = "Retourne le nombre de places encore disponibles pour un événement donné, ventilées par catégorie de place."
    )
    @GetMapping("/{id}/places-libres")
    public Response<VuePlaceLibreResponseDto> placeLibre(
            @Parameter(description = "Identifiant de l'événement", example = "1", required = true)
            @PathVariable Long id) {
        return new Response<>(evenementService.placeLibre(id));
    }

    /**
     * Calcule le taux de remplissage global et par catégorie pour un événement.
     *
     * @param id l'identifiant de l'événement
     * @return Response<RemplissageResponseDto> les pourcentages et statistiques d'occupation
     */
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

    /**
     * Exporte les réservations d'un événement sous forme de fichier CSV.
     *
     * @param id              l'identifiant de l'événement
     * @param inclureAnnulees inclure ou exclure les réservations annulées (false par défaut)
     * @return ResponseEntity<byte[]> le contenu du fichier CSV téléchargé
     */
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