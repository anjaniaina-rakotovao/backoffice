-- reset_tables_data.sql
-- Reinitialise les donnees (sans supprimer les tables) pour PostgreSQL.
-- - Vide toutes les lignes
-- - Remet les sequences SERIAL a 1
-- - Respecte les dependances via CASCADE
BEGIN;

TRUNCATE TABLE assignation,
reservation,
distance,
lieu,
vehicule,
"type",
token RESTART IDENTITY CASCADE;

COMMIT;

-- Verification rapide (optionnel)
-- SELECT 'assignation' AS table_name, COUNT(*) FROM assignation
-- UNION ALL SELECT 'reservation', COUNT(*) FROM reservation
-- UNION ALL SELECT 'distance', COUNT(*) FROM distance
-- UNION ALL SELECT 'lieu', COUNT(*) FROM lieu
-- UNION ALL SELECT 'vehicule', COUNT(*) FROM vehicule
-- UNION ALL SELECT 'type', COUNT(*) FROM "type"
-- UNION ALL SELECT 'token', COUNT(*) FROM token;
