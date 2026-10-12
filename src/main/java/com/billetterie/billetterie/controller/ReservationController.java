package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.ReservationRequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des réservations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Récupère la liste de toutes les réservations.
     *
     * @return la liste des réservations
     */
    @GetMapping
    public List<ResponseDto> getAll() {
        return reservationService.getAll();
    }

    /**
     * Récupère une réservation à partir de son identifiant.
     *
     * @param id l'identifiant de la réservation
     * @return la réservation trouvée
     * @throws Throwable si la réservation n'est pas trouvée
     */
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable {
        return reservationService.getById(id);
    }

    /**
     * Supprime une réservation à partir de son identifiant.
     *
     * @param id l'identifiant de la réservation
     * @return la réservation supprimée
     * @throws Throwable si la réservation n'est pas trouvée
     */
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable {
        return reservationService.delete(id);
    }

    /**
     * Modifie une réservation existante.
     *
     * @param id                l'identifiant de la réservation
     * @param reservationRequest les données de la requête
     * @return la réservation modifiée
     */
    @PutMapping("/{id}")
    public ResponseDto update(
            @PathVariable Long id,
            @RequestBody ReservationRequestDto reservationRequest) {
        return reservationService.update(id, reservationRequest);
    }

    /**
     * Crée une nouvelle réservation.
     *
     * @param reservationRequest les données de la requête
     * @return la réservation créée
     */
    @PostMapping
    public ResponseDto create(@RequestBody ReservationRequestDto reservationRequest) {
        return reservationService.create(reservationRequest);
    }
}
