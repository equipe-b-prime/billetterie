package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.ReservationRequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.service.ClientStatsService;
import com.billetterie.billetterie.service.EvenementService;
import com.billetterie.billetterie.service.ReservationService;
import com.billetterie.billetterie.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de gestion des réservations.
 */
@Tag(name = "Gestion des Réservations", description = "API de gestion des réservations, paiements, annulations et statistiques client")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ClientStatsService clientStatsService;
    private final EvenementService evenementService;

    /**
     * Constructeur injectant le service des réservations et celui des statistiques client.
     *
     * @param reservationService le service métier des réservations
     * @param clientStatsService le service des statistiques agrégées de réservation par client
     * @param evenementService   le service métier des événements
     */
    public ReservationController(ReservationService reservationService,
                                 ClientStatsService clientStatsService,
                                 EvenementService evenementService) {
        this.reservationService = reservationService;
        this.clientStatsService = clientStatsService;
        this.evenementService = evenementService;
    }

    /**
     * Récupère la liste de toutes les réservations.
     *
     * @return Response<ResponseDto> La liste des réservations encapsulée.
     */
    @Operation(
            summary = "Lister les réservations",
            description = "Récupère la liste paginée de toutes les réservations, avec possibilité de filtrer par adresse email client."
    )
    @GetMapping
    public Response<ResponseDto> getAll(
            @Parameter(description = "Numéro de la page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Email du client pour filtrer les réservations (insensible à la casse)", example = "client@exemple.ci")
            @RequestParam(required = false) String email) {
        return new Response<>(reservationService.getAll(email, page, size));
    }

    /**
     * Récupère une réservation à partir de son identifiant.
     *
     * @param id L'identifiant de la réservation à récupérer.
     * @return Response<ResponseDto> Les détails de la réservation récupérée.
     * @throws Throwable si la réservation n'existe pas.
     */
    @Operation(
            summary = "Obtenir une réservation par ID",
            description = "Retourne les détails d'une réservation spécifique à partir de son identifiant unique."
    )
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(
            @Parameter(description = "Identifiant de la réservation", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(reservationService.getById(id));
    }

    /**
     * Crée une nouvelle réservation.
     *
     * @param reservationRequest Les données de la réservation à créer.
     * @return Response<ResponseDto> Les détails de la réservation créée.
     */
    @Operation(
            summary = "Créer une réservation",
            description = "Enregistre une nouvelle réservation en statut PROVISOIRE et réserve les places associées."
    )
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody ReservationRequestDto reservationRequest) {
        return new Response<>(reservationService.create(reservationRequest));
    }

    /**
     * Modifie une réservation existante.
     *
     * @param id                 L'identifiant de la réservation à modifier.
     * @param reservationRequest Les nouvelles données de la réservation.
     * @return Response<ResponseDto> Les détails de la réservation modifiée.
     */
    @Operation(
            summary = "Modifier une réservation",
            description = "Met à jour les informations d'une réservation existante."
    )
    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant de la réservation à modifier", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDto reservationRequest) {
        return new Response<>(reservationService.update(id, reservationRequest));
    }

    /**
     * Supprime une réservation à partir de son identifiant.
     *
     * @param id L'identifiant de la réservation à supprimer.
     * @return Response<ResponseDto> Les détails de la réservation supprimée.
     * @throws Throwable si la réservation n'existe pas.
     */
    @Operation(
            summary = "Supprimer une réservation",
            description = "Supprime définitivement une réservation à partir de son identifiant."
    )
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant de la réservation à supprimer", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(reservationService.delete(id));
    }

    /**
     * Récupère une page des réservations d'un client à partir de son identifiant.
     *
     * @param clientId l'identifiant du client dont on veut les réservations
     * @param page     l'index de la page souhaitée (0 par défaut)
     * @param size     le nombre d'éléments par page (10 par défaut, borné à 100)
     * @return Response<ResponseDto> La page des réservations du client encapsulée.
     * @throws Throwable si aucune réservation ne correspond à ce client.
     */
    @Operation(
            summary = "Lister les réservations d'un client",
            description = "Retourne la liste paginée des réservations associées à l'identifiant d'un client."
    )
    @GetMapping("/client")
    public Response<ResponseDto> getReservationByIdClient(
            @Parameter(description = "Identifiant du client", example = "1", required = true)
            @RequestParam long clientId,
            @Parameter(description = "Numéro de la page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size) throws Throwable {
        return new Response<>(reservationService.getReservationByIdClient(clientId, page, size));
    }

    /**
     * Récupère une page des réservations ayant un statut donné à partir de l'identifiant du statut.
     *
     * @param statutId l'identifiant du statut recherché
     * @param page     l'index de la page souhaitée (0 par défaut)
     * @param size     le nombre d'éléments par page (10 par défaut, borné à 100)
     * @return Response<ResponseDto> La page des réservations correspondant au statut encapsulée.
     * @throws Throwable si aucune réservation ne correspond à ce statut.
     */
    @Operation(
            summary = "Lister les réservations par statut",
            description = "Retourne la liste paginée des réservations filtrées selon l'identifiant de leur statut."
    )
    @GetMapping("/statut/{statutId}")
    public Response<ResponseDto> getReservationByIdStatut(
            @Parameter(description = "Identifiant du statut", example = "1", required = true)
            @PathVariable long statutId,
            @Parameter(description = "Numéro de la page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size) throws Throwable {
        return new Response<>(reservationService.getReservationByIdStatut(statutId, page, size));
    }

    /**
     * Récupère une page des réservations d'un événement à partir de l'identifiant de cet événement.
     *
     * @param id   l'identifiant de l'événement recherché
     * @param page l'index de la page souhaitée (0 par défaut)
     * @param size le nombre d'éléments par page (10 par défaut, borné à 100)
     * @return Response<ResponseDto> La page des réservations correspondant à l'événement encapsulée.
     * @throws Throwable si aucune réservation ne correspond à cet événement.
     */
    @Operation(
            summary = "Lister les réservations d'un événement",
            description = "Retourne la liste paginée des réservations effectuées pour un événement donné."
    )
    @GetMapping("/evenement/{id}")
    public Response<ResponseDto> getReservationByIdEvenement(
            @Parameter(description = "Identifiant de l'événement", example = "1", required = true)
            @PathVariable long id,
            @Parameter(description = "Numéro de la page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size) throws Throwable {
        return new Response<>(reservationService.getReservationByIdEvenement(id, page, size));
    }

    /**
     * Récupère les statistiques agrégées de réservation d'un client.
     *
     * @param clientId l'identifiant du client recherché
     * @return Response<ResponseDto> Les statistiques du client encapsulées.
     * @throws NotFoundException si aucun client ne porte cet identifiant.
     */
    @Operation(
            summary = "Obtenir les statistiques d'un client",
            description = "Récupère les statistiques agrégées d'un client (volume de réservations, montants totaux, paiements et annulations)."
    )
    @GetMapping("/client/{clientId}/stats")
    public Response<ResponseDto> getClientStats(
            @Parameter(description = "Identifiant du client", example = "1", required = true)
            @PathVariable long clientId) {
        return new Response<>(clientStatsService.getClientStats(clientId));
    }

    /**
     * Valide le paiement d'une réservation.
     *
     * @param id L'identifiant de la réservation à payer.
     * @return Response<ResponseDto> La réservation mise à jour.
     * @throws Throwable si la réservation n'existe pas ou ne peut pas être payée.
     */
    @Operation(
            summary = "Valider le paiement d'une réservation",
            description = "Fait passer une réservation du statut PROVISOIRE au statut PAYEE et renseigne la date de paiement."
    )
    @PostMapping("/{id}/paiement")
    public Response<ResponseDto> passerDeProvisoreApaye(
            @Parameter(description = "Identifiant de la réservation", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(reservationService.passerDeProvisoreApaye(id));
    }

    /**
     * Annule une réservation.
     *
     * @param id L'identifiant de la réservation à annuler.
     * @return String Message de confirmation de l'annulation.
     * @throws Throwable si la réservation n'existe pas ou ne peut pas être annulée.
     */
    @Operation(
            summary = "Annuler une réservation",
            description = "Annule une réservation active, met à jour son statut à ANNULEE et libère les places réservées."
    )
    @PostMapping("/{id}/annulation")
    public Response<String> annulerReservation(
            @Parameter(description = "Identifiant de la réservation", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(reservationService.annulerReservation(id));
    }
}