package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.PlaceRequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.PlaceService;
import com.billetterie.billetterie.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de gestion des places.
 */
@Tag(name = "Gestion des Places", description = "API de gestion des places physiques et de leur configuration dans les salles")
@RestController
@RequestMapping("/places")
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
    @Operation(
            summary = "Lister les places",
            description = "Retourne la liste paginée de toutes les places enregistrées dans le système."
    )
    @GetMapping
    public Response<ResponseDto> getAll(
            @Parameter(description = "Numéro de la page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return new Response<>(placeService.getAll(page, size));
    }

    /**
     * Récupère une place à partir de son identifiant.
     *
     * @param id L'identifiant de la place à récupérer.
     * @return Response<ResponseDto> Les détails de la place récupérée.
     * @throws Throwable si la place n'existe pas.
     */
    @Operation(
            summary = "Obtenir une place par ID",
            description = "Retourne les détails d'une place spécifique à partir de son identifiant unique."
    )
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(
            @Parameter(description = "Identifiant de la place", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(placeService.getById(id));
    }

    /**
     * Crée une nouvelle place.
     *
     * @param placeRequest Les données de la place à créer.
     * @return Response<ResponseDto> Les détails de la place créée.
     */
    @Operation(
            summary = "Créer une place",
            description = "Ajoute une nouvelle place rattachée à une salle et à une catégorie."
    )
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
    @Operation(
            summary = "Modifier une place",
            description = "Met à jour les informations d'une place existante (rang, numéro, catégorie, salle)."
    )
    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant de la place à modifier", example = "1", required = true)
            @PathVariable Long id,
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
    @Operation(
            summary = "Supprimer une place",
            description = "Supprime une place du système à partir de son identifiant."
    )
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant de la place à supprimer", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(placeService.delete(id));
    }

}