package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.Categorie;
import com.billetterie.billetterie.entity.Place;
import com.billetterie.billetterie.entity.Salle;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.CategorieRepository;
import com.billetterie.billetterie.repository.PlaceRepository;
import com.billetterie.billetterie.repository.SalleRepository;
import org.springframework.stereotype.Service;

/**
 * Service métier gérant les places.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class PlaceService extends AbstractCrudService {
    private final PlaceRepository placeRepository;
    private final SalleRepository salleRepository;
    private final CategorieRepository categorieRepository;

    /**
     * Constructeur injectant les repositories des places, des salles et des catégories.
     *
     * @param placeRepository le repository des places
     * @param salleRepository le repository des salles
     * @param categorieRepository le repository des catégories
     */
    public PlaceService(PlaceRepository placeRepository, SalleRepository salleRepository, CategorieRepository categorieRepository) {
        super(placeRepository);
        this.placeRepository = placeRepository;
        this.salleRepository = salleRepository;
        this.categorieRepository = categorieRepository;
    }

    /**
     * Convertit les données d'une requête en entité place.
     *
     * @param placeRequest Les données de la requête pour créer la place.
     * @param salle La salle associée.
     * @param categorie La catégorie associée.
     * @return Place L'entité place créée.
     */
    public Place toEntity(PlaceRequestDto placeRequest, Salle salle, Categorie categorie) {
        return Place.builder()
                .numero(placeRequest.getNumero())
                .rang(placeRequest.getRang())
                .salle(salle)
                .categorie(categorie)
                .build();
    }

    /**
     * Convertit une entité place en objet de réponse.
     *
     * @param entity L'entité place à convertir.
     * @return PlaceResponseDto Les détails de la place convertie.
     */
    @Override
    public PlaceResponseDto toDto(Object entity) {
        Place place = (Place) entity;
        return PlaceResponseDto.builder()
                .id(place.getId())
                .numero(place.getNumero())
                .rang(place.getRang())
                .salle(SalleResponseDto.builder()
                        .id(place.getSalle().getId())
                        .capacite(place.getSalle().getCapacite())
                        .nom(place.getSalle().getNom())
                        .ville(place.getSalle().getVille())
                        .adresse(place.getSalle().getAdresse())
                        .build())
                .categorie(CategorieResponseDto.builder()
                        .id(place.getCategorie().getId())
                        .nom(place.getCategorie().getLibelle())
                        .description(place.getCategorie().getDescription())
                        .build())
                .build();
    }

    /**
     * Crée une nouvelle place.
     *
     * @param requestDto Les données de la requête pour créer la place.
     * @return PlaceResponseDto Les détails de la place créée.
     */
    @Override
    public PlaceResponseDto create(RequestDto requestDto) {
        PlaceRequestDto placeRequest = (PlaceRequestDto) requestDto;
        Salle salle = salleRepository.findById(placeRequest.getSalleId())
                .orElseThrow(() -> new NotFoundException("Salle non trouvée"));
        Categorie categorie = categorieRepository.findById(placeRequest.getCategorieId())
                .orElseThrow(() -> new NotFoundException("Categorie non trouvée"));
        Place place = toEntity(placeRequest, salle, categorie);
        return toDto(placeRepository.save(place));
    }

    /**
     * Modifie une place existante.
     *
     * @param id L'identifiant de la place à modifier.
     * @param requestDto Les données de la requête pour modifier la place.
     * @return PlaceResponseDto Les détails de la place modifiée.
     */
    @Override
    public PlaceResponseDto update(Long id, RequestDto requestDto) {
        PlaceRequestDto placeRequest = (PlaceRequestDto) requestDto;
        Salle salle = salleRepository.findById(placeRequest.getSalleId())
                .orElseThrow(() -> new NotFoundException("Salle non trouvée"));
        Categorie categorie = categorieRepository.findById(placeRequest.getCategorieId())
                .orElseThrow(() -> new NotFoundException("Categorie non trouvée"));
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Place non trouvée"));
        place.setNumero(placeRequest.getNumero());
        place.setRang(placeRequest.getRang());
        place.setSalle(salle);
        place.setCategorie(categorie);
        return toDto(placeRepository.save(place));
    }
}
