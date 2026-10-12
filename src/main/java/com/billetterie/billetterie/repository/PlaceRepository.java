package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository de gestion des Places.
 */
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
}
