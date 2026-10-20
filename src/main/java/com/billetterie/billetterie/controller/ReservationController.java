package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.ReservationRequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.service.ClientStatsService;
import com.billetterie.billetterie.service.ReservationService;
import com.billetterie.billetterie.utils.response.Response;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de gestion des réservations.
 */
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final ClientStatsService clientStatsService;

    /**
     * Constructeur injectant le service des réservations et celui des statistiques client.
     *
     * @param reservationService le service métier des réservations
     * @param clientStatsService le service des statistiques agrégées de réservation par client
     */
    public ReservationController(ReservationService reservationService,
                                 ClientStatsService clientStatsService) {
        this.reservationService = reservationService;
        this.clientStatsService = clientStatsService;
    }

    /**
     * Récupère la liste de toutes les réservations.
     *
     * @return Response<ResponseDto> La liste des réservations encapsulée.
     */
    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10")int taille) {
        return new Response<>(reservationService.getAll(page,taille).getContent());
    }
    /**
     * Récupère une réservation à partir de son identifiant.
     *
     * @param id L'identifiant de la réservation à récupérer.
     * @return Response<ResponseDto> Les détails de la réservation récupérée.
     * @throws Throwable si la réservation n'existe pas.
     */
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) throws Throwable {
        return new Response<>(reservationService.getById(id));
    }

    /**
     * Crée une nouvelle réservation.
     *
     * @param reservationRequest Les données de la réservation à créer.
     * @return Response<ResponseDto> Les détails de la réservation créée.
     */
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody ReservationRequestDto reservationRequest) {
        return new Response<>(reservationService.create(reservationRequest));
    }

    /**
     * Modifie une réservation existante.
     *
     * @param id L'identifiant de la réservation à modifier.
     * @param reservationRequest Les nouvelles données de la réservation.
     * @return Response<ResponseDto> Les détails de la réservation modifiée.
     */
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id,
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
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) throws Throwable {
        return new Response<>(reservationService.delete(id));
    }

    /**
     * Récupère une page des réservations d'un client à partir de son identifiant.
     *
     * @param clientId l'identifiant du client dont on veut les réservations
     * @param page l'index de la page souhaitée (0 par défaut)
     * @param taille le nombre d'éléments par page (10 par défaut, borné à 100)
     * @return Response<ResponseDto> La page des réservations du client encapsulée.
     * @throws Throwable si aucune réservation ne correspond à ce client.
     */
    @GetMapping("/client")
    public Response<ResponseDto> getReservationByIdClient(@RequestParam long clientId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int taille) throws Throwable {
        return new Response<>(reservationService.getReservationByIdClient(clientId, page, taille).getContent());
    }

    /**
     * Récupère une page des réservations ayant un statut donné à partir de l'identifiant du statut.
     *
     * @param statutId l'identifiant du statut recherché
     * @param page l'index de la page souhaitée (0 par défaut)
     * @param taille le nombre d'éléments par page (10 par défaut, borné à 100)
     * @return Response<ResponseDto> La page des réservations correspondant au statut encapsulée.
     * @throws Throwable si aucune réservation ne correspond à ce statut.
     */
    @GetMapping("/statut/{statutId}")
    public Response<ResponseDto> getReservationByIdStatut(@PathVariable long statutId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int taille) throws Throwable {
        return new Response<>(reservationService.getReservationByIdStatut(statutId, page, taille).getContent());
    }

    /**
     * Récupère une page des réservations d'un événement à partir de l'identifiant de cet événement.
     *
     * @param id l'identifiant de l'événement recherché
     * @param page l'index de la page souhaitée (0 par défaut)
     * @param taille le nombre d'éléments par page (10 par défaut, borné à 100)
     * @return Response<ResponseDto> La page des réservations correspondant à l'événement encapsulée.
     * @throws Throwable si aucune réservation ne correspond à cet événement.
     */
    @GetMapping("/evenement/{id}")
    public Response<ResponseDto> getReservationByIdEvenement(@PathVariable long id,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int taille) throws Throwable {
        return new Response<>(reservationService.getReservationByIdEvenement(id, page, taille).getContent());
    }

    /**
     * Récupère les statistiques agrégées de réservation d'un client
     * (nombre de réservations, montants brut/hors annulées/remboursés/net, amplitude de dates).
     * Ces données proviennent de la vue v_client_stats créée au démarrage.
     *
     * @param clientId l'identifiant du client recherché
     * @return Response<ResponseDto> Les statistiques du client encapsulées.
     * @throws NotFoundException si aucun client ne porte cet identifiant.
     */
    @GetMapping("/client/{clientId}/stats")
    public Response<ResponseDto> getClientStats(@PathVariable long clientId) {
        return new Response<>(clientStatsService.getClientStats(clientId));
    }
}
