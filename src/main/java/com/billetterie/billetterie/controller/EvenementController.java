package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.EvenementRequestDto;
import com.billetterie.billetterie.dto.EvenementResponseDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.EvenementService;
import com.billetterie.billetterie.utils.response.Response;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Contrôleur REST exposant les endpoints de gestion des événements.
 */
@RestController
@RequestMapping("/evenements")
public class EvenementController {
    private final EvenementService evenementService;

    /**
     * Constructeur injectant le service des événements.
     *
     * @param evenementService le service métier des événements
     */
    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    /**
     * Récupère une page d'événements.
     *
     * @param page l'index de la page souhaitée (0 par défaut)
     * @param taille le nombre d'éléments par page (10 par défaut, borné à 100)
     * @return Response<ResponseDto> La page des événements encapsulée.
     */
    @GetMapping("/ok")
    public Response<EvenementResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int taille) {
        return new Response<>(evenementService.getAll(page, taille));
    }
    /**
     * Récupère un événement à partir de son identifiant.
     *
     * @param id L'identifiant de l'événement à récupérer.
     * @return Response<ResponseDto> Les détails de l'événement récupéré.
     * @throws Throwable si l'événement n'existe pas.
     */
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) throws Throwable {
        return new Response<>(evenementService.getById(id));
    }

    /**
     * Crée un nouvel événement.
     *
     * @param evenementRequest Les données de l'événement à créer.
     * @return Response<ResponseDto> Les détails de l'événement créé.
     */
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody EvenementRequestDto evenementRequest) {
        return new Response<>(evenementService.create(evenementRequest));
    }
    /**
     * Modifie un événement existant.
     *
     * @param id L'identifiant de l'événement à modifier.
     * @param evenementRequest Les nouvelles données de l'événement.
     * @return Response<ResponseDto> Les détails de l'événement modifié.
     */
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id,
                                        @Valid @RequestBody EvenementRequestDto evenementRequest) {
        return new Response<>(evenementService.update(id, evenementRequest));
    }
    /**
     * Supprime un événement à partir de son identifiant.
     *
     * @param id L'identifiant de l'événement à supprimer.
     * @return Response<ResponseDto> Les détails de l'événement supprimé.
     * @throws Throwable si l'événement n'existe pas.
     */
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) throws Throwable {
        return new Response<>(evenementService.delete(id));
    }

    @GetMapping("/recherche")
    public Response<EvenementResponseDto> findByHoraireBetweenOrderByHoraireAsc(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime du,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime au,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int taille) {
        return new Response<>(evenementService.findByHoraireBetweenOrderByHoraireAsc(du, au,page,taille));
    }

    @GetMapping
    public Response<EvenementResponseDto> getEvenements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int taille,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime du,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime au) {
        return new Response<>(evenementService.getEvenements(page, taille, du, au));
    }
}

