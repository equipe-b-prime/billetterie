package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.dto.SalleRequestDto;
import com.billetterie.billetterie.dto.SalleResponseDto;
import com.billetterie.billetterie.service.SalleService;
import com.billetterie.billetterie.utils.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/salle")
public class SalleController {

    private final SalleService salleService;
    /**
     * @Param Récupère la liste de toutes les salles.
     * @return List<SalleResponseDto> La liste des salles.
     */
    @GetMapping
    public List<ResponseDto> getAllSalle() {
        return salleService.getAll();
    }
    /**
     * Crée une nouvelle salle.
     * @param salleRequest Les données de la requête pour créer la salle.
     * @return SalleResponseDto Les détails de la salle créée.
     */
    @PostMapping
    public ResponseDto  createSalle(@RequestBody SalleRequestDto salleRequest) {
        return salleService.create(salleRequest);
    }
    /**
     * Récupère une salle à partir de son identifiant.
     * @param id L'identifiant de la salle à récupérer.
     * @return SalleResponseDto Les détails de la salle récupérée.
     */
    @GetMapping("/{id}")
    public ResponseDto  getSalleById(@PathVariable Long id) throws Throwable {
        return salleService.getById(id);
    }
    /**
     * Modifie une salle existante.
     * @param id L'identifiant de la salle à modifier.
     * @param salleRequest Les données de la requête pour modifier la salle.
     * @return SalleResponseDto Les détails de la salle modifiée.
     */
    @PutMapping("/{id}")
    public ResponseDto updatesalle(
            @PathVariable Long id,
            @RequestBody SalleRequestDto salleRequest) {
        return salleService.update(id, salleRequest);
    }
    /**
     * Supprime une salle à partir de son identifiant.
     * @param id L'identifiant de la salle à supprimer.
     * @return SalleResponseDto Les détails de la salle supprimée.
     */
    @DeleteMapping("/{id}")
    public ResponseDto deleteSalle(@PathVariable Long id) throws Throwable {
        return salleService.delete(id);
    }
}