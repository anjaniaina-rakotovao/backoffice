-- test_final.sql
-- Jeu de donnees de test final (1 seule journee)
-- A executer APRES reset_tables_data.sql
-- Objectif: tester split, priorite petites reservations, et disponibilite vehicule

BEGIN;

-- 0) Garde-fous schema (au cas ou les migrations n'ont pas encore ete appliquees)
ALTER TABLE IF EXISTS vehicule
ADD COLUMN IF NOT EXISTS date_disponibilite TIMESTAMP DEFAULT '1900-01-01 00:00:00';

ALTER TABLE IF EXISTS assignation
ADD COLUMN IF NOT EXISTS nbr_passager_assigne INT;

UPDATE assignation a
SET nbr_passager_assigne = r.nbr_passager
FROM reservation r
WHERE a.id_reservation = r.id
  AND (a.nbr_passager_assigne IS NULL OR a.nbr_passager_assigne <= 0);

ALTER TABLE IF EXISTS assignation
ALTER COLUMN nbr_passager_assigne SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'assignation_nbr_passager_assigne_check'
    ) THEN
        ALTER TABLE assignation
        ADD CONSTRAINT assignation_nbr_passager_assigne_check
        CHECK (nbr_passager_assigne > 0);
    END IF;
END $$;

DROP INDEX IF EXISTS assignation_id_reservation_key;
ALTER TABLE IF EXISTS assignation
DROP CONSTRAINT IF EXISTS assignation_id_reservation_key;

-- 1) LIEUX: 1 aeroport + 2 hotels
INSERT INTO lieu (libelle, code) VALUES
('Aeroport International', 'AER'),
('Hotel Alpha', 'H01'),
('Hotel Beta', 'H02');

-- 2) DISTANCES (aller + retour pour rester explicite)
INSERT INTO distance (id_from, id_to, distance_km) VALUES
(1, 2, 12.5),
(2, 1, 12.5),
(1, 3, 18.0),
(3, 1, 18.0),
(2, 3, 7.0),
(3, 2, 7.0);

-- 3) VEHICULES (max 5, ici 4)
-- type autorise par schema: h, d, el, e
INSERT INTO vehicule (reference, nbr_place, type, date_disponibilite) VALUES
('VAN-10', 10, 'd', '1900-01-01 00:00:00'),
('MINI-05', 5, 'e', '1900-01-01 00:00:00'),
('CITY-04', 4, 'd', '1900-01-01 00:00:00'),
('MICRO-02', 2, 'e', '1900-01-01 00:00:00');

-- 4) RESERVATIONS (1 seule journee: 2026-03-20)
-- Scenario cle:
--   Intervalle 08:00-08:30 -> reservation de 8 (VAN-10 prend 8, reste 2 places)
--   Intervalle 08:30-09:00 -> reservations 3 et 1
--   Attendu: la reservation 1 passe avant de fragmenter la reservation 3 sur la derniere place
INSERT INTO reservation (id_client, nbr_passager, date_heure_arrivee, id_hotel_arrivee) VALUES
('C801', 8, '2026-03-20 08:10:00', 2),
('C302', 3, '2026-03-20 08:40:00', 3),
('C103', 1, '2026-03-20 08:42:00', 2),
('C604', 6, '2026-03-20 10:00:00', 3);

-- 5) TOKEN minimal (si vos ecrans/API l'utilisent)
INSERT INTO token (token, date_expiration)
VALUES ('00000000-0000-0000-0000-000000000001', '2026-12-31 23:59:59');

COMMIT;

-- Verification rapide du jeu de donnees charge
SELECT 'lieu' AS table_name, COUNT(*) AS total FROM lieu
UNION ALL
SELECT 'distance', COUNT(*) FROM distance
UNION ALL
SELECT 'vehicule', COUNT(*) FROM vehicule
UNION ALL
SELECT 'reservation', COUNT(*) FROM reservation
UNION ALL
SELECT 'assignation', COUNT(*) FROM assignation
UNION ALL
SELECT 'token', COUNT(*) FROM token;
