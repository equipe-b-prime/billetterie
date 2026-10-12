package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.Client;
import com.billetterie.billetterie.entity.Evenement;
import com.billetterie.billetterie.entity.Reservation;
import com.billetterie.billetterie.entity.Statut;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.ClientRepository;
import com.billetterie.billetterie.repository.EvenementRepository;
import com.billetterie.billetterie.repository.ReservationRepository;
import com.billetterie.billetterie.repository.StatutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
            ClientRepository clientRepository) {
        super(reservationRepository);
        this.reservationRepository = reservationRepository;
        this.statutRepository = statutRepository;
        this.evenementRepository = evenementRepository;
        this.clientRepository = clientRepository;
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
                .montantRembourse(reservationRequest.getMontantRembourse())
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
                .montantRembourse(reservation.getMontantRembourse())
                .client(ClientResponseDto.builder()
                        .id(reservation.getClient().getId())
                        .nom(reservation.getClient().getNom())
                        .prenom(reservation.getClient().getPrenom())
                        .telephone(reservation.getClient().getTelephone())
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
        reservation.setMontantRembourse(reservationRequest.getMontantRembourse());
        reservation.setStatut(statut);
        reservation.setClient(client);
        reservation.setEvenement(evenement);
        return toDto(reservationRepository.save(reservation));
    }

    /**
     * Crée une nouvelle réservation.
     *
     * @param requestDto Les données de la requête pour créer la réservation.
     * @return ReservationResponseDto Les détails de la réservation créée.
     */
    @Override
    public ResponseDto create(RequestDto requestDto) {
        ReservationRequestDto reservationRequest = (ReservationRequestDto) requestDto;
        Statut statut = statutRepository.findById(reservationRequest.getStatutId())
                .orElseThrow(() -> new NotFoundException("Statut non trouvé"));
        Client client = clientRepository.findById(reservationRequest.getClientId())
                .orElseThrow(() -> new NotFoundException("Client non trouvé"));
        Evenement evenement = evenementRepository.findById(reservationRequest.getEvenementId())
                .orElseThrow(() -> new NotFoundException("Événement non trouvé"));
        Reservation reservation = toEntity(reservationRequest, statut, client, evenement);
        reservation.setDateCreation(LocalDateTime.now());
        return toDto(reservationRepository.save(reservation));
    }
}
