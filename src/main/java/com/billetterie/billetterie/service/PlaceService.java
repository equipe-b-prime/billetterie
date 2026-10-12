package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.Categorie;
import com.billetterie.billetterie.entity.Place;
import com.billetterie.billetterie.entity.Salle;
import com.billetterie.billetterie.repository.CategorieRepository;
import com.billetterie.billetterie.repository.PlaceRepository;
import com.billetterie.billetterie.repository.SalleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de gestion des places.
 */
@Service
public class PlaceService extends AbstractCrudService {

    private final PlaceRepository placeRepository;
    private final SalleRepository salleRepository;
    private final CategorieRepository categorieRepository;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param placeRepository     le repository des places
     * @param salleRepository     le repository des salles
     * @param categorieRepository le repository des catégories
     */
    public PlaceService(PlaceRepository placeRepository, SalleRepository salleRepository, CategorieRepository categorieRepository) {
        super(placeRepository);
        this.placeRepository = placeRepository;
        this.salleRepository = salleRepository;
        this.categorieRepository = categorieRepository;
    }

    /**
     * Convertit une requête en entité Place.
     *
     * @param placeRequest les données de la requête
     * @param salle        la salle associée
     * @param categorie    la catégorie associée
     * @return l'entité Place construite
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
     * Convertit une entité Place en DTO de réponse.
     *
     * @param entity l'entité à convertir
     * @return le DTO de réponse
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
     * @param requestDto les données de la requête
     * @return le DTO de la place créée
     */
    @Override
    public PlaceResponseDto create(RequestDto requestDto) {
        PlaceRequestDto placeRequest = (PlaceRequestDto) requestDto;
        Salle salle = salleRepository.findById(placeRequest.getSalleId())
                .orElseThrow(() -> new RuntimeException("Categorie non trouvée"));
        Categorie categorie = categorieRepository.findById(placeRequest.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Categorie non trouvée"));
        Place place = toEntity(placeRequest, salle, categorie);
        return toDto(placeRepository.save(place));
    }

    /**
     * Modifie une place existante.
     *
     * @param id         l'identifiant de la place à modifier
     * @param requestDto les données de la requête
     * @return le DTO de la place modifiée
     */
    @Override
    public PlaceResponseDto update(Long id, RequestDto requestDto) {
        PlaceRequestDto placeRequest = (PlaceRequestDto) requestDto;
        Salle salle = salleRepository.findById(placeRequest.getSalleId())
                .orElseThrow(() -> new RuntimeException("Salle non trouvée"));
        Categorie categorie = categorieRepository.findById(placeRequest.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Categorie non trouvée"));
        Place place = placeRepository.findById(id).orElseThrow(() -> new RuntimeException("Place non trouvée"));
        place.setNumero(placeRequest.getNumero());
        place.setRang(placeRequest.getRang());
        place.setSalle(salle);
        place.setCategorie(categorie);
        return toDto(placeRepository.save(place));
    }
}
