-- Table Lieu (Hôtel ou Aéroport)
CREATE TABLE lieu (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE
);

-- Table Distance (entre deux lieux)
CREATE TABLE distance (
    id SERIAL PRIMARY KEY,
    id_from INT NOT NULL REFERENCES lieu (id) ON DELETE CASCADE,
    id_to INT NOT NULL REFERENCES lieu (id) ON DELETE CASCADE,
    distance_km DECIMAL(8, 2) NOT NULL CHECK (distance_km > 0),
    UNIQUE (id_from, id_to)
);

-- Table Reservation
CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    id_client VARCHAR(4) NOT NULL UNIQUE,
    nbr_passager INT NOT NULL CHECK (nbr_passager > 0),
    date_heure_arrivee TIMESTAMP NOT NULL,
    id_hotel_arrivee INT NOT NULL REFERENCES lieu (id) ON DELETE CASCADE
);

-- Index sur date pour les requêtes de filtrage
CREATE INDEX idx_reservation_date ON reservation (date_heure_arrivee);

CREATE INDEX idx_reservation_client ON reservation (id_client);

insert into
    vehicule (reference, nbr_place, type)
values
    ('vehicule1', 12, 'd'),
    ('vehicule2', 5, 'e'),
    ('vehicule3', 5, 'd'),
    ('vehicule4', 12, 'e');

insert into
    lieu (libelle, code)
values
    ('aeroport', 'AE'),
    ('hotel1', 'h1');

insert into
    distance (id_from, id_to, distance_km)
values
    (2, 1, 50);
