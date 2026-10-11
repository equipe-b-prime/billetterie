package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.Categorie;
import com.billetterie.billetterie.entity.Place;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.CategorieRepository;
import com.billetterie.billetterie.repository.PlaceRepository;
import com.billetterie.billetterie.entity.Salle;
import com.billetterie.billetterie.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.plaf.metal.MetalBorders;
import java.util.List;

@Service
public class PlaceService extends AbstractCrudService{
    private final PlaceRepository placeRepository;
    private final SalleRepository salleRepository;
    private final CategorieRepository categorieRepository;
    public PlaceService(PlaceRepository placeRepository, SalleRepository salleRepository, CategorieRepository categorieRepository) {
        super(placeRepository);
        this.placeRepository = placeRepository;
        this.salleRepository = salleRepository;
        this.categorieRepository = categorieRepository;
    }

    public Place toEntity(PlaceRequestDto placeRequest, Salle salle, Categorie categorie) {
        return Place.builder()
                .numero(placeRequest.getNumero())
                .rang(placeRequest.getRang())
                .salle(salle)
                .categorie(categorie)
                .build();
    }
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
    @Override
    public PlaceResponseDto create(RequestDto requestDto) {
        PlaceRequestDto placeRequest = (PlaceRequestDto) requestDto;
        Salle salle = salleRepository.findById(placeRequest.getSalleId())
                .orElseThrow(() -> new NotFoundException("Categorie non trouvée"));
        Categorie categorie = categorieRepository.findById(placeRequest.getCategorieId())
                .orElseThrow(() -> new NotFoundException("Categorie non trouvée"));
        Place place = toEntity(placeRequest, salle, categorie);
        return toDto(placeRepository.save(place));
    }
    @Override
    public PlaceResponseDto update(Long id, RequestDto requestDto) {
        PlaceRequestDto placeRequest = (PlaceRequestDto) requestDto;
        Salle salle = salleRepository.findById(placeRequest.getSalleId())
                .orElseThrow(() -> new NotFoundException("Salle non trouvée"));
        Categorie categorie = categorieRepository.findById(placeRequest.getCategorieId())
                .orElseThrow(() -> new NotFoundException("Categorie non trouvée"));
        Place place = placeRepository.findById(id).orElseThrow(() -> new NotFoundException("Place non trouvée"));
        place.setNumero(placeRequest.getNumero());
        place.setRang(placeRequest.getRang());
        place.setSalle(salle);
        place.setCategorie(categorie);
        return toDto(placeRepository.save(place));

    }
}
