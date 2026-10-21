package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.Evenement;
import com.billetterie.billetterie.entity.EvenementDetailView;
import com.billetterie.billetterie.entity.Place;
import com.billetterie.billetterie.entity.Salle;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.exception.RessourceUtiliseeException;
import com.billetterie.billetterie.repository.EvenementRepository;
import com.billetterie.billetterie.repository.PlaceRepository;
import com.billetterie.billetterie.repository.ReservationRepository;
import com.billetterie.billetterie.repository.SalleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service métier gérant les événements.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
    public class EvenementService implements CrudService{
        private final EvenementRepository evenementRepository;
        private final SalleRepository salleRepository;
        private final PlaceRepository placeRepository;
        private final ReservationRepository reservationRepository;

        /**
         * Constructeur injectant les repositories des événements et des salles.
         *
         * @param evenementRepository le repository des événements
         * @param salleRepository le repository des salles
         */
        public EvenementService(EvenementRepository evenementRepository, SalleRepository salleRepository, PlaceRepository placeRepository, ReservationRepository reservationRepository) {
            this.evenementRepository = evenementRepository;
            this.salleRepository = salleRepository;
            this.placeRepository = placeRepository;
            this.reservationRepository = reservationRepository;
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
         * Convertit spécifiquement l'entité de la VUE SQL.
         */
        public EvenementResponseDto toDto(EvenementDetailView view) {
            return EvenementResponseDto.builder()
                    .id(view.getId())
                    .titre(view.getTitre())
                    .description(view.getDescription())
                    .horaire(view.getHoraire())
                    .placementLibre(view.getPlacementLibre())
                    .salle(SalleResponseDto.builder()
                            .id(view.getSalleId())
                            .nom(view.getSalleNom())
                            .ville(view.getSalleVille())
                            .capacite(view.getSalleCapacite() != null ? view.getSalleCapacite() : 0)
                            .adresse(view.getSalleAdresse())
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
            throw new IllegalArgumentException(
                    "L'horaire de l'événement ne peut pas être dans le passé");
        }
        if (!placeRepository.existsBySalleId(evenementRequest.getSalleId())) {
            throw new IllegalArgumentException("Attention, la salle sélectionnée ne contient aucune place. " +
                    "Veuillez choisir une salle avec au moins une place.");
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
        boolean dateAchangee = !evenement.getHoraire().isEqual(evenementRequest.getHoraire());
        if (dateAchangee && evenementRequest.getHoraire().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("L'horaire de l'événement ne peut pas être dans le passé");
        }
        boolean estReservee = evenement.getSalle().getId().equals(salle.getId());
        if (reservationRepository.existsByEvenementId(id) && estReservee) {
            throw new IllegalArgumentException("La nouvelle salle sélectionnée ne contient aucune place.");
        }
        evenement.setTitre(evenementRequest.getTitre());
        evenement.setDescription(evenementRequest.getDescription());
        evenement.setHoraire(evenementRequest.getHoraire());
        evenement.setPlacementLibre(evenementRequest.getPlacementLibre());
        evenement.setSalle(salle);
        return toDto(evenementRepository.save(evenement));
    }
    /**
     * Récupère un événement par son identifiant en s'appuyant sur la vue SQL.
     *
     * @param id l'identifiant de l'événement recherché
     * @return un objet {@link ResponseDto} représentant l'événement trouvé
     * @throws NotFoundException si aucun événement ne correspond à cet identifiant
     */
    public ResponseDto getById(Long id) {
        EvenementDetailView view = evenementRepository.findDetailViewById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        return toDto(view);
    }

    /**
     * Récupère une page d'événements à partir de la vue SQL.
     *
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments souhaités par page
     * @return une page d'objets {@link ResponseDto}
     */
    public Page<ResponseDto> getAll(int page, int taille) {
        Sort sort = Sort.by(Sort.Order.asc("horaire"));
        Pageable pageable = PageRequest.of(page, taille,sort);

        return evenementRepository.findDetailViews(pageable)
                .map(view -> toDto(view));
    }

    /**
     * Supprime un événement à partir de son identifiant.
     *
     * @param id l'identifiant de l'événement à supprimer
     * @return EvenementResponseDto Les détails de l'événement supprimé.
     * @throws NotFoundException si l'événement n'existe pas
     */
    public EvenementResponseDto delete(Long id) throws Throwable{
        Object entity = evenementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        boolean evenementUtilisee = reservationRepository.existsByEvenementId(id);
        if (reservationRepository.existsByEvenementId(id)) {
            throw new RessourceUtiliseeException("Evenement",id,"Reservation");
        }
        evenementRepository.deleteById(id);
        return toDto(entity);
        }
}
