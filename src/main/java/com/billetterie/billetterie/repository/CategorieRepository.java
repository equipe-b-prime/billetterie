package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository de gestion des Categories.
 */
@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}
