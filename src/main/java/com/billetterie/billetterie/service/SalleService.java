package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.RequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.dto.SalleRequestDto;
import com.billetterie.billetterie.dto.SalleResponseDto;
import com.billetterie.billetterie.entity.Categorie;
import com.billetterie.billetterie.entity.Place;
import com.billetterie.billetterie.entity.Salle;
import com.billetterie.billetterie.exception.DoublonRessourceException;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.exception.RessourceUtiliseeException;
import com.billetterie.billetterie.repository.EvenementRepository;
import com.billetterie.billetterie.repository.PlaceRepository;
import com.billetterie.billetterie.repository.SalleRepository;
import org.hibernate.annotations.DialectOverride;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier gérant les salles.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class SalleService extends AbstractCrudService {
    private final SalleRepository salleRepository;
    private final EvenementRepository evenementRepository;
    private final PlaceRepository placeRepository;


    /**
     * Constructeur injectant le repository des salles.
     *
     * @param salleRepository le repository des salles
     */
    public SalleService(SalleRepository salleRepository, EvenementRepository evenementRepository, PlaceRepository placeRepository) {
        super(salleRepository);
        this.salleRepository = salleRepository;
        this.evenementRepository = evenementRepository;
        this.placeRepository = placeRepository;
    }

    /**
     * Convertit les données d'une requête en entité salle.
     *
     * @param salleRequest Les données de la requête pour créer la salle.
     * @return Salle L'entité salle créée.
     */
    public Salle toEntity(SalleRequestDto salleRequest) {
        return Salle.builder()
                .capacite(salleRequest.getCapacite())
                .nom(salleRequest.getNom())
                .ville(salleRequest.getVille())
                .adresse(salleRequest.getAdresse())
                .build();
    }

    /**
     * Convertit une entité salle en objet de réponse.
     *
     * @param entity L'entité salle à convertir.
     * @return SalleResponseDto Les détails de la salle convertie.
     */
    @Override
    public ResponseDto toDto(Object entity) {
        Salle salle = (Salle) entity;
        int totalPlacesEnregistrees = (int) placeRepository.countBySalleId(salle.getId());
        return SalleResponseDto.builder()
                .id(salle.getId())
                .nom(salle.getNom())
                .ville(salle.getVille())
                .capacite(salle.getCapacite())
                .adresse(salle.getAdresse())
                .nbplaceOccupee(totalPlacesEnregistrees)
                .build();
    }

    /**
     * Crée une nouvelle salle.
     *
     * @param requestDto Les données de la requête pour créer la salle.
     * @return SalleResponseDto Les détails de la salle créée.
     */
    @Override
    public ResponseDto create(RequestDto requestDto) {
        SalleRequestDto salleRequest = (SalleRequestDto) requestDto;
        if(salleRepository.existsByNomAndVille(salleRequest.getNom(), salleRequest.getVille())) {
            throw new DoublonRessourceException("Une salle nommée '" + salleRequest.getNom() + "' existe déjà à "
                    + salleRequest.getVille());
        }
        Salle salle = salleRepository.save(toEntity(salleRequest));
        return toDto(salle);
    }

    /**
     * Modifie une salle existante.
     *
     * @param id L'identifiant de la salle à modifier.
     * @param requestDto Les données de la requête pour modifier la salle.
     * @return SalleResponseDto Les détails de la salle modifiée.
     */
    @Override
    public ResponseDto update(Long id, RequestDto requestDto) {
        SalleRequestDto salleRequest = (SalleRequestDto) requestDto;
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Salle non trouvée"));
        boolean nomOuVilleModifie = !salle.getNom().equalsIgnoreCase(salleRequest.getNom())
                || !salle.getVille().equalsIgnoreCase(salleRequest.getVille());

        if (nomOuVilleModifie && salleRepository.existsByNomAndVille(salleRequest.getNom(), salleRequest.getVille())) {
            throw new DoublonRessourceException("Une autre salle nommée '" + salleRequest.getNom() + "' existe déjà à "
                    + salleRequest.getVille());
        }
        if (salleRequest.getCapacite() < placeRepository.countBySalleId(id) ) {
          throw new IllegalArgumentException("La capacité (" + salleRequest.getCapacite() + ") ne peut pas être" +
                  " inférieure au nombre de places déjà enregistrées (" + placeRepository.countBySalleId(id)  + ").");
        }
        salle.setNom(salleRequest.getNom());
        salle.setVille(salleRequest.getVille());
        salle.setCapacite(salleRequest.getCapacite());
        salle.setAdresse(salleRequest.getAdresse());
        salleRepository.save(salle);
        return toDto(salle);
    }

    public ResponseDto getByNom(String libelle) {
        Salle salle = salleRepository.findByNom(libelle)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        return toDto(salle);
    }

    /**
     * Supprime une entité de la base de données après avoir vérifié son existence,
     * puis retourne les informations de l'entité supprimée.
     *
     * @param id l'identifiant de l'entité à supprimer
     * @return un objet {@link ResponseDto} contenant les données de l'entité tout juste supprimée
     * @throws NotFoundException si l'entité à supprimer n'existe pas
     */
    @Override
    public ResponseDto delete(Long id) throws Throwable{
        Object entity = salleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        boolean evenementUtilisee = evenementRepository.existsBySalleId(id);
        boolean placeUtilisee = placeRepository.existsBySalleId(id);
        if (evenementUtilisee) {
            throw new RessourceUtiliseeException("Salle",id,"Evenement");
        }if (placeUtilisee) {
            throw new RessourceUtiliseeException("Salle",id,"Place");
        }
        salleRepository.deleteById(id);
        return toDto(entity);
    }
}
