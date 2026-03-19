-- Table Assignation (Assignation de véhicules aux réservations)
CREATE TABLE assignation (
    id SERIAL PRIMARY KEY,
    id_vehicule INT NOT NULL REFERENCES vehicule (id) ON DELETE CASCADE,
    id_reservation INT NOT NULL REFERENCES reservation (id) ON DELETE CASCADE,
    nbr_passager_assigne INT NOT NULL CHECK (nbr_passager_assigne > 0),
    date_assignation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_depart TIMESTAMP
);

-- Index pour les requêtes de filtrage
CREATE INDEX idx_assignation_vehicule ON assignation (id_vehicule);

CREATE INDEX idx_assignation_reservation ON assignation (id_reservation);

CREATE INDEX idx_assignation_date ON assignation (date_assignation);

insert into
    assignation (id_vehicule, id_reservation, date_depart)
values
    (8, 1, '2023-10-10 08:00:00');