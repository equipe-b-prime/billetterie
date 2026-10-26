CREATE OR REPLACE VIEW vue_places_libres AS
SELECT
    (e.id || '-' || c.libelle) AS id,
    e.id AS evenement_id,
    c.libelle AS categorie,
    COUNT(p.id) AS places_disponibles
FROM evenement e
         JOIN salle s ON e.salle_id = s.id
         JOIN place p ON p.salle_id = s.id
         JOIN categorie c ON p.categorie_id = c.id
WHERE NOT EXISTS (
    SELECT 1
    FROM billet b
             JOIN reservation r ON b.reservation_id = r.id
             JOIN statut st ON r.statut_id = st.id
    WHERE b.place_id = p.id
      AND r.evenement_id = e.id
      AND st.libelle IN ('PROVISOIRE', 'PAYEE')
)
GROUP BY e.id, c.libelle;