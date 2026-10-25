package com.billetterie.billetterie.service;

import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Service abstrait de base (classe mère) pour toutes les entités de l'application.
 * Il implémente {@link CrudService} et centralise la logique métier répétitive
 * de lecture et de suppression en utilisant un {@link JpaRepository} global (type brut).
 * Les classes enfants (ex: CategorieService, SalleService) héritent de ces méthodes
 * et se concentrent uniquement sur la création, la modification et le mapping (DTO/Entité).
 */
public abstract class AbstractCrudService implements CrudService {


    /**
     * Le repository global utilisé pour les opérations génériques (find, delete).
     * Il est injecté par la classe enfant via le mot-clé "super".
     */
    private final JpaRepository repository;

    /**
     * Constructeur protégé appelé par les services enfants lors de leur instanciation.
     *
     * @param repository le repository spécifique à l'entité (ex: SalleRepository)
     *                   traité ici de manière globale
     */
    public AbstractCrudService(JpaRepository repository) {
        this.repository = repository;
    }
    /**
     * Borne la taille de page demandée entre 1 et {@link #TAILLE_MAX}.
     * Une valeur inférieure à 1 retombe sur {@link #TAILLE_DEFAUT}.
     *
     * @param taille la taille de page transmise par le client
     * @return la taille de page autorisée
     */
    protected int tailleAutorisee(int taille) {
        if (taille < 1) {
            return TAILLE_DEFAUT;
        }
        return Math.min(taille, TAILLE_MAX);
    }

    /**
     * Normalise l'index de page en ramenant toute valeur négative à 0.
     *
     * @param page l'index de page transmis par le client
     * @return l'index de page autorisé
     */
    protected int pageAutorisee(int page) {
        return Math.max(page, 0);
    }

}