package com.billetterie.billetterie.exception;

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
 * Centralise le format des réponses d'erreur.
 */
@RestControllerAdvice
public class GlobalException {

    /**
     * Gère les erreurs de validation des requêtes (@Valid).
     *
     * Code HTTP : 400 BAD_REQUEST
     *
     * @param ex L'exception de validation.
     * @return ResponseEntity avec la liste des erreurs de validation par champ.
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
     * Gère les erreurs de ressource introuvable en BDD.
     *
     * Code HTTP : 404 NOT_FOUND
     *
     * @param ex L'exception de ressource introuvable.
     * @return ResponseEntity avec le message d'erreur.
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
     * Gère les erreurs de type de paramètre dans la requête (ex. transmettre une chaîne au lieu d'un entier).
     *
     * Code HTTP : 400 BAD_REQUEST
     *
     * @param ex L'exception de non-correspondance de type.
     * @return ResponseEntity expliquant le type attendu.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<List<String>>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String typeBrut = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "";
        String typeAtendu;

        switch (typeBrut) {
            case "int", "Long", "Integer" -> typeAtendu = "entier";
            case "boolean", "Boolean" -> typeAtendu = "booléen";
            case "String" -> typeAtendu = "chaîne de caractères";
            case "LocalDateTime" -> typeAtendu = "date et heure au format 'dd-MM-yyyy HH:mm:ss'";
            default -> typeAtendu = typeBrut.isEmpty() ? "valide" : typeBrut;
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
     * Gère les doublons de ressource (ex. unicité titre/horaire ou email).
     *
     * Code HTTP : 409 CONFLICT
     *
     * @param ex L'exception de doublon.
     * @return ResponseEntity indiquant le conflit.
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
     * Gère les tentatives de suppression ou modification d'une ressource référencée ailleurs (FK).
     *
     * Code HTTP : 409 CONFLICT
     *
     * @param ex L'exception de ressource liée.
     * @return ResponseEntity indiquant l'impossibilité d'effectuer l'opération.
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
     * Gère les arguments invalides transmis à une méthode métier.
     *
     * Code HTTP : 400 BAD_REQUEST
     *
     * @param ex L'exception d'argument invalide.
     * @return ResponseEntity avec le détail de l'erreur.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<String>> illegalArgumentException(IllegalArgumentException ex) {
        Response<String> reponse = new Response<>(Collections.emptyList());
        reponse.setNombre(0L);
        reponse.setHasError(true);
        reponse.setStatus(new Status(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reponse);
    }

    /**
     * Gère les erreurs d'état d'exécution invalide (ex. transition de statut d'une réservation impossible).
     *
     * Code HTTP : 409 CONFLICT
     *
     * @param ex L'exception d'état invalide.
     * @return ResponseEntity avec le message d'erreur.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response<String>> handleIllegalStateException(IllegalStateException ex) {
        Response<String> reponse = new Response<>(Collections.emptyList());
        reponse.setNombre(0L);
        reponse.setHasError(true);
        reponse.setStatus(new Status(HttpStatus.CONFLICT.value(), ex.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(reponse);
    }
}