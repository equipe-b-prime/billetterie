-- =========================================================================
-- 1. DONNÉES DE PARAMÉTRAGE / RÉFÉRENTIELS (PARAMETRES FIXES)
-- =========================================================================

INSERT INTO statut (id, libelle) VALUES
                                     (1, 'EN_COURS'),
                                     (2, 'VALIDE'),
                                     (3, 'ANNULE'),
                                     (4, 'REMBOURSE'),
                                     (5, 'PROVISOIRE'),
                                     (6, 'PAYEE')
    ON CONFLICT (id) DO UPDATE SET libelle = EXCLUDED.libelle;

INSERT INTO categorie (id, libelle, description) VALUES
                                                     (1, 'PLATINE',  'Placement premium, meilleure vue'),
                                                     (2, 'OR',       'Placement privilégié'),
                                                     (3, 'ARGENT',   'Placement intermédiaire'),
                                                     (4, 'STANDARD', 'Placement standard')
    ON CONFLICT (id) DO UPDATE SET
    libelle = EXCLUDED.libelle,
                            description = EXCLUDED.description;

-- =========================================================================
-- 2. GÉNÉRATION DU JEU DE DONNÉES (500 LIGNES PAR TABLE)
-- =========================================================================

-- Salles
INSERT INTO salle (id, nom, ville, capacite, adresse)
SELECT
    n AS id,
    'Salle_' || n,
    CASE n % 4
    WHEN 0 THEN 'Paris'
        WHEN 1 THEN 'Lyon'
        WHEN 2 THEN 'Marseille'
        ELSE 'Lille'
END,
    100 + (n % 900),
    'Adresse_' || n
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- Événements
INSERT INTO evenement (id, titre, description, horaire, placement_libre, salle_id)
SELECT
    n AS id,
    'Événement_' || n,
    'Description du spectacle n°' || n,
    TIMESTAMP '2026-01-01 10:00:00' + (n || ' minutes')::INTERVAL,
    (n % 2 = 0),
    ((n - 1) % 500) + 1
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- Places
INSERT INTO place (id, rang, numero, salle_id, categorie_id)
SELECT
    n AS id,
    (n % 20) + 1,
    (n % 50) + 1,
    ((n - 1) % 500) + 1,
    (n % 4) + 1
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- Clients
INSERT INTO client (id, nom, prenom, telephone, email)
SELECT
    n AS id,
    'Nom_' || n,
    'Prenom_' || n,
    '07' || LPAD(n::text, 8, '0'),
    'user' || n || '@mail.com'
FROM generate_series(1, 500) AS n
    ON CONFLICT (id) DO NOTHING;

-- Tarifs Événements
INSERT INTO tarif_evenement (id, prix, evenement_id)
SELECT
    n AS id,
    50.00 + (n % 200),
    ((n - 1) % 500) + 1
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- Réservations
INSERT INTO reservation (
    id, date_creation, date_paiement, date_annulation,
    client_id, montant_total, montant_rembourse,
    evenement_id, statut_id, place_id, est_rembourse
)
SELECT
    n AS id,
    TIMESTAMP '2026-01-01 08:00:00' + (n || ' seconds')::INTERVAL,
    CASE WHEN (n % 4) IN (1, 2) THEN TIMESTAMP '2026-01-01 08:05:00' + (n || ' seconds')::INTERVAL ELSE NULL END,
    CASE WHEN (n % 4) = 3 THEN TIMESTAMP '2026-01-01 09:00:00' + (n || ' seconds')::INTERVAL ELSE NULL END,
    ((n - 1) % 500) + 1,
    100.00 + (n % 300),
    CASE WHEN (n % 4) = 3 THEN 100.00 + (n % 300) ELSE NULL END,
    ((n - 1) % 500) + 1,
    (n % 4) + 1,
    ((n - 1) % 500) + 1,
    (n % 4 = 3)
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- Paiements
INSERT INTO paiement (id, montant, date_paiement, mode_paiement, reservation_id)
SELECT
    n AS id,
    100.00 + (n % 300),
    TIMESTAMP '2026-01-01 08:05:00' + (n || ' seconds')::INTERVAL,
    CASE n % 3
    WHEN 0 THEN 'Carte'
        WHEN 1 THEN 'Mobile Money'
        ELSE 'Espèces'
END,
    ((n - 1) % 500) + 1
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- Remboursements
INSERT INTO remboursement (id, montant, date_remboursement, mode_remboursement, motif, reservation_id)
SELECT
    n AS id,
    100.00 + (n % 300),
    TIMESTAMP '2026-01-01 09:30:00' + (n || ' seconds')::INTERVAL,
    'Virement',
    'Annulation événement',
    n
FROM generate_series(1, 100) AS n
    ON CONFLICT (id) DO NOTHING;

-- Billets
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
    (34, 19, 29, 15000.00, 'TKT-00000034-VVIP2')
    ON CONFLICT (id) DO NOTHING;

-- =========================================================================
-- 3. RECALCUL ET REAIGNEMENT DES SÉQUENCES POSTGRESQL
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
SELECT setval(pg_get_serial_sequence('remboursement', 'id'), COALESCE((SELECT MAX(id) FROM remboursement), 1));
SELECT setval(pg_get_serial_sequence('billet', 'id'), COALESCE((SELECT MAX(id) FROM billet), 1));

ANALYZE statut, categorie, salle, evenement, place, client, tarif_evenement, reservation, paiement, remboursement, billet;