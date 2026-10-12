package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.CategorieRequestDto;
import com.billetterie.billetterie.dto.CategorieResponseDto;
import com.billetterie.billetterie.dto.RequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.entity.Categorie;
import com.billetterie.billetterie.repository.CategorieRepository;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des catégories.
 */
@Service
public class CategorieService extends AbstractCrudService {

    private final CategorieRepository categorieRepository;

    /**
     * Construit le service avec son repository.
     *
     * @param categorieRepository le repository des catégories
     */
    public CategorieService(CategorieRepository categorieRepository) {
        super(categorieRepository);
        this.categorieRepository = categorieRepository;
    }

    /**
     * Convertit une requête en entité Categorie.
     *
     * @param categorieRequest les données de la requête
     * @return l'entité Categorie construite
     */
    public Categorie toEntity(CategorieRequestDto categorieRequest) {
        return Categorie.builder()
                .libelle(categorieRequest.getNom())
                .description(categorieRequest.getDescription())
                .build();
    }

    /**
     * Convertit une entité Categorie en DTO de réponse.
     *
     * @param entity l'entité à convertir
     * @return le DTO de réponse
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
     * @param request les données de la requête
     * @return le DTO de la catégorie créée
     */
    @Override
    public ResponseDto create(RequestDto request) {
        CategorieRequestDto categorieRequest = (CategorieRequestDto) request;
        Categorie categorieSauvegardee = categorieRepository.save(toEntity(categorieRequest));
        return toDto(categorieSauvegardee);
    }

    /**
     * Modifie une catégorie existante.
     *
     * @param id         l'identifiant de la catégorie à modifier
     * @param requestDto les données de la requête
     * @return le DTO de la catégorie modifiée
     */
    @Override
    public ResponseDto update(Long id, RequestDto requestDto) {
        CategorieRequestDto categorieRequest = (CategorieRequestDto) requestDto;
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categorie non trouvée"));
        categorie.setLibelle(categorieRequest.getNom());
        categorie.setDescription(categorieRequest.getDescription());
        categorieRepository.save(categorie);
        return toDto(categorie);
    }
}
