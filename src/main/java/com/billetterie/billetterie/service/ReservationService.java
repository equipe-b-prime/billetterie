package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.*;
import com.billetterie.billetterie.entity.Client;
import com.billetterie.billetterie.entity.Evenement;
import com.billetterie.billetterie.entity.Reservation;
import com.billetterie.billetterie.entity.Statut;
import com.billetterie.billetterie.repository.ClientRepository;
import com.billetterie.billetterie.repository.EvenementRepository;
import com.billetterie.billetterie.repository.ReservationRepository;
import com.billetterie.billetterie.repository.StatutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de gestion des réservations.
 */
@Service
public class ReservationService extends AbstractCrudService {

    private final ReservationRepository reservationRepository;
    private final StatutRepository statutRepository;
    private final EvenementRepository evenementRepository;
    private final ClientRepository clientRepository;

    /**
     * Construit le service avec ses dépendances.
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
            ClientRepository clientRepository) {

        super(reservationRepository);

        this.reservationRepository = reservationRepository;
        this.statutRepository = statutRepository;
        this.evenementRepository = evenementRepository;
        this.clientRepository = clientRepository;
    }

    /**
     * Convertit une requête en entité Reservation.
     *
     * @param reservationRequest les données de la requête
     * @param statut             le statut associé
     * @param client             le client associé
     * @param evenement          l'événement associé
     * @return l'entité Reservation construite
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
     * Convertit une entité Reservation en DTO de réponse.
     *
     * @param entity l'entité à convertir
     * @return le DTO de réponse
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
                )
                .evenement(EvenementResponseDto.builder()
                        .id(reservation.getEvenement().getId())
                        .titre(reservation.getEvenement().getTitre())
                        .description(reservation.getEvenement().getDescription())
                        .horaire(reservation.getEvenement().getHoraire())
                        .placementLibre(reservation.getEvenement().getPlacementLibre())
                        .build()
                )
                .statut(StatutResponseDto.builder()
                        .id(reservation.getStatut().getId())
                        .libelle(reservation.getStatut().getLibelle())
                        .build()
                )
                .build();
    }

    /**
     * Modifie une réservation existante.
     *
     * @param id         l'identifiant de la réservation à modifier
     * @param requestDto les données de la requête
     * @return le DTO de la réservation modifiée
     */
    @Override
    public ResponseDto update(Long id, RequestDto requestDto) {
        ReservationRequestDto reservationRequest = (ReservationRequestDto) requestDto;
        Statut statut = statutRepository.findById(reservationRequest.getStatutId())
                .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
        Client client = clientRepository.findById(reservationRequest.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Evenement evenement = evenementRepository.findById(reservationRequest.getEvenementId())
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
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
     * @param requestDto les données de la requête
     * @return le DTO de la réservation créée
     */
    public ResponseDto create(RequestDto requestDto) {
        ReservationRequestDto reservationRequest = (ReservationRequestDto) requestDto;
        Statut statut = statutRepository.findById(reservationRequest.getStatutId())
                .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
        Client client = clientRepository.findById(reservationRequest.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Evenement evenement = evenementRepository.findById(reservationRequest.getEvenementId())
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        Reservation reservation = toEntity(reservationRequest, statut, client, evenement);
        reservation.setDateCreation(LocalDateTime.now());
        return toDto(reservationRepository.save(reservation));
    }
}
