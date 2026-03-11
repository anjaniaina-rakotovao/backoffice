-- seed_assignations_2026_03_11.sql
-- Objectif:
-- 1) garantir 5 reservations pour le 2026-03-11
-- 2) forcer la repartition des assignations: 2 + 1 + 1 + 1 sur 4 vehicules

CREATE TABLE IF NOT EXISTS assignation (
  id SERIAL PRIMARY KEY,
  id_vehicule INT NOT NULL REFERENCES vehicule(id) ON DELETE CASCADE,
  id_reservation INT NOT NULL REFERENCES reservation(id) ON DELETE CASCADE,
  date_assignation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  date_depart TIMESTAMP,
  UNIQUE (id_reservation)
);

-- psql variables
\set target_date '2026-03-11'
\set base_time '2026-03-11 09:00:00'

-- Securite: il faut au moins 4 vehicules pour obtenir 2+1+1+1
DO $$
BEGIN
  IF (SELECT COUNT(*) FROM vehicule) < 4 THEN
    RAISE EXCEPTION 'Il faut au moins 4 vehicules dans la table vehicule.';
  END IF;
END $$;

-- Assure qu'il y a au moins un lieu et recupere son id
WITH ensured_lieu AS (
  INSERT INTO lieu (libelle, code)
  SELECT 'Hotel Seed 2026-03-11', 'S11H'
  WHERE NOT EXISTS (SELECT 1 FROM lieu)
  ON CONFLICT (code) DO NOTHING
  RETURNING id
), picked_lieu AS (
  SELECT id FROM ensured_lieu
  UNION ALL
  SELECT id FROM lieu ORDER BY id LIMIT 1
), seed_rows AS (
  SELECT *
  FROM (
    VALUES
      ('A111', 2, (:'base_time')::timestamp + INTERVAL '0 hour'),
      ('A112', 3, (:'base_time')::timestamp + INTERVAL '1 hour'),
      ('A113', 1, (:'base_time')::timestamp + INTERVAL '2 hour'),
      ('A114', 4, (:'base_time')::timestamp + INTERVAL '3 hour'),
      ('A115', 2, (:'base_time')::timestamp + INTERVAL '4 hour')
  ) AS t(id_client, nbr_passager, date_heure_arrivee)
)
INSERT INTO reservation (id_client, nbr_passager, date_heure_arrivee, id_hotel_arrivee)
SELECT
  s.id_client,
  s.nbr_passager,
  s.date_heure_arrivee,
  (SELECT id FROM picked_lieu LIMIT 1)
FROM seed_rows s
ON CONFLICT (id_client) DO UPDATE
SET
  nbr_passager = EXCLUDED.nbr_passager,
  date_heure_arrivee = EXCLUDED.date_heure_arrivee,
  id_hotel_arrivee = EXCLUDED.id_hotel_arrivee;

-- Repartit 5 reservations seed sur 4 vehicules: 2 sur le 1er, 1 sur les 3 autres
WITH veh_ids AS (
  SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
  FROM vehicule
  ORDER BY id
  LIMIT 4
), res_seed AS (
  SELECT id, id_client, ROW_NUMBER() OVER (ORDER BY date_heure_arrivee, id) AS rn
  FROM reservation
  WHERE id_client IN ('A111', 'A112', 'A113', 'A114', 'A115')
)
INSERT INTO assignation (id_vehicule, id_reservation, date_assignation)
SELECT
  CASE
    WHEN r.rn IN (1, 2) THEN (SELECT id FROM veh_ids WHERE rn = 1)
    WHEN r.rn = 3 THEN (SELECT id FROM veh_ids WHERE rn = 2)
    WHEN r.rn = 4 THEN (SELECT id FROM veh_ids WHERE rn = 3)
    WHEN r.rn = 5 THEN (SELECT id FROM veh_ids WHERE rn = 4)
  END AS id_vehicule,
  r.id AS id_reservation,
  (:'base_time')::timestamp + (r.rn * INTERVAL '10 minutes') AS date_assignation
FROM res_seed r
ON CONFLICT (id_reservation) DO UPDATE
SET
  id_vehicule = EXCLUDED.id_vehicule,
  date_assignation = EXCLUDED.date_assignation;

-- Verification rapide
SELECT
  DATE(r.date_heure_arrivee) AS jour,
  a.id_vehicule,
  COUNT(*) AS nb_reservations
FROM assignation a
JOIN reservation r ON r.id = a.id_reservation
WHERE DATE(r.date_heure_arrivee) = :'target_date'
GROUP BY DATE(r.date_heure_arrivee), a.id_vehicule
ORDER BY nb_reservations DESC, a.id_vehicule;
