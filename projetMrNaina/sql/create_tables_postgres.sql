

create database mydb;
\c mydb;

CREATE TABLE IF NOT EXISTS hotels (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    idclient VARCHAR(255) NOT NULL,
    nombre_passagers INTEGER NOT NULL,
    dateheure_arrivee TIMESTAMP NOT NULL,
    hotel_arrivee INTEGER REFERENCES hotels(id) ON DELETE SET NULL
);

-- Example data
INSERT INTO hotels(nom) VALUES ('Hôtel Plaza');
INSERT INTO reservations(idclient, nombre_passagers, dateheure_arrivee, hotel_arrivee)
VALUES ('client-123', 2, '2026-02-06 15:30:00', 1);
