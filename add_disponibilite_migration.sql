-- Migration: Ajouter le champ date_disponibilite à la table vehicule
-- Ce champ permet de tracker quand exactement un véhicule se libère
-- Format: YYYY-MM-DD HH:MM:SS

-- Ajouter la colonne (si elle n'existe pas)
ALTER TABLE vehicule 
ADD COLUMN date_disponibilite TIMESTAMP DEFAULT '1900-01-01 00:00:00';

-- Initialiser tous les véhicules existants à disponibles dès le départ
UPDATE vehicule 
SET date_disponibilite = '1900-01-01 00:00:00' 
WHERE date_disponibilite IS NULL;

-- Ajouter une contrainte CHECK
ALTER TABLE vehicule 
ADD CONSTRAINT chk_date_disponibilite CHECK (date_disponibilite IS NOT NULL);

-- Verification
SELECT id, reference, nbr_place, type, date_disponibilite FROM vehicule;
