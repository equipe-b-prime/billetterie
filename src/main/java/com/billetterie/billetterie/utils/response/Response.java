package com.billetterie.billetterie.utils.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Enveloppe standard des réponses HTTP de l'application.
 *
 * @param <T> le type des données retournées
 */
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"status", "nombre", "totalElements", "totalPages", "hasError", "data"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private Status status;
    private boolean hasError;
    private List<T> data;
    private Long nombre;
    private Long totalElements;
    private Integer totalPages;

    /**
     * Constructeur pour une liste de résultats.
     *
     * @param data la liste des données retournées
     */
     public Response(List<T> data) {
            this.status = new Status(800, "Opération effectuée avec succès");
        this.hasError = false;
        this.nombre = (long) data.size();
        this.data = data;
    }

    /**
     * Constructeur pour un résultat unique.
     *
     * @param data l'unique donnée retournée
     */
    public Response(T data) {
        this.data = List.of(data);
        this.status = new Status(800, "Opération effectuée avec succès");
        this.hasError = false;
        this.nombre = 1L;
    }

    public Response(Page<T> page) {
        this.status = new Status(800, "Opération effectuée avec succès");
        this.hasError = false;
        this.data = page.getContent();
        this.nombre = (long) page.getContent().size();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
