-- Migration pour supporter le split de réservation.
-- 1) Ajouter le nombre de passagers assignés par ligne d'assignation
ALTER TABLE assignation
ADD COLUMN IF NOT EXISTS nbr_passager_assigne INT;

-- 2) Initialiser les lignes existantes avec le nombre total de passagers de la réservation
UPDATE assignation a
SET
    nbr_passager_assigne = r.nbr_passager
FROM
    reservation r
WHERE
    a.id_reservation = r.id
    AND (
        a.nbr_passager_assigne IS NULL
        OR a.nbr_passager_assigne <= 0
    );

-- 3) Appliquer les contraintes
ALTER TABLE assignation
ALTER COLUMN nbr_passager_assigne
SET
    NOT NULL;

ALTER TABLE assignation ADD CONSTRAINT assignation_nbr_passager_assigne_check CHECK (nbr_passager_assigne > 0);

-- 4) Supprimer la contrainte d'unicité pour autoriser plusieurs lignes
-- pour la même réservation (split)
DROP INDEX IF EXISTS assignation_id_reservation_key;

ALTER TABLE assignation
DROP CONSTRAINT IF EXISTS assignation_id_reservation_key;
