-- =========================================================================
-- 1. EXTENSION DES TYPES & RECRÉATION DE LA VUE
-- =========================================================================

DROP VIEW IF EXISTS v_evenement_details CASCADE;

ALTER TABLE client ALTER COLUMN nom TYPE VARCHAR(50);
ALTER TABLE client ALTER COLUMN prenom TYPE VARCHAR(50);
ALTER TABLE client ALTER COLUMN telephone TYPE VARCHAR(20);

ALTER TABLE salle ALTER COLUMN nom TYPE VARCHAR(100);
ALTER TABLE salle ALTER COLUMN adresse TYPE VARCHAR(255);
ALTER TABLE salle ALTER COLUMN ville TYPE VARCHAR(50);

ALTER TABLE evenement ALTER COLUMN titre TYPE VARCHAR(150);

CREATE VIEW v_evenement_details AS
SELECT
    e.id,
    e.titre,
    e.description,
    e.horaire,
    e.placement_libre,
    s.id AS salle_id,
    s.nom AS salle_nom,
    s.ville AS salle_ville,
    s.capacite AS salle_capacite,
    s.adresse AS salle_adresse
FROM evenement e
         JOIN salle s ON e.salle_id = s.id;

-- =========================================================================
-- 2. NETTOYAGE DES TABLES
-- =========================================================================

TRUNCATE TABLE
    billet,
    remboursement,
    paiement,
    reservation,
    tarif_evenement,
    place,
    client,
    evenement,
    salle,
    categorie,
    statut
RESTART IDENTITY CASCADE;

-- =========================================================================
-- 3. STATUTS & CATÉGORIES (REFERENTIELS)
-- =========================================================================

INSERT INTO statut (id, libelle) VALUES
                                     (1, 'EN_COURS'),
                                     (2, 'VALIDE'),
                                     (3, 'ANNULE'),
                                     (4, 'REMBOURSE');

INSERT INTO categorie (id, libelle, description) VALUES
                                                     (1, 'PLATINE',  'Placement premium, meilleure vue'),
                                                     (2, 'OR',       'Placement privilégié'),
                                                     (3, 'ARGENT',   'Placement intermédiaire'),
                                                     (4, 'STANDARD', 'Placement standard');

-- =========================================================================
-- 4. GÉNÉRATION DES DONNÉES (500 LIGNES PER TABLE)
-- =========================================================================

-- Salles (500)
INSERT INTO salle (nom, ville, capacite, adresse)
SELECT
    'Salle_' || n,
    CASE n % 4
    WHEN 0 THEN 'Paris'
        WHEN 1 THEN 'Lyon'
        WHEN 2 THEN 'Marseille'
        ELSE 'Lille'
END,
    100 + (n % 900),
    'Adresse_' || n
FROM generate_series(1, 500) AS n;

-- Événements (500)
INSERT INTO evenement (titre, description, horaire, placement_libre, salle_id)
SELECT
    'Événement_' || n,
    'Description du spectacle n°' || n,
    TIMESTAMP '2026-01-01 10:00:00' + (n || ' minutes')::INTERVAL,
    (n % 2 = 0),
    ((n - 1) % 500) + 1
FROM generate_series(1, 500) AS n;

-- Places (500)
INSERT INTO place (rang, numero, salle_id, categorie_id)
SELECT
    (n % 20) + 1,
    (n % 50) + 1,
    ((n - 1) % 500) + 1,
    (n % 4) + 1
FROM generate_series(1, 500) AS n;

-- Clients (500)
INSERT INTO client (nom, prenom, telephone)
SELECT
    'Nom_' || n,
    'Prenom_' || n,
    '07' || LPAD(n::text, 8, '0')
FROM generate_series(1, 500) AS n;

-- Tarifs Événements (500)
INSERT INTO tarif_evenement (prix, evenement_id)
SELECT
    50.00 + (n % 200),
    ((n - 1) % 500) + 1
FROM generate_series(1, 500) AS n;

