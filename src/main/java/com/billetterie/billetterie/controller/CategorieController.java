package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.CategorieRequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des catégories.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/categorie")
public class CategorieController {

    private final CategorieService categorieService;

    /**
     * Récupère la liste de toutes les catégories.
     *
     * @return la liste des catégories
     */
    @GetMapping
    public List<ResponseDto> getAll() {
        return categorieService.getAll();
    }

    /**
     * Crée une nouvelle catégorie.
     *
     * @param categorieRequest les données de la requête
     * @return la catégorie créée
     */
    @PostMapping()
    private ResponseDto create(@RequestBody CategorieRequestDto categorieRequest) {
        return categorieService.create(categorieRequest);
    }

    /**
     * Récupère une catégorie par son identifiant.
     *
     * @param id l'identifiant de la catégorie
     * @return la catégorie trouvée
     * @throws Throwable si la catégorie n'est pas trouvée
     */
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable {
        return categorieService.getById(id);
    }

    /**
     * Supprime une catégorie par son identifiant.
     *
     * @param id l'identifiant de la catégorie
     * @return la catégorie supprimée
     * @throws Throwable si la catégorie n'est pas trouvée
     */
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable {
        return categorieService.delete(id);
    }

    /**
     * Modifie une catégorie existante.
     *
     * @param id               l'identifiant de la catégorie
     * @param categorieRequest les données de la requête
     * @return la catégorie modifiée
     */
    @PutMapping("/{id}")
    public ResponseDto update(@PathVariable Long id, @RequestBody CategorieRequestDto categorieRequest) {
        return categorieService.update(id, categorieRequest);
    }
}
