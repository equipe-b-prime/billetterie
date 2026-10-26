package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.*;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.exception.RessourceUtiliseeException;
import com.billetterie.billetterie.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service métier gérant les réservations.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Slf4j
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
    private final RemboursementRepository remboursementRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM- yyyy HH:mm:ss");



    /**
     * Constructeur injectant les repositories des réservations, des statuts, des événements et des clients.
     *
     * @param reservationRepository le repository des réservations
     * @param statutRepository      le repository des statuts
     * @param evenementRepository   le repository des événements
     * @param clientRepository      le repository des clients
     */
    public ReservationService(
            ReservationRepository reservationRepository,
            StatutRepository statutRepository,
            EvenementRepository evenementRepository,
            ClientRepository clientRepository, CategorieRepository categorieRepository, PlaceRepository placeRepository, TarifEvenementRepository tarifEvenementRepository, BilletRepository billetRepository, PaiementRepository paiementRepository, RemboursementRepository remboursementRepository) {
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
        this.remboursementRepository = remboursementRepository;
    }

    /**
     * Convertit les données d'une requête en entité réservation.
     *
     * @param reservationRequest Les données de la requête pour créer la réservation.
     * @param statut             Le statut associé.
     * @param client             Le client associé.
     * @param evenement          L'événement associé.
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
     * @param id         L'identifiant de la réservation à modifier.
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
     * @param page   l'index de la page souhaitée
     * @param taille le nombre d'éléments souhaités par page
     * @return une page d'objets {@link ResponseDto}
     */
    public Page<ResponseDto> getAllSansEmail(int page, int taille) {

        Sort sort = Sort.by(Sort.Order.desc("dateCreation"));
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), sort);

        return reservationRepository.findAll(pageable)
                .map(this::toDto);
    }

    public Page<ResponseDto> getReservationByEmailClient(String email, int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), Sort.by("id"));
        return reservationRepository.findReservationByEmailClient(email, pageable).map(this::toDto);
    }

    public Page<ResponseDto> getAll(String email, int page, int taille) {
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
    @Transactional
    public ResponseDto create(RequestDto requestDto) {
        ReservationRequestDto reservationRequestDto = (ReservationRequestDto) requestDto;


        Client client = clientRepository.findById(reservationRequestDto.getClientId())
                .orElseThrow(() -> new NotFoundException("Client non trouvé : " + reservationRequestDto.getClientId()));

        Evenement evenement = evenementRepository.findByIdForUpdate(reservationRequestDto.getEvenementId())
                .orElseThrow(() -> new NotFoundException("Événement non trouvé : " + reservationRequestDto.getEvenementId()));
        if (evenement.getHoraire().isBefore(LocalDateTime.now())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
            String dateFormatee = evenement.getHoraire().format(formatter);
            throw new IllegalArgumentException("Impossible de réserver pour un événement passé.");
        }

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
     * @param clientId l'identifiant du clientTrue
     * @param page     l'index de la page souhaitée
     * @param taille   le nombre d'éléments souhaités par page
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
     * @param page     l'index de la page souhaitée
     * @param taille   le nombre d'éléments souhaités par page
     * @return une page d'objets {@link ResponseDto}
     */
    public Page<ResponseDto> getReservationByIdStatut(long statutId, int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), Sort.by("id"));
        return reservationRepository.findByStatutId(statutId, pageable).map(this::toDto);
    }

    /**
     * Récupère une page des réservations d'un événement spécifique.
     *
     * @param id     l'identifiant de l'événement
     * @param page   l'index de la page souhaitée
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
     * @throws NotFoundException     si la réservation n'existe pas
     * @throws IllegalStateException si la réservation est dans un statut non supprimable (ex: PAYEE)
     */
    public ResponseDto delete(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        String statutLibelle = reservation.getStatut().getLibelle();
        if (!("PROVISOIRE".equalsIgnoreCase(statutLibelle) || "ANNULEE".equalsIgnoreCase(statutLibelle))) {
            throw new IllegalArgumentException(
                    "Impossible de supprimer la réservation  Une réservation payée ne peut pas être " +
                            "supprimée, elle doit être annulée."
            );
        }
        if (paiementRepository.existsByReservationId(id)) {
            throw new RessourceUtiliseeException("Reservation", id, "Paiement");
        }

        ResponseDto responseDto = toDto(reservation);
        reservationRepository.delete(reservation);
        return responseDto;
    }

    public ResponseDto passerDeProvisoreApaye(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        String statutLibelle = reservation.getStatut().getLibelle();
        if (!"PROVISOIRE".equalsIgnoreCase(statutLibelle)) {
            throw new IllegalStateException(
                    "Impossible de passer la réservation en " +
                            "statut PAYEE. Seules les réservations en statut PROVISOIRE sont autorisee");

        }
        Statut statut = statutRepository.findByLibelleIgnoreCase("PAYEE")
                .orElseThrow(() -> new NotFoundException("Statut PAYEE non configuré"));
        reservation.setStatut(statut);
        return toDto(reservationRepository.save(reservation));
    }


    /**
     * Annule une réservation existante.
     * Si la réservation a déjà été payée, déclenche automatiquement le calcul et la génération
     * d'un remboursement en fonction du temps restant avant l'événement.
     * Met ensuite à jour le statut de la réservation à "ANNULEE".
     *
     * @param id L'identifiant unique de la réservation à annuler.
     * @return Un message de confirmation indiquant le succès de l'opération.
     * @throws NotFoundException Si la réservation ou le statut "ANNULEE" n'existe pas en base de données.
     * @throws IllegalStateException Si la réservation a déjà le statut "ANNULEE".
     */
    @Transactional
    public String annulerReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Réservation non trouvée : " + id));

        if ("ANNULEE".equalsIgnoreCase(reservation.getStatut().getLibelle())) {
            throw new IllegalStateException("Impossible d'annuler. La réservation est déjà annulée.");
        }

        // 1. Traitement du remboursement si nécessaire
        if ("PAYEE".equalsIgnoreCase(reservation.getStatut().getLibelle())) {
            genererRemboursementSiEligible(reservation);
        }

        Statut statutAnnulee = statutRepository.findByLibelleIgnoreCase("ANNULEE")
                .orElseThrow(() -> new NotFoundException("Statut ANNULEE non configuré"));

        reservation.setStatut(statutAnnulee);
        reservation.setDateAnnulation(LocalDateTime.now());

        reservationRepository.save(reservation); // Un seul save() global suffit grâce à @Transactional

        return "Réservation annulée avec succès.";
    }
    /**
     * Crée et sauvegarde une entité Remboursement si le client y est éligible
     * (c'est-à-dire si le montant calculé est strictement supérieur à zéro).
     * Marque également la réservation comme remboursée.
     *
     * @param reservation La réservation payée pour laquelle un remboursement doit être évalué.
     */
    private void genererRemboursementSiEligible(Reservation reservation) {
        BigDecimal montant = calculerMontantRemboursement(reservation);

        if (montant.compareTo(BigDecimal.ZERO) > 0) {
            Remboursement remboursement = new Remboursement();
            remboursement.setReservation(reservation);
            remboursement.setMontant(montant);
            remboursement.setDateRemboursement(LocalDateTime.now());
            remboursementRepository.save(remboursement);

            reservation.setEstRembourse(true);
        }
    }

    /**
     * Calcule le montant à rembourser selon les règles métier (exigence F12) :
     * - Plus de 48h avant l'événement : 100% du montant payé.
     * - Entre 24h et 48h avant l'événement : 50% du montant payé.
     * - Moins de 24h avant l'événement (ou événement passé) : 0% (aucun remboursement).
     *
     * @param reservation La réservation pour laquelle le calcul est effectué.
     * @return Le montant exact à rembourser (peut être égal à BigDecimal.ZERO).
     * @throws NotFoundException Si l'enregistrement du paiement lié à la réservation est introuvable.
     */
    private BigDecimal calculerMontantRemboursement(Reservation reservation) {
        long heuresRestantes = Duration.between(LocalDateTime.now(), reservation.getEvenement().getHoraire()).toHours();

        if (heuresRestantes < 24) {
            return BigDecimal.ZERO;
        }

        Paiement paiement = paiementRepository.findByReservationId(reservation.getId())
                .orElseThrow(() -> new NotFoundException("Paiement introuvable pour la réservation"));

        if (heuresRestantes > 48) {
            return paiement.getMontant();
        } else {
            return paiement.getMontant().multiply(new BigDecimal("0.5"));
        }
    }
    /**
     * Tâche planifiée s'exécutant automatiquement toutes les minutes (60 000 millisecondes)
     * pour libérer les places bloquées par des réservations non finalisées.
     *
     * Règle métier (F16) : Toute réservation ayant le statut "PROVISOIRE" dont la date de
     * création remonte à plus de 15 minutes est considérée comme expirée. Son statut
     * bascule en "ANNULEE" pour rendre la place à nouveau disponible à la vente.
     * Les annulations sont tracées dans les journaux de l'application.
     *
     * @throws NotFoundException Si le statut "ANNULEE" est introuvable en base de données.
     */
    @Scheduled(fixedRate = 60000)
    public void libererReservationsExpirées() {
        LocalDateTime tempsExpiration = LocalDateTime.now().minusMinutes(15);
        List<Reservation> reservationexpiree = reservationRepository.findReservationExpirées(tempsExpiration);
        if (reservationexpiree.isEmpty()) return;
        Statut statutAnnule = statutRepository.findByLibelle("ANNULEE")
                .orElseThrow(() -> new NotFoundException("Statut ANNULEE non configuré"));
        for (Reservation r :reservationexpiree){
            r.setStatut(statutAnnule);
            LocalDateTime momentExpiration = r.getDateCreation().plusMinutes(15);

            log.info("La réservation provisoire n°{} créée à {} a été annulée automatiquement (expirée à {}).",
                    r.getId(),
                    r.getDateCreation(),
                    tempsExpiration);
        }
        reservationRepository.saveAll(reservationexpiree);
    }

    @Transactional(readOnly = true)
    public byte[] exporterReservationsCsv(Long evenementId, boolean inclureAnnulees) {

        if (!evenementRepository.existsById(evenementId)) {
            throw new NotFoundException("Événement introuvable ID : " + evenementId);
        }
        List<ReservationExportDto> reservations = reservationRepository.findReservationsPourExport(evenementId, inclureAnnulees);

        StringBuilder csv = new StringBuilder("rang;numéro;catégorie;nom du client;email;statut;date de création\n");

        for (ReservationExportDto r : reservations) {
            String date = r.getDateCreation() != null ? r.getDateCreation().format(DATE_FORMATTER) : "";

            csv.append(r.getRang()).append(";")
                    .append(r.getNumero()).append(";")
                    .append(r.getNomCategorie()).append(";")
                    .append(r.getNomClient()).append(";")
                    .append(r.getEmailClient()).append(";")
                    .append(r.getStatut()).append(";")
                    .append(date).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
