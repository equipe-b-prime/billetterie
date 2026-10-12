package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.RequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.dto.SalleRequestDto;
import com.billetterie.billetterie.dto.SalleResponseDto;
import com.billetterie.billetterie.entity.Salle;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.SalleRepository;
import org.springframework.stereotype.Service;

/**
 * Service métier gérant les salles.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class SalleService extends AbstractCrudService {
    private final SalleRepository salleRepository;

    /**
     * Constructeur injectant le repository des salles.
     *
     * @param salleRepository le repository des salles
     */
    public SalleService(SalleRepository salleRepository) {
        super(salleRepository);
        this.salleRepository = salleRepository;
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
        return SalleResponseDto.builder()
                .id(salle.getId())
                .nom(salle.getNom())
                .ville(salle.getVille())
                .capacite(salle.getCapacite())
                .adresse(salle.getAdresse())
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
        salle.setNom(salleRequest.getNom());
        salle.setVille(salleRequest.getVille());
        salle.setCapacite(salleRequest.getCapacite());
        salle.setAdresse(salleRequest.getAdresse());
        salleRepository.save(salle);
        return toDto(salle);
    }
}
