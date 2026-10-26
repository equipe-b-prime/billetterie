package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.dto.SalleRequestDto;
import com.billetterie.billetterie.service.SalleService;
import com.billetterie.billetterie.utils.response.Response;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de gestion des salles.
 */
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

    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10")int taille) {
        return new Response<>(salleService.getAll(page,taille));
    }

    /**
     * Récupère une salle à partir de son identifiant.
     *
     * @param id L'identifiant de la salle à récupérer.
     * @return Response<ResponseDto> Les détails de la salle récupérée.
     * @throws Throwable si la salle n'existe pas.
     */
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) throws Throwable {
        return new Response<>(salleService.getById(id));
    }

    /**
     * Crée une nouvelle salle.
     *
     * @param salleRequest Les données de la salle à créer.
     * @return Response<ResponseDto> Les détails de la salle créée.
     */
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
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id,
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
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) throws Throwable {
        return new Response<>(salleService.delete(id));
    }

    /**
     * Recherche une salle à partir de son nom.
     *
     * @param nom le nom de la salle recherchée
     * @return Response<ResponseDto> Les détails de la salle trouvée.
     * @throws Throwable si aucune salle ne porte ce nom.
     */
    @GetMapping("/recherche")
    public Response<ResponseDto> getByNom(@RequestParam String nom) throws Throwable {
        return new Response<>(salleService.getByNom(nom));
    }

//    @GetMapping("/{id}/places")
//    public Response<ResponseDto> findBySalleIdOrderByRangAscNumeroAsc(@PathVariable  Long id){
//        return  new Response<>(salleService.findBySalleIdOrderByRangAscNumeroAsc(id));
//    }
//    @GetMapping("/{id}/places")
//    public Response<ResponseDto> findBySalleIdAndCategorieLibelle(@PathVariable Long id,@RequestParam(name= "categorie")
//    String libelle){
//        return  new Response<>(salleService.findBySalleIdAndCategorieLibelle(id,libelle));
//    }
    @GetMapping("/{id}/places")
    public Response<ResponseDto> getPlacesBySalle(
            @PathVariable Long id,
            @RequestParam(name = "categorie", required = false) String categorie) {
        if (categorie != null && !categorie.isBlank()) {
            return new Response<>(salleService.getPlacesBySalle(id, categorie));
        }
        return new Response<>(salleService.findBySalleIdOrderByRangAscNumeroAsc(id));
    }

    @GetMapping("/{id}/evenements")
    private Response<ResponseDto> getEvenementsBySalle(@PathVariable Long id) {
        return new Response<>(salleService.getEvenementsBySalleId(id));
    }

}
