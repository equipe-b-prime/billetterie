package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.VueRemplissageCategorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VueRemplissageCategorieRepository extends JpaRepository<VueRemplissageCategorie, String> {

    // Spring Data déduit la requête tout seul : WHERE evenement_id = ?
    List<VueRemplissageCategorie> findByEvenementId(Long evenementId);
}