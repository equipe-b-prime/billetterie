package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.*;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.exception.RessourceUtiliseeException;
import com.billetterie.billetterie.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service métier gérant les réservations.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class ReservationService extends AbstractCrudService {

    private final ReservationRepository reservationRepository;
    private final StatutRepository statutRepository;
    private final EvenementRepository evenementRepository;
    private final ClientRepository clientRepository;
    private final CategorieRepository categorieRepository;
    private final PlaceRepository placeRepository;
    private final TarifEvenementRepository tarifEvenementRepository;
    private final BilletRepository billetRepository;
    private final PaiementRepository paiementRepository;



    /**
     * Constructeur injectant les repositories des réservations, des statuts, des événements et des clients.
     *
     * @param reservationRepository le repository des réservations
     * @param statutRepository le repository des statuts
     * @param evenementRepository le repository des événements
     * @param clientRepository le repository des clients
     */
    public ReservationService(
            ReservationRepository reservationRepository,
            StatutRepository statutRepository,
            EvenementRepository evenementRepository,
            ClientRepository clientRepository, CategorieRepository categorieRepository, PlaceRepository placeRepository, TarifEvenementRepository tarifEvenementRepository, BilletRepository billetRepository, PaiementRepository paiementRepository) {
        super(reservationRepository);
        this.reservationRepository = reservationRepository;
        this.statutRepository = statutRepository;
        this.evenementRepository = evenementRepository;
        this.clientRepository = clientRepository;
        this.categorieRepository = categorieRepository;
        this.placeRepository = placeRepository;
        this.tarifEvenementRepository = tarifEvenementRepository;
        this.billetRepository = billetRepository;
        this.paiementRepository = paiementRepository;
    }

    /**
     * Convertit les données d'une requête en entité réservation.
     *
     * @param reservationRequest Les données de la requête pour créer la réservation.
     * @param statut Le statut associé.
     * @param client Le client associé.
     * @param evenement L'événement associé.
     * @return Reservation L'entité réservation créée.
     */
    public Reservation toEntity(ReservationRequestDto reservationRequest, Statut statut, Client client, Evenement evenement) {
        return Reservation.builder()
                .dateCreation(reservationRequest.getDateCreation())
                .datePaiement(reservationRequest.getDatePaiement())
                .dateAnnulation(reservationRequest.getDateAnnulation())
                .client(client)
                .montantTotal(reservationRequest.getMontantTotal())
                .statut(statut)
                .evenement(evenement)
                .build();
    }

    /**
     * Convertit une entité réservation en objet de réponse.
     *
     * @param entity L'entité réservation à convertir.
     * @return ReservationResponseDto Les détails de la réservation convertie.
     */
    @Override
    public ResponseDto toDto(Object entity) {
        Reservation reservation = (Reservation) entity;
        return ReservationResponseDto.builder()
                .id(reservation.getId())
                .dateCreation(reservation.getDateCreation())
                .datePaiement(reservation.getDatePaiement())
                .dateAnnulation(reservation.getDateAnnulation())
                .montantTotal(reservation.getMontantTotal())
                .client(ClientResponseDto.builder()
                        .id(reservation.getClient().getId())
                        .nom(reservation.getClient().getNom())
                        .prenom(reservation.getClient().getPrenom())
                        .telephone(reservation.getClient().getTelephone())
                        .email(reservation.getClient().getEmail())
                        .build()
                ).evenement(EvenementResponseDto.builder()
                        .id(reservation.getEvenement().getId())
                        .titre(reservation.getEvenement().getTitre())
                        .description(reservation.getEvenement().getDescription())
                        .horaire(reservation.getEvenement().getHoraire())
                        .placementLibre(reservation.getEvenement().getPlacementLibre())
                        .build()
                ).statut(StatutResponseDto.builder()
                        .id(reservation.getStatut().getId())
                        .libelle(reservation.getStatut().getLibelle())
                        .build()
                ).build();
    }

    /**
     * Modifie une réservation existante.
     *
     * @param id L'identifiant de la réservation à modifier.
     * @param requestDto Les données de la requête pour modifier la réservation.
     * @return ReservationResponseDto Les détails de la réservation modifiée.
     */
    @Override
    public ResponseDto update(Long id, RequestDto requestDto) {
        ReservationRequestDto reservationRequest = (ReservationRequestDto) requestDto;
        Statut statut = statutRepository.findById(reservationRequest.getStatutId())
                .orElseThrow(() -> new NotFoundException("Statut non trouvé"));
        Client client = clientRepository.findById(reservationRequest.getClientId())
                .orElseThrow(() -> new NotFoundException("Client non trouvé"));
        Evenement evenement = evenementRepository.findById(reservationRequest.getEvenementId())
                .orElseThrow(() -> new NotFoundException("Événement non trouvé"));
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Réservation non trouvée"));
        reservation.setDatePaiement(reservationRequest.getDatePaiement());
        reservation.setDateAnnulation(reservationRequest.getDateAnnulation());
        reservation.setMontantTotal(reservationRequest.getMontantTotal());
        reservation.setStatut(statut);
        reservation.setClient(client);
        reservation.setEvenement(evenement);
        return toDto(reservationRepository.save(reservation));
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
    public Page<ResponseDto> getAllSansEmail(int page, int taille) {

        Sort sort = Sort.by(Sort.Order.desc("dateCreation"));
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille),sort);

        return reservationRepository.findAll(pageable)
                .map(this::toDto);
    }

    public Page<ResponseDto> getReservationByEmailClient(String email, int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), Sort.by("id"));
        return reservationRepository.findReservationByEmailClient(email, pageable).map(this::toDto);
    }

    public Page<ResponseDto> getAll (String email, int page, int taille){
        if (email == null || email.isEmpty()) {
            return getAllSansEmail(page, taille);
        } else {
            return getReservationByEmailClient(email, page, taille);
        }
    }

    public ResponseDto getById(Long id) throws Throwable {
        Object entity = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        return toDto(entity);
    }

    /**
     * Crée une nouvelle réservation.
     *
     * @param requestDto Les données de la requête pour créer la réservation.
     * @return ReservationResponseDto Les détails de la réservation créée.
     */
    @Override
    public ResponseDto create(RequestDto requestDto) {
        ReservationRequestDto reservationRequestDto = (ReservationRequestDto) requestDto;


        Client client = clientRepository.findById(reservationRequestDto.getClientId())
                .orElseThrow(() -> new NotFoundException("Client non trouvé : " + reservationRequestDto.getClientId()));

        Evenement evenement = evenementRepository.findByIdForUpdate(reservationRequestDto.getEvenementId())
                .orElseThrow(() -> new NotFoundException("Événement non trouvé : " + reservationRequestDto.getEvenementId()));

        Statut statutProvisoire = statutRepository.findByLibelle("PROVISOIRE")
                .orElseThrow(() -> new NotFoundException("Statut PROVISOIRE non configuré"));

        Long salleId = evenement.getSalle().getId();
        BigDecimal montantTotalGlobal = BigDecimal.ZERO;

        List<Place> toutesLesPlaces = new ArrayList<>();
        Map<Place, BigDecimal> prixParPlace = new HashMap<>();

        // 2. Traitement de chaque catégorie sélectionnée
        for (LigneReservationDto ligne : reservationRequestDto.getLignes()) {
            Categorie categorie = categorieRepository.findById(ligne.getCategorieId())
                    .orElseThrow(() -> new NotFoundException("Catégorie non trouvée : " + ligne.getCategorieId()));

            // Récupération du tarif pour ce couple (Événement + Catégorie)
            BigDecimal prixUnitaire = tarifEvenementRepository
                    .findPrixParEvenementEtCategorie(evenement.getId(), categorie.getId())
                    .orElseThrow(() -> new NotFoundException("Tarif non trouvé pour la catégorie : " + categorie.getLibelle()));

            // Recherche des places disponibles dans la salle
            int quantiteDemandee = ligne.getQuantite();
            List<Place> placesDisponibles = placeRepository.findPlacesDisponibles(
                    salleId,
                    evenement.getId(),
                    categorie.getId(),
                    PageRequest.of(0, quantiteDemandee)
            );

            // Vérification de la disponibilité
            if (placesDisponibles.size() < quantiteDemandee) {
                throw new IllegalStateException("Places insuffisantes pour la catégorie " + categorie.getLibelle()
                        + ". Demandées : " + quantiteDemandee + ", Disponibles : " + placesDisponibles.size());
            }

            // Cumul du montant total
            BigDecimal sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantiteDemandee));
            montantTotalGlobal = montantTotalGlobal.add(sousTotal);

            // Association des places avec leur prix
            for (Place place : placesDisponibles) {
                toutesLesPlaces.add(place);
                prixParPlace.put(place, prixUnitaire);
            }
        }

        // 3. Sauvegarde de la Réservation
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setEvenement(evenement);
        reservation.setStatut(statutProvisoire);
        reservation.setDateCreation(LocalDateTime.now());
        reservation.setMontantTotal(montantTotalGlobal);

        Reservation reservationSauvegardee = reservationRepository.save(reservation);

        // 4. Création des Billets
        List<Billet> billets = new ArrayList<>();
        for (Place place : toutesLesPlaces) {
            Billet billet = Billet.builder()
                    .reservation(reservationSauvegardee)
                    .place(place)
                    .prixApplique(prixParPlace.get(place))
                    .codeBillet("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .build();

            billets.add(billet);
        }

        billetRepository.saveAll(billets);

        return toDto(reservationSauvegardee);
    }


    /**
     * Récupère une page des réservations d'un client spécifique.
     *
     * @param clientId l'identifiant du client
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments souhaités par page
     * @return une page d'objets {@link ResponseDto}
     */
    public Page<ResponseDto> getReservationByIdClient(long clientId, int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), Sort.by("id"));
        return reservationRepository.findByClientId(clientId, pageable).map(this::toDto);
    }

    /**
     * Récupère une page des réservations ayant un statut spécifique.
     *
     * @param statutId l'identifiant du statut
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments souhaités par page
     * @return une page d'objets {@link ResponseDto}
     */
    public Page<ResponseDto> getReservationByIdStatut(long statutId, int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), Sort.by("id"));
        return reservationRepository.findByStatutId(statutId, pageable).map(this::toDto);
    }

    /**
     * Récupère une page des réservations d'un événement spécifique.
     *
     * @param id l'identifiant de l'événement
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments souhaités par page
     * @return une page d'objets {@link ResponseDto}
     */
    public Page<ResponseDto> getReservationByIdEvenement(long id, int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), Sort.by("id"));
        return reservationRepository.findByEvenementId(id, pageable).map(this::toDto);
    }
    /**
     * Supprime une réservation de la base de données après avoir vérifié son existence et son statut.
     * La suppression est autorisée uniquement pour les réservations en statut 'PROVISOIRE' ou 'ANNULEE'.
     *
     * @param id l'identifiant de la réservation à supprimer
     * @return un objet {@link ResponseDto} contenant les données de l'entité supprimée
     * @throws NotFoundException si la réservation n'existe pas
     * @throws IllegalStateException si la réservation est dans un statut non supprimable (ex: PAYEE)
     */
    public ResponseDto delete(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        String statutLibelle = reservation.getStatut().getLibelle();
        if (!("PROVISOIRE".equalsIgnoreCase(statutLibelle) ||"ANNULEE".equalsIgnoreCase(statutLibelle))) {
            throw new IllegalArgumentException(
                    "Impossible de supprimer la réservation  Une réservation payée ne peut pas être " +
                            "supprimée, elle doit être annulée."
            );
        }
        if (paiementRepository.existsByReservationId(id)){
            throw new RessourceUtiliseeException("Reservation",id,"Paiement");
        }

        ResponseDto responseDto = toDto(reservation);
        reservationRepository.delete(reservation);
        return responseDto;
    }
}
