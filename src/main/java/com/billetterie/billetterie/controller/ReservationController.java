package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.ReservationRequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.ReservationService;
import com.billetterie.billetterie.utils.response.Response;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les endpoints de gestion des réservations.
 */
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Constructeur injectant le service des réservations.
     *
     * @param reservationService le service métier des réservations
     */
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Récupère la liste de toutes les réservations.
     *
     * @return Response<ResponseDto> La liste des réservations encapsulée.
     */
    @GetMapping
    public Response<ResponseDto> getAll() {
        return new Response<>(reservationService.getAll());
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
}
