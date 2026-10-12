package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.EvenementRequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.EvenementService;
import com.billetterie.billetterie.utils.response.Response;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les endpoints de gestion des événements.
 */
@RestController
@RequestMapping("/evenement")
public class EvenementController {
    private final EvenementService evenementService;

    /**
     * Constructeur injectant le service des événements.
     *
     * @param evenementService le service métier des événements
     */
    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    /**
     * Récupère la liste de tous les événements.
     *
     * @return Response<ResponseDto> La liste des événements encapsulée.
     */
    @GetMapping
    public Response<ResponseDto> getAll() {
        return new Response<>(evenementService.getAll());
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
}
