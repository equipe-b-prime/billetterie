package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.CategorieRequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.CategorieService;
import com.billetterie.billetterie.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de gestion des catégories.
 */
@Tag(name = "Gestion des Catégories", description = "API de gestion du référentiel des catégories de places")
@RestController
@RequestMapping("/categories")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @Operation(
            summary = "Lister les catégories",
            description = "Retourne la liste paginée de toutes les catégories enregistrées."
    )
    @GetMapping
    public Response<ResponseDto> getAll(
            @Parameter(description = "Numéro de la page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return new Response<>(categorieService.getAll(page, size));
    }

    @Operation(
            summary = "Obtenir une catégorie par ID",
            description = "Retourne les détails d'une catégorie à partir de son identifiant unique."
    )

    @GetMapping("/{id}")
    public Response<ResponseDto> getById(
            @Parameter(description = "Identifiant de la catégorie", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(categorieService.getById(id));
    }

    @Operation(
            summary = "Rechercher une catégorie par nom",
            description = "Recherche une catégorie spécifique à partir de son libellé exact."
    )
    @GetMapping("/recherche")
    public Response<ResponseDto> getByNom(
            @Parameter(description = "Libellé de la catégorie", example = "VIP", required = true)
            @RequestParam String nom) throws Throwable {
        return new Response<>(categorieService.getByNom(nom));
    }

    @Operation(
            summary = "Créer une catégorie",
            description = "Ajoute une nouvelle catégorie dans le système."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catégorie créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de la requête invalides"),
            @ApiResponse(responseCode = "409", description = "Une catégorie avec ce libellé existe déjà")
    })
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody CategorieRequestDto categorieRequest) {
        return new Response<>(categorieService.create(categorieRequest));
    }

    @Operation(
            summary = "Modifier une catégorie",
            description = "Met à jour les informations d'une catégorie existante."
    )

    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant de la catégorie à modifier", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CategorieRequestDto categorieRequest) {
        return new Response<>(categorieService.update(id, categorieRequest));
    }

    @Operation(
            summary = "Supprimer une catégorie",
            description = "Supprime une catégorie à partir de son identifiant."
    )

    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant de la catégorie à supprimer", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(categorieService.delete(id));
    }
}