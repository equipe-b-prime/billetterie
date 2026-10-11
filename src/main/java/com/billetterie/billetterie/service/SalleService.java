package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.RequestDto;
import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.dto.SalleRequestDto;
import com.billetterie.billetterie.dto.SalleResponseDto;
import com.billetterie.billetterie.entity.Salle;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Représente la salle de evenement
 */
@Service
public class SalleService extends AbstractCrudService {
    private final SalleRepository salleRepository;
    public SalleService(SalleRepository salleRepository) {
        super(salleRepository);
        this.salleRepository = salleRepository;
    }
    public Salle toEntity(SalleRequestDto salleRequest) {
        return Salle.builder()
                .capacite(salleRequest.getCapacite())
                .nom(salleRequest.getNom())
                .ville(salleRequest.getVille())
                .adresse(salleRequest.getAdresse())
                .build();
    }
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
    @Override
    public ResponseDto create(RequestDto requestDto) {
        SalleRequestDto salleRequest = (SalleRequestDto)  requestDto;
        Salle salle = salleRepository.save(toEntity(salleRequest));
        return toDto(salle);
    }
    @Override
    public ResponseDto update(Long id, RequestDto requestDto) {
        SalleRequestDto salleRequest = (SalleRequestDto)  requestDto;
        Salle salle = salleRepository.findById(id).orElseThrow(() -> new NotFoundException("Salle non trouvée"));
            salle.setNom(salleRequest.getNom());
            salle.setVille(salleRequest.getVille());
            salle.setCapacite(salleRequest.getCapacite());
            salle.setAdresse(salleRequest.getAdresse());
            salleRepository.save(salle);
            return toDto(salle);
    }


}
