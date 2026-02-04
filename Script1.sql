Create database projetmrnaina;

create table type(
    id_type int serial primary key,
    nom varchar(255) not null
);
create table vehicule(
    id_vehicule int serial primary key,
    id_type int references type(id_type),
    capacite int not null
);

create table nyavo(
    id int serial primary key,
    nom varchar(255) not null,
);