package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.CategorieRequestDto;
import com.billetterie.billetterie.dto.CategorieResponseDto;
import com.billetterie.billetterie.dto.RequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.entity.Categorie;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.CategorieRepository;
import org.springframework.stereotype.Service;

/**
 * Service métier gérant les catégories.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class CategorieService extends AbstractCrudService {
    private final CategorieRepository categorieRepository;

    /**
     * Constructeur injectant le repository des catégories.
     *
     * @param categorieRepository le repository des catégories
     */
    public CategorieService(CategorieRepository categorieRepository) {
        super(categorieRepository);
        this.categorieRepository = categorieRepository;
    }

    /**
     * Convertit les données d'une requête en entité catégorie.
     *
     * @param categorieRequestDto Les données de la requête pour créer la catégorie.
     * @return Categorie L'entité catégorie créée.
     */
    public Categorie toEntity(CategorieRequestDto categorieRequestDto) {
        return Categorie.builder()
                .libelle(categorieRequestDto.getNom())
                .description(categorieRequestDto.getDescription())
                .build();
    }

    /**
     * Convertit une entité catégorie en objet de réponse.
     *
     * @param entity L'entité catégorie à convertir.
     * @return CategorieResponseDto Les détails de la catégorie convertie.
     */
    @Override
    public ResponseDto toDto(Object entity) {
        Categorie categorie = (Categorie) entity;
        return CategorieResponseDto.builder()
                .id(categorie.getId())
                .nom(categorie.getLibelle())
                .description(categorie.getDescription())
                .build();
    }

    /**
     * Crée une nouvelle catégorie.
     *
     * @param request Les données de la requête pour créer la catégorie.
     * @return CategorieResponseDto Les détails de la catégorie créée.
     */
    @Override
    public ResponseDto create(RequestDto request) {
        CategorieRequestDto categorieRequest = (CategorieRequestDto) request;
        categorieRequest.setNom(categorieRequest.getNom().trim());
        categorieRequest.setDescription(categorieRequest.getDescription().trim());
        Categorie categorieSauvegardee = categorieRepository.save(toEntity(categorieRequest));
        return toDto(categorieSauvegardee);
    }

    /**
     * Modifie une catégorie existante.
     *
     * @param id L'identifiant de la catégorie à modifier.
     * @param requestDto Les données de la requête pour modifier la catégorie.
     * @return CategorieResponseDto Les détails de la catégorie modifiée.
     */
    @Override
    public ResponseDto update(Long id, RequestDto requestDto) {
        CategorieRequestDto categorieRequest = (CategorieRequestDto) requestDto;
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        categorie.setLibelle(categorieRequest.getNom().trim());
        categorie.setDescription(categorieRequest.getDescription().trim());
        categorieRepository.save(categorie);
        return toDto(categorie);
    }
}
