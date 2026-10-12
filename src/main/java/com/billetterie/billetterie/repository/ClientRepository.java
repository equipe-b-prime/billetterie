package com.billetterie.billetterie.repository;

import com.billetterie.billetterie.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository de gestion des Clients.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
