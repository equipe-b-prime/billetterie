package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.EvenementRequestDto;
import com.billetterie.billetterie.dto.EvenementResponseDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.EvenementService;
import com.billetterie.billetterie.utils.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evenement")

public class EvenementController {
    private final EvenementService evenementService;

    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    /**
     * Crée un nouvel événement.
     *
     * @param evenementRequest les informations de l'événement à créer
     * @return l'événement créé
     */
    @PostMapping
    public ResponseDto create(
            @RequestBody EvenementRequestDto evenementRequest) {
        return evenementService.create(evenementRequest);
    }
    /**
     * Récupère un événement à partir de son identifiant.
     *
     * @param id L'identifiant de l'événement à récupérer.
     * @return EvenementResponseDto Les détails de l'événement récupéré.
     */
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable{
        return evenementService.getById(id);
    }

    /**
     * Récupère la liste de tous les événements.
     *
     * @return List<EvenementResponseDto> La liste des événements.
     */
    @GetMapping
    public List<ResponseDto> getAll() {
        return evenementService.getAll();
    }

    /**
     * Modifie un événement existant.
     *
     * @param id L'identifiant de l'événement à modifier.
     * @param evenementRequest Les données de la requête pour modifier l'événement.
     * @return EvenementResponseDto Les détails de l'événement modifié.
     */
    @PutMapping("/{id}")
    public ResponseDto update(@PathVariable Long id, @RequestBody EvenementRequestDto evenementRequest) {
        return evenementService.update(id, evenementRequest);
    }

    /**
     * Supprime un événement à partir de son identifiant.
     *
     * @param id L'identifiant de l'événement à supprimer.
     * @return EvenementResponseDto Les détails de l'événement supprimé.
     */
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable{
        return evenementService.delete(id);
    }
}
