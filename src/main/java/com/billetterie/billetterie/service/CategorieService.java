package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.CategorieRequestDto;
import com.billetterie.billetterie.dto.CategorieResponseDto;
import com.billetterie.billetterie.dto.RequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.entity.Categorie;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.CategorieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

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
        categorieRequest.setNom(categorieRequest.getNom());
        categorieRequest.setDescription(categorieRequest.getDescription());
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
        categorieRequest.setNom(categorieRequest.getNom());
        categorieRequest.setDescription(categorieRequest.getDescription());
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        categorie.setLibelle(categorieRequest.getNom());
        categorie.setDescription(categorieRequest.getDescription());
        categorieRepository.save(categorie);
        return toDto(categorie);
    }

    /**
     * Récupère une page d'entités présentes dans la base de données
     * et les convertit en DTOs de réponse.
     * La page et la taille transmises sont normalisées par {@link CrudService}.
     *
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments souhaités par page
     * @return une page d'objets {@link ResponseDto}
     */
    public Page<ResponseDto> getAll(int page, int taille) {

        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille));

        return categorieRepository.findAll(pageable)
                .map(this::toDto);
    }
    /**
     * Recherche une entité spécifique par son identifiant et la convertit en DTO.
     *
     * @param id l'identifiant unique de l'entité recherchée
     * @return un objet {@link ResponseDto} représentant l'entité trouvée
     * @throws NotFoundException si aucune entité ne correspond à cet identifiant
     */

    public ResponseDto getById(Long id) throws Throwable {
        Object entity = categorieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        return toDto(entity);
    }

    public List<ResponseDto> getByNom(String libelle) {
        Categorie categorie= categorieRepository.findByLibelle(libelle)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        return List.of(toDto(categorie));
    }
    /**
     * Supprime une entité de la base de données après avoir vérifié son existence,
     * puis retourne les informations de l'entité supprimée.
     *
     * @param id l'identifiant de l'entité à supprimer
     * @return un objet {@link ResponseDto} contenant les données de l'entité tout juste supprimée
     * @throws NotFoundException si l'entité à supprimer n'existe pas
     */
    public ResponseDto delete(Long id) throws Throwable{
        Object entity = categorieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        categorieRepository.deleteById(id);
        return toDto(entity);
    }
}
