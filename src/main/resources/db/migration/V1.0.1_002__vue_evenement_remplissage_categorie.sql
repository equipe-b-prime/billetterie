CREATE OR REPLACE VIEW v_evenement_remplissage_categorie AS
SELECT
    CONCAT(e.id, '_', c.id) AS id, -- Clé unique pour JPA
    e.id AS evenement_id,
    c.libelle AS nom_categorie,
    COUNT(DISTINCT p.id) AS total_places,
    COUNT(DISTINCT CASE WHEN UPPER(s.libelle) = 'PROVISOIRE' THEN r.id END) AS places_provisoires,
    COUNT(DISTINCT CASE WHEN UPPER(s.libelle) = 'PAYEE' THEN r.id END) AS places_payees
FROM evenement e
         JOIN salle sal ON e.salle_id = sal.id
         JOIN place p ON p.salle_id = sal.id
         JOIN categorie c ON p.categorie_id = c.id
         LEFT JOIN reservation r ON r.place_id = p.id AND r.evenement_id = e.id
         LEFT JOIN statut s ON r.statut_id = s.id
GROUP BY e.id, c.id, c.libelle;