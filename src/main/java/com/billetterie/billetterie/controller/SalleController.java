package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.dto.SalleRequestDto;
import com.billetterie.billetterie.service.SalleService;
import com.billetterie.billetterie.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de gestion des salles.
 */
@Tag(name = "Gestion des Salles", description = "API de gestion des salles de spectacle, de leurs places et de leur programmation")
@RestController
@RequestMapping("/salles")
public class SalleController {

    private final SalleService salleService;

    /**
     * Constructeur injectant le service des salles.
     *
     * @param salleService le service métier des salles
     */
    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    /**
     * Récupère la liste de toutes les salles.
     *
     * @return Response<ResponseDto> La liste des salles encapsulée.
     */
    @Operation(
            summary = "Lister les salles",
            description = "Retourne la liste paginée de toutes les salles enregistrées dans le système."
    )
    @GetMapping
    public Response<ResponseDto> getAll(
            @Parameter(description = "Numéro de la page (index à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return new Response<>(salleService.getAll(page, size));
    }

    /**
     * Récupère une salle à partir de son identifiant.
     *
     * @param id L'identifiant de la salle à récupérer.
     * @return Response<ResponseDto> Les détails de la salle récupérée.
     * @throws Throwable si la salle n'existe pas.
     */
    @Operation(
            summary = "Obtenir une salle par ID",
            description = "Retourne les détails d'une salle spécifique à partir de son identifiant unique."
    )
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(
            @Parameter(description = "Identifiant de la salle", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(salleService.getById(id));
    }

    /**
     * Crée une nouvelle salle.
     *
     * @param salleRequest Les données de la salle à créer.
     * @return Response<ResponseDto> Les détails de la salle créée.
     */
    @Operation(
            summary = "Créer une salle",
            description = "Ajoute une nouvelle salle de spectacle dans le système."
    )
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody SalleRequestDto salleRequest) {
        return new Response<>(salleService.create(salleRequest));
    }

    /**
     * Modifie une salle existante.
     *
     * @param id L'identifiant de la salle à modifier.
     * @param salleRequest Les nouvelles données de la salle.
     * @return Response<ResponseDto> Les détails de la salle modifiée.
     */
    @Operation(
            summary = "Modifier une salle",
            description = "Met à jour les informations d'une salle existante."
    )
    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant de la salle à modifier", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody SalleRequestDto salleRequest) {
        return new Response<>(salleService.update(id, salleRequest));
    }

    /**
     * Supprime une salle à partir de son identifiant.
     *
     * @param id L'identifiant de la salle à supprimer.
     * @return Response<ResponseDto> Les détails de la salle supprimée.
     * @throws Throwable si la salle n'existe pas.
     */
    @Operation(
            summary = "Supprimer une salle",
            description = "Supprime une salle du système à partir de son identifiant."
    )
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant de la salle à supprimer", example = "1", required = true)
            @PathVariable Long id) throws Throwable {
        return new Response<>(salleService.delete(id));
    }

    /**
     * Recherche une salle à partir de son nom.
     *
     * @param nom le nom de la salle recherchée
     * @return Response<ResponseDto> Les détails de la salle trouvée.
     * @throws Throwable si aucune salle ne porte ce nom.
     */
    @Operation(
            summary = "Rechercher une salle par nom",
            description = "Recherche une salle spécifique à partir de son nom exact."
    )
    @GetMapping("/recherche")
    public Response<ResponseDto> getByNom(
            @Parameter(description = "Nom de la salle recherchée", example = "Grande Salle", required = true)
            @RequestParam String nom) throws Throwable {
        return new Response<>(salleService.getByNom(nom));
    }

    /**
     * Récupère les places d'une salle, filtrées optionnellement par catégorie.
     *
     * @param id L'identifiant de la salle.
     * @param categorie Le libellé de la catégorie (optionnel).
     * @return Response<ResponseDto> La liste des places.
     */
    @Operation(
            summary = "Lister les places d'une salle",
            description = "Retourne la liste des places d'une salle, triées par rang et numéro, avec un filtre optionnel par catégorie."
    )
    @GetMapping("/{id}/places")
    public Response<ResponseDto> getPlacesBySalle(
            @Parameter(description = "Identifiant de la salle", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nom de la catégorie pour filtrer les places (optionnel)", example = "OR")
            @RequestParam(name = "categorie", required = false) String categorie) {
        return new Response<>(salleService.getPlacesBySalle(id, categorie));
    }

    /**
     * Récupère la programmation des événements d'une salle.
     *
     * @param id L'identifiant de la salle.
     * @return Response<ResponseDto> La liste des événements de la salle.
     */
    @Operation(
            summary = "Lister la programmation d'une salle",
            description = "Retourne la liste des événements programmés dans une salle, triée par date croissante."
    )
    @GetMapping("/{id}/evenements")
    public Response<ResponseDto> getEvenementsBySalle(
            @Parameter(description = "Identifiant de la salle", example = "1", required = true)
            @PathVariable Long id) {
        return new Response<>(salleService.getEvenementsBySalleId(id));
    }
}