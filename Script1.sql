Create database projetmrnaina;

create table type(
    id_type int serial primary key,
    nom varchar(255) not null
);
-- create table vehicule(
--     id_vehicule int serial primary key,
--     id_type int references type(id_type),
--     capacite int not null
-- );

--correction
CREATE TABLE vehicule (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL,
    nbr_place INT NOT NULL CHECK (nbr_place > 0),
    type CHAR(2) NOT NULL CHECK (type IN ('h', 'd', 'el', 'e'))
);
