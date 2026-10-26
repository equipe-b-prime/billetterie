package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.VuePlaceLibre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VuePlaceLibreRepository extends JpaRepository<VuePlaceLibre,Long> {
    List<VuePlaceLibre> findByEvenementId(Long evenementId);
}
