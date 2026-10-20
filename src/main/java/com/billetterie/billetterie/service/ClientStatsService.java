package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.ClientStatsResponseDto;
import com.billetterie.billetterie.entity.ClientStatsView;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.ClientRepository;
import org.springframework.stereotype.Service;

/**
 * Service métier des statistiques agrégées de réservation par client.
 * Il est le seul à connaître la conversion de la vue v_client_stats.
 * La lecture de la vue est portée par {@link ClientRepository}, exactement comme
 * la lecture de v_evenement_details est portée par EvenementRepository.
 */
@Service
public class ClientStatsService {

    private final ClientRepository clientRepository;

    /**
     * Constructeur injectant le repository des clients, qui porte la lecture de la vue.
     *
     * @param clientRepository le repository des clients
     */
    public ClientStatsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Convertit une ligne de la vue en objet de réponse.
     *
     * @param stats la ligne agrégée issue de la vue
     * @return ClientStatsResponseDto les statistiques du client
     */
    public ClientStatsResponseDto toDto(ClientStatsView stats) {
        return ClientStatsResponseDto.builder()
                .clientId(stats.getClientId())
                .nbReservations(stats.getNbReservations())
                .nbValidees(stats.getNbValidees())
                .nbAnnulees(stats.getNbAnnulees())
                .nbRemboursees(stats.getNbRemboursees())
                .caBrut(stats.getCaBrut())
                .caHorsAnnulees(stats.getCaHorsAnnulees())
                .totalRembourse(stats.getTotalRembourse())
                .net(stats.getNet())
                .nbEvenements(stats.getNbEvenements())
                .premiereReservation(stats.getPremiereReservation())
                .derniereReservation(stats.getDerniereReservation())
                .build();
    }

    /**
     * Récupère les statistiques de réservation d'un client.
     * Un client connu sans aucune réservation renvoie des compteurs à zéro ;
     * un client inexistant n'apparaît pas dans la vue et provoque une erreur 404.
     *
     * @param clientId l'identifiant du client
     * @return ClientStatsResponseDto les statistiques agrégées du client
     * @throws NotFoundException si aucun client ne porte cet identifiant
     */
    public ClientStatsResponseDto getClientStats(long clientId) {
        ClientStatsView stats = clientRepository.findStatsByClientId(clientId)
                .orElseThrow(() -> new NotFoundException(CrudService.NOT_FOUND_MESSAGE));
        return toDto(stats);
    }
}
