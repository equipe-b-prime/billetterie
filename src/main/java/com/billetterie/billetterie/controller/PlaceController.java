package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.PlaceRequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.PlaceService;
import com.billetterie.billetterie.utils.response.Response;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les endpoints de gestion des places.
 */
@RestController
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    /**
     * Constructeur injectant le service des places.
     *
     * @param placeService le service métier des places
     */
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    /**
     * Récupère la liste de toutes les places.
     *
     * @return Response<ResponseDto> La liste des places encapsulée.
     */
    @GetMapping
    public Response<ResponseDto> getAll() {
        return new Response<>(placeService.getAll());
    }

    /**
     * Récupère une place à partir de son identifiant.
     *
     * @param id L'identifiant de la place à récupérer.
     * @return Response<ResponseDto> Les détails de la place récupérée.
     * @throws Throwable si la place n'existe pas.
     */
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) throws Throwable {
        return new Response<>(placeService.getById(id));
    }

    /**
     * Crée une nouvelle place.
     *
     * @param placeRequest Les données de la place à créer.
     * @return Response<ResponseDto> Les détails de la place créée.
     */
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody PlaceRequestDto placeRequest) {
        return new Response<>(placeService.create(placeRequest));
    }

    /**
     * Modifie une place existante.
     *
     * @param id L'identifiant de la place à modifier.
     * @param placeRequest Les nouvelles données de la place.
     * @return Response<ResponseDto> Les détails de la place modifiée.
     */
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id,
                                        @Valid @RequestBody PlaceRequestDto placeRequest) {
        return new Response<>(placeService.update(id, placeRequest));
    }

    /**
     * Supprime une place à partir de son identifiant.
     *
     * @param id L'identifiant de la place à supprimer.
     * @return Response<ResponseDto> Les détails de la place supprimée.
     * @throws Throwable si la place n'existe pas.
     */
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) throws Throwable {
        return new Response<>(placeService.delete(id));
    }
}
