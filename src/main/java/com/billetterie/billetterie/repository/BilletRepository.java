package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Billet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BilletRepository extends JpaRepository<Billet, Long> {
    boolean existsByPlaceId(long salleId);
}
