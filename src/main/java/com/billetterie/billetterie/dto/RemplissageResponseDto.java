package com.billetterie.billetterie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemplissageResponseDto {

    private Long idEvenement;
    private int totalPlaces;
    private int placesProvisoires;
    private int placesPayees;
    private double pourcentageRemplissage;
    private List<RemplissageCategorieDto> detailsParCategorie;
}