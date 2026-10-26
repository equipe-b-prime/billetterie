package com.billetterie.billetterie.dto;

import com.billetterie.billetterie.entity.Evenement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VuePlaceLibreResponseDto {
    private String id;
    private String categorie;
    private Long placesDisponibles;
}
