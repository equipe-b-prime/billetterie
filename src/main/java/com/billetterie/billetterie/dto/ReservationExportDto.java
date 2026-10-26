package com.billetterie.billetterie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class    ReservationExportDto {
    private int rang;
    private int numero;
    private String nomCategorie;
    private String nomClient;
    private String emailClient;
    private String statut;
    private LocalDateTime dateCreation;
}