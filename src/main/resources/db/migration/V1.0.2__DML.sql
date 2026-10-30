-- =========================================================================
-- V1.0.2__DML.sql : INITIALISATION DES DONNÉES DE RÉFÉRENCE ET JEU DE TEST
-- =========================================================================

-- =========================================================================
-- 1. NETTOYAGE DES TABLES
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
-- 2. DONNÉES DE PARAMÉTRAGE / RÉFÉRENTIELS (PARAMÈTRES FIXES)
-- =========================================================================

INSERT INTO statut (id, libelle) VALUES
    (1, 'PROVISOIRE'),
    (2, 'PAYEE'),
    (3, 'ANNULEE'),
    (4, 'REMBOURSE'),
    (5, 'EN_COURS'),
    (6, 'VALIDE')
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
-- 3. GÉNÉRATION DU JEU DE DONNÉES (500 LIGNES PAR TABLE)
-- =========================================================================

-- Salles (500)
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

-- Événements (500 : programmés dans le futur en 2027 pour permettre les tests de réservation)
INSERT INTO evenement (id, titre, description, horaire, placement_libre, salle_id)
SELECT
    n AS id,
    'Événement_' || n,
    'Description du spectacle n°' || n,
    TIMESTAMP '2027-06-01 18:00:00' + (n || ' hours')::INTERVAL,
    (n % 2 = 0),
    ((n - 1) % 500) + 1
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- Places (2000 : 4 places par salle — 1 de chaque catégorie PLATINE, OR, ARGENT, STANDARD)
INSERT INTO place (id, rang, numero, salle_id, categorie_id)
SELECT
    ((s - 1) * 4 + c) AS id,
    c AS rang,
    s AS numero,
    s AS salle_id,
    c AS categorie_id
FROM generate_series(1, 500) AS s
CROSS JOIN generate_series(1, 4) AS c
ON CONFLICT (id) DO NOTHING;

-- Clients (500)
INSERT INTO client (id, nom, prenom, telephone, email)
SELECT
    n AS id,
    'Nom_' || n,
    'Prenom_' || n,
    '07' || LPAD(n::TEXT, 8, '0'),
    'user' || n || '@mail.com'
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- Tarifs Événements (500)
INSERT INTO tarif_evenement (id, prix, evenement_id)
SELECT
    n AS id,
    50.00 + (n % 200),
    ((n - 1) % 500) + 1
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- Réservations (500 : date_creation récente avec NOW() pour éviter l'annulation immédiate du @Scheduled)
INSERT INTO reservation (
    id, date_creation, date_paiement, date_annulation,
    client_id, montant_total, montant_rembourse,
    evenement_id, statut_id, place_id, est_rembourse
)
SELECT
    n AS id,
    NOW() - ((500 - n) || ' seconds')::INTERVAL,
    CASE WHEN (n % 4) = 1 THEN NOW() - ((400 - (n % 300)) || ' seconds')::INTERVAL ELSE NULL END,
    CASE WHEN (n % 4) IN (2, 3) THEN NOW() - ((100 - (n % 50)) || ' seconds')::INTERVAL ELSE NULL END,
    ((n - 1) % 500) + 1,
    100.00 + (n % 300),
    CASE WHEN (n % 4) = 3 THEN 100.00 + (n % 300) ELSE NULL END,
    ((n - 1) % 500) + 1,
    (n % 4) + 1,
    ((n - 1) * 4) + 1,
    (n % 4 = 3)
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- Paiements (uniquement pour les réservations PAYEE, ANNULEE ou REMBOURSE : pas sur les PROVISOIRE afin de permettre leur suppression F4)
INSERT INTO paiement (id, montant, date_paiement, mode_paiement, reservation_id)
SELECT
    n AS id,
    100.00 + (n % 300),
    NOW() - ((400 - (n % 300)) || ' seconds')::INTERVAL,
    CASE n % 3
        WHEN 0 THEN 'Carte'
        WHEN 1 THEN 'Mobile Money'
        ELSE 'Espèces'
    END,
    n
FROM generate_series(1, 500) AS n
WHERE (n % 4) <> 0
ON CONFLICT (id) DO NOTHING;

-- Remboursements (sur les réservations remboursées)
INSERT INTO remboursement (id, montant, date_remboursement, mode_remboursement, motif, reservation_id)
SELECT
    n AS id,
    100.00 + (n % 300),
    NOW() - ((100 - (n % 50)) || ' seconds')::INTERVAL,
    'Virement',
    'Annulation événement',
    n
FROM generate_series(1, 500) AS n
WHERE (n % 4) = 3
ON CONFLICT (id) DO NOTHING;

-- Billets (1 billet par réservation correspondant à sa place dans la salle de l'événement)
INSERT INTO billet (id, reservation_id, place_id, prix_applique, code_billet)
SELECT
    n AS id,
    n AS reservation_id,
    ((n - 1) * 4) + 1 AS place_id,
    50.00 + (n % 200) AS prix_applique,
    'TKT-' || LPAD(n::TEXT, 8, '0') || '-A1' AS code_billet
FROM generate_series(1, 500) AS n
ON CONFLICT (id) DO NOTHING;

-- =========================================================================
-- 4. RECALCUL ET RÉALIGNEMENT DES SÉQUENCES POSTGRESQL
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
