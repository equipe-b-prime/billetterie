package com.billetterie.billetterie.exception;

import com.billetterie.billetterie.entity.Reservation;
import com.billetterie.billetterie.utils.response.Response;
import com.billetterie.billetterie.utils.response.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

/**
 * Gestionnaire global des exceptions de l'application.
 * Centralise le format des réponses d'erreur (validation, ressource introuvable).
 */
@RestControllerAdvice
public class GlobalException {

    /**
     * Gère les erreurs de validation des requêtes (@Valid).
     *
     * @param ex L'exception de validation.
     * @return ResponseEntity<Response<Map<String,String>>> La liste des erreurs par champ.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<Map<String, String>> listeDerreurs = new ArrayList<>();
        for (FieldError error : ex.getFieldErrors()) {
            Map<String, String> detailErreur = new HashMap<>();
            detailErreur.put("champ", error.getField());
            detailErreur.put("message", error.getDefaultMessage());
            listeDerreurs.add(detailErreur);
        }
        Response<Map<String, String>> response = new Response<>();
        response.setData(listeDerreurs);
        response.setStatus(new Status(HttpStatus.BAD_REQUEST.value(), "Erreur de validation"));
        response.setHasError(true);
        response.setNombre((long) listeDerreurs.size());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les erreurs de ressource introuvable.
     *
     * @param ex L'exception de ressource introuvable.
     * @return ResponseEntity<Response> Le message d'erreur encapsulé.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response<String>> notFoundException(NotFoundException ex) {
        Response<String> reponse = new Response<>(Collections.emptyList());
        reponse.setNombre(0L);
        reponse.setHasError(true);
        reponse.setStatus(new Status(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(reponse);
    }

    /**
     * Gère les erreurs où paramètre envoyé dans une requête ne corresponde pas
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<List<String>>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String typeBrut = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "";
        String typeAtendu;

        switch (typeBrut) {
            case "int", "Long", "Integer" ->
                    typeAtendu = "entier";
            case "boolean", "Boolean" ->
                    typeAtendu = "booléen";
            case "String" ->
                    typeAtendu = "chaîne de caractères";
            case "LocalDateTime" ->
                    typeAtendu = "date et heure au format 'dd-MM-yyyy HH:mm:ss'";
            default ->
                    typeAtendu = typeBrut.isEmpty() ? "valide" : typeBrut;
        }

        Response<List<String>> response = new Response<>(Collections.emptyList());
        response.setData(null);
        response.setStatus(new Status(
                HttpStatus.BAD_REQUEST.value(),
                "Le parametre '" + ex.getName() + "' doit etre de type " + typeAtendu
        ));
        response.setHasError(true);
        response.setNombre(0L);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    /**
     * Gère les erreurs où on a un cas de doublon de ressource
     */
    @ExceptionHandler(DoublonRessourceException.class)
    public ResponseEntity<Response<String>> doublonRessourceException(DoublonRessourceException ex) {
        Response<String> reponse = new Response<>(Collections.emptyList());
        reponse.setNombre(0L);
        reponse.setHasError(true);
        reponse.setStatus(new Status(HttpStatus.CONFLICT.value(), "Doublon de ressource : " + ex.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(reponse);
    }
    /**
     * Gère les erreurs où on a un cas de ressource utilisée par une autre ressource
     */
    @ExceptionHandler(RessourceUtiliseeException.class)
    public ResponseEntity<Response<String>> ressourceUtiliseeException(RessourceUtiliseeException ex) {
        Response<String> reponse = new Response<>(Collections.emptyList());
        reponse.setNombre(0L);
        reponse.setHasError(true);
        reponse.setStatus(new Status(HttpStatus.CONFLICT.value(), ex.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(reponse);
    }

    /**
     * Gère les erreurs où on a un cas de ressource utilisée par une autre ressource
     * @param ex
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<String>> illegalArgumentException(IllegalArgumentException ex) {
        Response<String> reponse = new Response<>(Collections.emptyList());
        reponse.setNombre(0L);
        reponse.setHasError(true);
        reponse.setStatus(new Status(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reponse);
    }/**
     * Gère les erreurs où on a un cas de ressource utilisée par une autre ressource
     * @param ex
     * @return
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response<String>> IllegalStateException(IllegalStateException ex) {
        Response<String> reponse = new Response<>(Collections.emptyList());
        reponse.setNombre(0L);
        reponse.setHasError(true);
        reponse.setStatus(new Status(HttpStatus.CONFLICT.value(), ex.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT

        ).body(reponse);
    }
}
