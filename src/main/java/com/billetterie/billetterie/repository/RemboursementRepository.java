package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Remboursement;
import com.billetterie.billetterie.entity.TarifEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {




}
