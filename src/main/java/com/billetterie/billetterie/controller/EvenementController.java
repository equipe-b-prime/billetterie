package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.service.EvenementService;
import com.billetterie.billetterie.service.ReservationService;
import com.billetterie.billetterie.utils.response.Response;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur REST exposant les endpoints de gestion des événements.
 */
@RestController
@RequestMapping("/evenements")
public class EvenementController {
    private final EvenementService evenementService;
    private final ReservationService reservationService;

    /**
     * Constructeur injectant le service des événements.
     *
     * @param evenementService le service métier des événements
     */
    public EvenementController(EvenementService evenementService, ReservationService reservationService) {
        this.evenementService = evenementService;
        this.reservationService = reservationService;
    }

    /**
     * Récupère une page d'événements.
     *
     * @param page l'index de la page souhaitée (0 par défaut)
     * @param size le nombre d'éléments par page (10 par défaut, borné à 100)
     * @return Response<ResponseDto> La page des événements encapsulée.
     */
    @GetMapping("/ok")
    public Response<EvenementResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return new Response<>(evenementService.getAll(page, size));
    }
    /**
     * Récupère un événement à partir de son identifiant.
     *
     * @param id L'identifiant de l'événement à récupérer.
     * @return Response<ResponseDto> Les détails de l'événement récupéré.
     * @throws Throwable si l'événement n'existe pas.
     */
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) throws Throwable {
        return new Response<>(evenementService.getById(id));
    }

    /**
     * Crée un nouvel événement.
     *
     * @param evenementRequest Les données de l'événement à créer.
     * @return Response<ResponseDto> Les détails de l'événement créé.
     */
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody EvenementRequestDto evenementRequest) {
        return new Response<>(evenementService.create(evenementRequest));
    }
    /**
     * Modifie un événement existant.
     *
     * @param id L'identifiant de l'événement à modifier.
     * @param evenementRequest Les nouvelles données de l'événement.
     * @return Response<ResponseDto> Les détails de l'événement modifié.
     */
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id,
                                        @Valid @RequestBody EvenementRequestDto evenementRequest) {
        return new Response<>(evenementService.update(id, evenementRequest));
    }
    /**
     * Supprime un événement à partir de son identifiant.
     *
     * @param id L'identifiant de l'événement à supprimer.
     * @return Response<ResponseDto> Les détails de l'événement supprimé.
     * @throws Throwable si l'événement n'existe pas.
     */
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) throws Throwable {
        return new Response<>(evenementService.delete(id));
    }

    @GetMapping("/recherche")
    public Response<EvenementResponseDto> findByHoraireBetweenOrderByHoraireAsc(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime du,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime au,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size) {
        return new Response<>(evenementService.findByHoraireBetweenOrderByHoraireAsc(du, au,page,size));
    }

    @GetMapping
    public Response<EvenementResponseDto> getEvenements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime du,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime au) {
        return new Response<>(evenementService.getEvenements(page, size, du, au));
    }
    @GetMapping("/{id}/places-libres")
    public Response<VuePlaceLibreResponseDto> placeLibre(@PathVariable Long id) {
        return new Response<>(evenementService.placeLibre(id));
    }

    @GetMapping("/{id}/remplissage")
    public Response<RemplissageResponseDto> getTauxRemplissage(@PathVariable Long id) {
        return new Response<>(evenementService.calculerTauxRemplissage(id));
    }
    @GetMapping("/{id}/reservations/export")
    public ResponseEntity<byte[]> exporterReservations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean inclureAnnulees) {

        byte[] csvContent = reservationService.exporterReservationsCsv(id, inclureAnnulees);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reservations_evenement_" + id + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvContent);
    }
}

