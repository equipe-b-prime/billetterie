package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.dto.SalleRequestDto;
import com.billetterie.billetterie.service.SalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des salles.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/salle")
public class SalleController {

    private final SalleService salleService;

    /**
     * Récupère la liste de toutes les salles.
     *
     * @return la liste des salles
     */
    @GetMapping
    public List<ResponseDto> getAll() {
        return salleService.getAll();
    }

    /**
     * Crée une nouvelle salle.
     *
     * @param salleRequest les données de la requête
     * @return la salle créée
     */
    @PostMapping
    public ResponseDto create(@RequestBody SalleRequestDto salleRequest) {
        return salleService.create(salleRequest);
    }

    /**
     * Récupère une salle à partir de son identifiant.
     *
     * @param id l'identifiant de la salle
     * @return la salle trouvée
     * @throws Throwable si la salle n'est pas trouvée
     */
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable {
        return salleService.getById(id);
    }

    /**
     * Modifie une salle existante.
     *
     * @param id           l'identifiant de la salle
     * @param salleRequest les données de la requête
     * @return la salle modifiée
     */
    @PutMapping("/{id}")
    public ResponseDto update(
            @PathVariable Long id,
            @RequestBody SalleRequestDto salleRequest) {
        return salleService.update(id, salleRequest);
    }

    /**
     * Supprime une salle à partir de son identifiant.
     *
     * @param id l'identifiant de la salle
     * @return la salle supprimée
     * @throws Throwable si la salle n'est pas trouvée
     */
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable {
        return salleService.delete(id);
    }
}