-- Réservations (500)
INSERT INTO reservation (
    date_creation, date_paiement, date_annulation,
    client_id, montant_total, montant_rembourse,
    evenement_id, statut_id
)
SELECT
    TIMESTAMP '2026-01-01 08:00:00' + (n || ' seconds')::INTERVAL,
    CASE WHEN (n % 4) IN (1, 2) THEN TIMESTAMP '2026-01-01 08:05:00' + (n || ' seconds')::INTERVAL ELSE NULL END,
    CASE WHEN (n % 4) = 3 THEN TIMESTAMP '2026-01-01 09:00:00' + (n || ' seconds')::INTERVAL ELSE NULL END,
    ((n - 1) % 500) + 1,
    100.00 + (n % 300),
    CASE WHEN (n % 4) = 3 THEN 100.00 + (n % 300) ELSE NULL END,
    ((n - 1) % 500) + 1,
    (n % 4) + 1
FROM generate_series(1, 500) AS n;

-- Paiements (500)
INSERT INTO paiement (montant, date_paiement, mode_paiement, reservation_id)
SELECT
    100.00 + (n % 300),
    TIMESTAMP '2026-01-01 08:05:00' + (n || ' seconds')::INTERVAL,
    CASE n % 3
    WHEN 0 THEN 'Carte'
        WHEN 1 THEN 'Mobile Money'
        ELSE 'Espèces'
END,
    ((n - 1) % 500) + 1
FROM generate_series(1, 500) AS n;

-- Billets spécifiques (20)
INSERT INTO billet (id, reservation_id, place_id, prix_applique, code_billet)
VALUES
    (15, 10, 10, 5000.00, 'TKT-00000015-A1'),
    (16, 10, 11, 5000.00, 'TKT-00000016-A2'),
    (17, 11, 12, 7500.00, 'TKT-00000017-B1'),
    (18, 11, 13, 7500.00, 'TKT-00000018-B2'),
    (19, 12, 14, 10000.00, 'TKT-00000019-C1'),
    (20, 12, 15, 10000.00, 'TKT-00000020-C2'),
    (21, 13, 16, 5000.00, 'TKT-00000021-A3'),
    (22, 13, 17, 5000.00, 'TKT-00000022-A4'),
    (23, 14, 18, 12000.00, 'TKT-00000023-VIP1'),
    (24, 14, 19, 12000.00, 'TKT-00000024-VIP2'),
    (25, 15, 20, 5000.00, 'TKT-00000025-A5'),
    (26, 15, 21, 5000.00, 'TKT-00000026-A6'),
    (27, 16, 22, 7500.00, 'TKT-00000027-B3'),
    (28, 16, 23, 7500.00, 'TKT-00000028-B4'),
    (29, 17, 24, 10000.00, 'TKT-00000029-C3'),
    (30, 17, 25, 10000.00, 'TKT-00000030-C4'),
    (31, 18, 26, 5000.00, 'TKT-00000031-A7'),
    (32, 18, 27, 5000.00, 'TKT-00000032-A8'),
    (33, 19, 28, 15000.00, 'TKT-00000033-VVIP1'),
    (34, 19, 29, 15000.00, 'TKT-00000034-VVIP2');

-- =========================================================================
-- 5. RECALCUL DES SÉQUENCES D'IDENTIFIANTS (SGBD)
-- =========================================================================

SELECT setval(pg_get_serial_sequence('statut', 'id'), COALESCE((SELECT MAX(id) FROM statut), 1));
SELECT setval(pg_get_serial_sequence('categorie', 'id'), COALESCE((SELECT MAX(id) FROM categorie), 1));
SELECT setval(pg_get_serial_sequence('salle', 'id'), COALESCE((SELECT MAX(id) FROM salle), 1));
SELECT setval(pg_get_serial_sequence('evenement', 'id'), COALESCE((SELECT MAX(id) FROM evenement), 1));
SELECT setval(pg_get_serial_sequence('place', 'id'), COALESCE((SELECT MAX(id) FROM place), 1));
SELECT setval(pg_get_serial_sequence('client', 'id'), COALESCE((SELECT MAX(id) FROM client), 1));
SELECT setval(pg_get_serial_sequence('tarif_evenement', 'id'), COALESCE((SELECT MAX(id) FROM tarif_evenement), 1));
SELECT setval(pg_get_serial_sequence('reservation', 'id'), COALESCE((SELECT MAX(id) FROM reservation), 1));
SELECT setval(pg_get_serial_sequence('paiement', 'id'), COALESCE((SELECT MAX(id) FROM paiement), 1));
SELECT setval(pg_get_serial_sequence('billet', 'id'), COALESCE((SELECT MAX(id) FROM billet), 1));

ANALYZE salle, evenement, place, client, tarif_evenement, reservation, paiement, billet;