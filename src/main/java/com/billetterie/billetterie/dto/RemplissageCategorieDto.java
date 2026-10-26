package com.billetterie.billetterie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemplissageCategorieDto {

    private String nomCategorie;
    private int totalPlaces;
    private int placesProvisoires;
    private int placesPayees;
    private double pourcentageRemplissage;
}