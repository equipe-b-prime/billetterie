package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.Evenement;
import com.billetterie.billetterie.entity.Salle;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.EvenementRepository;
import com.billetterie.billetterie.repository.SalleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service métier gérant les événements.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class EvenementService extends AbstractCrudService {
    private final EvenementRepository evenementRepository;
    private final SalleRepository salleRepository;

    /**
     * Constructeur injectant les repositories des événements et des salles.
     *
     * @param evenementRepository le repository des événements
     * @param salleRepository le repository des salles
     */
    public EvenementService(EvenementRepository evenementRepository, SalleRepository salleRepository) {
        super(evenementRepository);
        this.evenementRepository = evenementRepository;
        this.salleRepository = salleRepository;
    }

    /**
     * Convertit une entité événement en objet de réponse.
     *
     * @param entity L'entité événement à convertir.
     * @return EvenementResponseDto Les détails de l'événement converti.
     */
    @Override
    public EvenementResponseDto toDto(Object entity) {
        Evenement evenement = (Evenement) entity;
        return EvenementResponseDto.builder()
                .id(evenement.getId())
                .titre(evenement.getTitre())
                .description(evenement.getDescription())
                .horaire(evenement.getHoraire())
                .placementLibre(evenement.getPlacementLibre())
                .salle(SalleResponseDto.builder()
                        .id(evenement.getSalle().getId())
                        .nom(evenement.getSalle().getNom())
                        .ville(evenement.getSalle().getVille())
                        .capacite(evenement.getSalle().getCapacite())
                        .adresse(evenement.getSalle().getAdresse())
                        .build())
                .build();
    }

    /**
     * Convertit les données d'une requête en entité événement.
     *
     * @param evenementRequest Les données de la requête pour créer l'événement.
     * @param salle La salle associée à l'événement.
     * @return Evenement L'entité événement créée.
     */
    public Evenement toEntity(EvenementRequestDto evenementRequest, Salle salle) {
        return Evenement.builder()
                .titre(evenementRequest.getTitre())
                .description(evenementRequest.getDescription())
                .horaire(evenementRequest.getHoraire())
                .placementLibre(evenementRequest.getPlacementLibre())
                .salle(salle)
                .build();
    }

    /**
     * Crée un nouvel événement.
     *
     * @param requestDto les informations de l'événement à créer
     * @return l'événement créé
     */
    @Override
    public ResponseDto create(RequestDto requestDto) {
        EvenementRequestDto evenementRequest = (EvenementRequestDto) requestDto;
        Salle salle = salleRepository.findById(evenementRequest.getSalleId())
                .orElseThrow(() -> new NotFoundException("Salle non trouvée"));
        if (evenementRequest.getHoraire().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("L'horaire de l'événement ne peut pas être dans le passé");
        }
        return toDto(evenementRepository.save(toEntity(evenementRequest, salle)));
    }

    /**
     * Modifie un événement existant.
     *
     * @param id L'identifiant de l'événement à modifier.
     * @param requestDto Les données de la requête pour modifier l'événement.
     * @return EvenementResponseDto Les détails de l'événement modifié.
     */
    @Override
    public ResponseDto update(Long id, RequestDto requestDto) {
        EvenementRequestDto evenementRequest = (EvenementRequestDto) requestDto;
        Salle salle = salleRepository.findById(evenementRequest.getSalleId())
                .orElseThrow(() -> new NotFoundException("Salle non trouvée"));
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        if (evenementRequest.getHoraire().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("L'horaire de l'événement ne peut pas être dans le passé");
        }
        evenement.setTitre(evenementRequest.getTitre());
        evenement.setDescription(evenementRequest.getDescription());
        evenement.setHoraire(evenementRequest.getHoraire());
        evenement.setPlacementLibre(evenementRequest.getPlacementLibre());
        evenement.setSalle(salle);
        return toDto(evenementRepository.save(evenement));
    }
}
