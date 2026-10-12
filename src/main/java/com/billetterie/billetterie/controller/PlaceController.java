package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.PlaceRequestDto;
import com.billetterie.billetterie.dto.PlaceResponseDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des places.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    /**
     * Récupère la liste de toutes les places.
     *
     * @return la liste des places
     */
    @GetMapping
    public List<ResponseDto> getAll() {
        return placeService.getAll();
    }

    /**
     * Récupère une place à partir de son identifiant.
     *
     * @param id l'identifiant de la place
     * @return la place trouvée
     * @throws Throwable si la place n'est pas trouvée
     */
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable {
        return placeService.getById(id);
    }

    /**
     * Crée une nouvelle place.
     *
     * @param placeRequest les données de la requête
     * @return la place créée
     */
    @PostMapping
    public ResponseDto create(@RequestBody PlaceRequestDto placeRequest) {
        return placeService.create(placeRequest);
    }

    /**
     * Modifie une place existante.
     *
     * @param id           l'identifiant de la place
     * @param placeRequest les données de la requête
     * @return la place modifiée
     */
    @PutMapping("/{id}")
    public PlaceResponseDto update(
            @PathVariable Long id,
            @RequestBody PlaceRequestDto placeRequest) {
        return placeService.update(id, placeRequest);
    }

    /**
     * Supprime une place à partir de son identifiant.
     *
     * @param id l'identifiant de la place
     * @return la place supprimée
     * @throws Throwable si la place n'est pas trouvée
     */
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable {
        return placeService.delete(id);
    }
}
