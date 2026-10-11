package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.CategorieRequestDto;
import com.billetterie.billetterie.dto.CategorieResponseDto;
import com.billetterie.billetterie.dto.RequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.entity.Categorie;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.CategorieRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
@Service
public class CategorieService extends AbstractCrudService {
    private final CategorieRepository categorieRepository;
    public CategorieService(CategorieRepository categorieRepository) {
        super(categorieRepository);
        this.categorieRepository = categorieRepository;
    }

    public Categorie toEntity(CategorieRequestDto CategorieRequestDto) {
        return Categorie.builder()
                .libelle(CategorieRequestDto.getNom())
                .description(CategorieRequestDto.getDescription())
                .build();
    }
    @Override
    public ResponseDto toDto(Object entity) {
        Categorie categorie = (Categorie) entity;
        return CategorieResponseDto.builder()
                .id(categorie.getId())
                .nom(categorie.getLibelle())
                .description(categorie.getDescription())
                .build();
    }

  @Override
    public ResponseDto create(RequestDto request) {
        CategorieRequestDto categorieRequest = (CategorieRequestDto) request;
        categorieRequest.setNom(categorieRequest.getNom().trim());
        categorieRequest.setDescription(categorieRequest.getDescription().trim());
        Categorie categorieSauvegardee = categorieRepository.save(toEntity(categorieRequest));
        return toDto(categorieSauvegardee);
    }

    @Override
    public ResponseDto update(Long id, RequestDto requestDto) {
        CategorieRequestDto categorieRequest = (CategorieRequestDto) requestDto;
        Categorie categorie = categorieRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        categorie.setLibelle(categorieRequest.getNom().trim());
        categorieRequest.setDescription(categorieRequest.getDescription().trim());
        categorieRepository.save(categorie);
        return toDto(categorie);
    }
}