package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.*;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.exception.RessourceUtiliseeException;
import com.billetterie.billetterie.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import java.util.function.ToIntFunction;


/**
 * Service métier gérant les événements.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
    public class EvenementService extends AbstractCrudService implements CrudService{
        private final EvenementRepository evenementRepository;
        private final SalleRepository salleRepository;
        private final PlaceRepository placeRepository;
        private final ReservationRepository reservationRepository;
        private final VuePlaceLibreRepository vuePlaceLibreRepository;
        private final VueRemplissageCategorieRepository vueRemplissageRepository;

        /**
         * Constructeur injectant les repositories des événements et des salles.
         *
         * @param evenementRepository le repository des événements
         * @param salleRepository le repository des salles
         */
        public EvenementService(EvenementRepository evenementRepository, SalleRepository salleRepository, PlaceRepository placeRepository, ReservationRepository reservationRepository, VuePlaceLibreRepository vuePlaceLibreRepository, VueRemplissageCategorieRepository vueRemplissageRepository) {
            super(evenementRepository);
            this.evenementRepository = evenementRepository;
            this.salleRepository = salleRepository;
            this.placeRepository = placeRepository;
            this.reservationRepository = reservationRepository;
            this.vuePlaceLibreRepository = vuePlaceLibreRepository;
            this.vueRemplissageRepository = vueRemplissageRepository;
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
     * Convertit spécifiquement l'entité de sans menbtionner les information de le a salle auquel il est affecte.
     * @param evenement l'entite a transfomer.
     * @return EvenementResponseDto le Dto de retour.
     */
    public EvenementResponseDto toDtoSansSalle(Evenement evenement) {
        boolean estAVenir = evenement.getHoraire() != null
                && evenement.getHoraire().isAfter(LocalDateTime.now());

        return EvenementResponseDto.builder()
                .id(evenement.getId())
                .titre(evenement.getTitre())
                .description(evenement.getDescription())
                .horaire(evenement.getHoraire())
                .placementLibre(evenement.getPlacementLibre())
                .estAVenir(estAVenir)
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

        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        Salle salle = salleRepository.findById(evenementRequest.getSalleId())
                .orElseThrow(() -> new NotFoundException("Salle non trouvée : " + evenementRequest.getSalleId()));

        boolean salleAChanger = !evenement.getSalle().getId().equals(salle.getId());
        if (salleAChanger) {
            if (reservationRepository.existsByEvenementId(id)) {
                throw new IllegalArgumentException("Impossible de changer la salle : cet événement a déjà des réservations.");
            }
            if (!placeRepository.existsBySalleId(salle.getId())) {
                throw new IllegalArgumentException("La nouvelle salle sélectionnée ne contient aucune place.");
            }
        }

        boolean dateAChangee = !evenement.getHoraire().isEqual(evenementRequest.getHoraire());
        if (dateAChangee && evenementRequest.getHoraire().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("L'horaire de l'événement ne peut pas être dans le passé.");
        }

        // 3. Mise à jour des valeurs
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
    public Page<EvenementResponseDto> getAll(int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille));
        return evenementRepository.findDetailViews(pageable)
                .map(view -> toDto(view));
    }

    /**
     * Supprime un événement à partir de son identifiant.
     *
     * @param id l'identifiant de l'événement à supprimer
     * @return EvenementResponseD        // 1. Contrôle du changement de salle
to Les détails de l'événement supprimé.
     * @throws NotFoundException si l'événement n'existe pas
     */
    public EvenementResponseDto delete(Long id) throws Throwable{
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        boolean evenementUtilisee = reservationRepository.existsByEvenementId(id);
        if (reservationRepository.existsByEvenementId(id)) {
            throw new RessourceUtiliseeException("Evenement",id,"Reservation");
        }
        evenementRepository.deleteById(id);
        return toDto(evenement);
        }

    public Page<EvenementResponseDto> findByHoraireBetweenOrderByHoraireAsc(LocalDateTime du, LocalDateTime au, int page, int taille) {
        if (du == null) {du = evenementRepository.findPremiereDate();}
        if (au == null) {au = evenementRepository.findDerniereDate();}
        if (au.isBefore(du)) {throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début.");}
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille));
        Page<Evenement> evenements = evenementRepository.findByHoraireBetweenOrderByHoraireAsc(du, au, pageable);
        return evenements.map(this::toDto);
    }
    public Page<EvenementResponseDto> getEvenements(int page, int taille, LocalDateTime du, LocalDateTime au) {
        if (du == null && au == null) {
           return getAll(page,taille);
        } else {
            return  findByHoraireBetweenOrderByHoraireAsc(du,au,page,taille) ;
        }
    }

    public List<VuePlaceLibreResponseDto> placeLibre(Long id) {
        // 1. Vérifier simplement si l'événement existe (plus rapide que findById)
        if (!evenementRepository.existsById(id)) {
            throw new NotFoundException("Événement introuvable");
        }

        // 2. Récupérer les données de la vue
        List<VuePlaceLibre> vuesPlacesLibres = vuePlaceLibreRepository.findByEvenementId(id);

        // 3. Construire le DTO sans l'événement
        return vuesPlacesLibres.stream()
                .map(vue -> VuePlaceLibreResponseDto.builder()
                        .id(vue.getId())
                        .categorie(vue.getCategorie())
                        .placesDisponibles(vue.getPlacesDisponibles())
                        .build())
                        .toList();
    }

    @Transactional(readOnly = true)
    public RemplissageResponseDto calculerTauxRemplissage(Long evenementId) {
        verifierExistenceEvenement(evenementId);

        List<RemplissageCategorieDto> detailsParCategorie = chargerDetailsParCategorie(evenementId);

        // Correction des méthodes références Lombok (getters)
        int totalPlaces = calculerTotal(detailsParCategorie, RemplissageCategorieDto::getTotalPlaces);
        validerSalleNonVide(totalPlaces);

        int totalProvisoires = calculerTotal(detailsParCategorie, RemplissageCategorieDto::getPlacesProvisoires);
        int totalPayees = calculerTotal(detailsParCategorie, RemplissageCategorieDto::getPlacesPayees);
        double pourcentageGlobal = calculerPourcentage(totalProvisoires + totalPayees, totalPlaces);

        return new RemplissageResponseDto(
                evenementId,
                totalPlaces,
                totalProvisoires,
                totalPayees,
                pourcentageGlobal,
                detailsParCategorie
        );
    }


    private void verifierExistenceEvenement(Long evenementId) {
        if (!evenementRepository.existsById(evenementId)) {
            throw new NotFoundException("Événement introuvable ID : " + evenementId);
        }
    }

    private List<RemplissageCategorieDto> chargerDetailsParCategorie(Long evenementId) {
        return vueRemplissageRepository.findByEvenementId(evenementId).stream()
                .map(this::mapToCategorieDto)
                .toList();
    }

    private RemplissageCategorieDto mapToCategorieDto(VueRemplissageCategorie vue) {
        int total = vue.getTotalPlaces();
        int provisoires = vue.getPlacesProvisoires();
        int payees = vue.getPlacesPayees();

        double pourcentageCategorie = calculerPourcentage(provisoires + payees, total);

        return new RemplissageCategorieDto(
                vue.getNomCategorie(),
                total,
                provisoires,
                payees,
                pourcentageCategorie
        );
    }

    private void validerSalleNonVide(int totalPlaces) {
        if (totalPlaces == 0) {
            throw new IllegalStateException("La salle associée à cet événement ne possède aucune place.");
        }
    }

    private int calculerTotal(List<RemplissageCategorieDto> details, ToIntFunction<RemplissageCategorieDto> getter) {
        return details.stream().mapToInt(getter).sum();
    }

    private double calculerPourcentage(int valeur, int total) {
        if (total == 0) return 0.0;
        double pourcentage = (valeur * 100.0) / total;
        return Math.round(pourcentage * 100.0) / 100.0;
    }
}