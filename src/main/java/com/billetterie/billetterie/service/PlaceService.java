package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.Categorie;
import com.billetterie.billetterie.entity.Place;
import com.billetterie.billetterie.entity.Salle;
import com.billetterie.billetterie.exception.DoublonRessourceException;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.BilletRepository;
import com.billetterie.billetterie.repository.CategorieRepository;
import com.billetterie.billetterie.repository.PlaceRepository;
import com.billetterie.billetterie.repository.SalleRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final BilletRepository billetRepository;
    /**
     * Constructeur injectant les repositories des places, des salles et des catégories.
     *
     * @param placeRepository le repository des places
     * @param salleRepository le repository des salles
     * @param categorieRepository le repository des catégories
     */
    public PlaceService(PlaceRepository placeRepository, SalleRepository salleRepository, CategorieRepository categorieRepository, BilletRepository billetRepository) {
        super(placeRepository);
        this.placeRepository = placeRepository;
        this.salleRepository = salleRepository;
        this.categorieRepository = categorieRepository;
        this.billetRepository = billetRepository;
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
        long nbPlacesActuelles = placeRepository.countBySalleId(placeRequest.getSalleId());
        Salle salle = salleRepository.findById(placeRequest.getSalleId())
                .orElseThrow(() -> new NotFoundException("Salle non trouvée"));
        Categorie categorie = categorieRepository.findById(placeRequest.getCategorieId())
                .orElseThrow(() -> new NotFoundException("Categorie non trouvée"));
        Place place = toEntity(placeRequest, salle, categorie);
        if (placeRepository.existsBySalleIdAndRangAndNumero(placeRequest.getSalleId(), placeRequest.getRang(),
                placeRequest.getNumero())) {
            throw new DoublonRessourceException("Une place avec le même rang et numéro existe déjà dans cette salle.");
        }
        if (nbPlacesActuelles + 1 > salle.getCapacite()) {
            throw new IllegalArgumentException("La salle est pleine, impossible d'ajouter une nouvelle place.");
        }
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
        if (placeRepository.existsBySalleIdAndRangAndNumeroAndIdNot(placeRequest.getSalleId(), placeRequest.getRang(),
                placeRequest.getNumero(), id)) {
            throw new DoublonRessourceException("Une place avec le même rang et numéro existe déjà dans cette salle.");
        }
        boolean estPleine = placeRepository.countBySalleId(placeRequest.getSalleId()) > salle.getCapacite();
        if (estPleine) {
            throw new IllegalArgumentException(" La salle est pleine, impossible de modifier la place.");
        }
        boolean rangOunumeroEstModifiee = place.getRang() != placeRequest.getRang()
                || place.getNumero() != placeRequest.getNumero();
        if (billetRepository.existsByPlaceId(id) && rangOunumeroEstModifiee ){
            throw new IllegalArgumentException("Impossible de modifier le rang ou le numéro d'une place qui a déjà été réservée.");
        }
        if (placeRepository.existsByRangAndNumeroAndSalleIdAndIdNot(placeRequest.getRang(), placeRequest.getNumero(), id, id)) {
            throw new DoublonRessourceException("Une place avec le même rang et numéro existe déjà dans cette salle.");
        }
        place.setNumero(placeRequest.getNumero());
        place.setRang(placeRequest.getRang());
        place.setSalle(salle);
        place.setCategorie(categorie);
        return toDto(placeRepository.save(place));
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
    @Override
    public Page<ResponseDto> getAll(int page, int taille) {
        Sort sort = Sort.by(Sort.Order.asc("rang"), Sort.Order.asc("numero"));
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille),sort);
        return placeRepository.findAll(pageable)
                .map(this::toDto);
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
        Object entity = placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        if(billetRepository.existsByPlaceId(id)){
            throw new IllegalArgumentException("Impossible de supprimer une place qui a déjà été réservée.");
        }
        placeRepository.deleteById(id);
        return toDto(entity);
    }
}
