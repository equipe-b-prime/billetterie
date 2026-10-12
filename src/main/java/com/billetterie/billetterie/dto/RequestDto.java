package com.billetterie.billetterie.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface marqueur pour les DTO de requête.
 * Permet la désérialisation polymorphe par déduction de sous-type.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(CategorieRequestDto.class),
        @JsonSubTypes.Type(PlaceRequestDto.class),
        @JsonSubTypes.Type(EvenementRequestDto.class),
        @JsonSubTypes.Type(SalleRequestDto.class),
        @JsonSubTypes.Type(ReservationRequestDto.class)
})
public interface RequestDto { }
