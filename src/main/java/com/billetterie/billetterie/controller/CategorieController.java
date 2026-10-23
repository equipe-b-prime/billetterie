package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.CategorieRequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.CategorieService;
import com.billetterie.billetterie.utils.response.Response;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de gestion des catégories.
 */
@RestController
@RequestMapping("/categories")
public class CategorieController {
    private final CategorieService categorieService;

    /**
     * Constructeur injectant le service des catégories.
     *
     * @param categorieService le service métier des catégories
     */
    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    /**
     * Récupère la liste de toutes les catégories.
     *
     * @return Response<ResponseDto> La liste des catégories encapsulée.
     */
    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10")int taille) {
        return new Response<>(categorieService.getAll(page,taille).getContent());
    }

    /**
     * Récupère une catégorie à partir de son identifiant.
     *
     * @param id L'identifiant de la catégorie à récupérer.
     * @return Response<ResponseDto> Les détails de la catégorie récupérée.
     * @throws Throwable si la catégorie n'existe pas.
     */
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) throws Throwable {
        return new Response<>(categorieService.getById(id));
    }

    /**
     * Recherche une catégorie à partir de son nom.
     *
     * @param nom le nom (libellé) de la catégorie recherchée
     * @return Response<ResponseDto> Les détails de la catégorie trouvée.
     * @throws Throwable si aucune catégorie ne porte ce nom.
     */
    @GetMapping("/recherche")
    public Response<ResponseDto> getByNom(@RequestParam String nom) throws Throwable {
        return new Response<>(categorieService.getByNom(nom));
    }

    /**
     * Crée une nouvelle catégorie.
     *
     * @param categorieRequest Les données de la catégorie à créer.
     * @return Response<ResponseDto> Les détails de la catégorie créée.
     */
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody CategorieRequestDto categorieRequest) {
        return new Response<>(categorieService.create(categorieRequest));
    }

    /**
     * Modifie une catégorie existante.
     *
     * @param id L'identifiant de la catégorie à modifier.
     * @param categorieRequest Les nouvelles données de la catégorie.
     * @return Response<ResponseDto> Les détails de la catégorie modifiée.
     */
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id,
                                        @Valid @RequestBody CategorieRequestDto categorieRequest) {
        return new Response<>(categorieService.update(id, categorieRequest));
    }

    /**
     * Supprime une catégorie à partir de son identifiant.
     *
     * @param id L'identifiant de la catégorie à supprimer.
     * @return Response<ResponseDto> Les détails de la catégorie supprimée.
     * @throws Throwable si la catégorie n'existe pas.
     */
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) throws Throwable {
        return new Response<>(categorieService.delete(id));
    }
}
