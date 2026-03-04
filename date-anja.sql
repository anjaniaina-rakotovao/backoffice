-- Insertion des lieux (hôtels et aéroport)
INSERT INTO lieu (libelle, code) VALUES
-- Aéroport
('Aéroport International d''Antananarivo', 'TNR'),

-- Hôtels à Antananarivo centre/ville
('Hôtel Carlton Madagascar', 'CARLTON'),
('Hôtel Colbert', 'COLBERT'),
('Hôtel Radisson Blu Ambodivona', 'RADISSON'),
('Hôtel Tamboho', 'TAMBOHO'),
('Hôtel Palissandre', 'PALISSANDRE'),
('Hôtel La Ribaudière', 'RIBAUD'),
('Hôtel Belvedere', 'BELVED'),
('Hôtel Le Louvre', 'LOUVRE'),
('Hôtel Sakamanga', 'SAKAM'),
('Hôtel Ibis Antananarivo', 'IBIS'),

-- Hôtels en périphérie
('Hôtel Novotel Convention & Spa', 'NOVO'),
('Hôtel Royal Palissandre', 'ROYAL'),
('Hôtel Crystal', 'CRYSTAL'),
('Hôtel Chalet des Roses', 'CHALET'),
('Hôtel Green View', 'GREEN');

-- Insertion des distances depuis l'aéroport (TNR)
INSERT INTO distance (id_from, id_to, distance_km) 
SELECT 
    (SELECT id FROM lieu WHERE code = 'TNR'),
    id,
    CASE code
        WHEN 'CARLTON' THEN 15.5
        WHEN 'COLBERT' THEN 16.2
        WHEN 'RADISSON' THEN 14.8
        WHEN 'TAMBOHO' THEN 13.5
        WHEN 'PALISSANDRE' THEN 16.0
        WHEN 'RIBAUD' THEN 15.8
        WHEN 'BELVED' THEN 17.5
        WHEN 'LOUVRE' THEN 16.5
        WHEN 'SAKAM' THEN 15.0
        WHEN 'IBIS' THEN 14.5
        WHEN 'NOVO' THEN 22.0
        WHEN 'ROYAL' THEN 18.5
        WHEN 'CRYSTAL' THEN 16.8
        WHEN 'CHALET' THEN 19.5
        WHEN 'GREEN' THEN 21.0
    END
FROM lieu 
WHERE code != 'TNR';

-- Insertion des réservations (clients avec dates variées)
INSERT INTO reservation (id_client, nbr_passager, date_heure_arrivee, id_hotel_arrivee) VALUES
-- Réservations récentes (2024)
('CL01', 2, '2024-12-15 14:30:00', (SELECT id FROM lieu WHERE code = 'CARLTON')),
('CL02', 1, '2024-12-16 09:15:00', (SELECT id FROM lieu WHERE code = 'RADISSON')),
('CL03', 4, '2024-12-17 22:45:00', (SELECT id FROM lieu WHERE code = 'TAMBOHO')),
('CL04', 2, '2024-12-18 11:30:00', (SELECT id FROM lieu WHERE code = 'COLBERT')),
('CL05', 3, '2024-12-19 16:20:00', (SELECT id FROM lieu WHERE code = 'SAKAM')),

-- Réservations pour janvier 2025 (haute saison)
('CL06', 2, '2025-01-05 10:00:00', (SELECT id FROM lieu WHERE code = 'NOVO')),
('CL07', 5, '2025-01-08 13:45:00', (SELECT id FROM lieu WHERE code = 'ROYAL')),
('CL08', 2, '2025-01-10 18:30:00', (SELECT id FROM lieu WHERE code = 'PALISSANDRE')),
('CL09', 3, '2025-01-12 08:15:00', (SELECT id FROM lieu WHERE code = 'RIBAUD')),
('CL10', 1, '2025-01-15 21:00:00', (SELECT id FROM lieu WHERE code = 'CRYSTAL')),
('CL11', 4, '2025-01-18 12:30:00', (SELECT id FROM lieu WHERE code = 'IBIS')),
('CL12', 2, '2025-01-20 15:45:00', (SELECT id FROM lieu WHERE code = 'GREEN')),
('CL13', 3, '2025-01-22 09:30:00', (SELECT id FROM lieu WHERE code = 'CHALET')),

-- Réservations pour février 2025
('CL14', 2, '2025-02-01 17:15:00', (SELECT id FROM lieu WHERE code = 'BELVED')),
('CL15', 6, '2025-02-05 11:00:00', (SELECT id FROM lieu WHERE code = 'LOUVRE')),
('CL16', 2, '2025-02-10 14:20:00', (SELECT id FROM lieu WHERE code = 'CARLTON')),
('CL17', 3, '2025-02-15 19:45:00', (SELECT id FROM lieu WHERE code = 'RADISSON')),
('CL18', 4, '2025-02-20 08:30:00', (SELECT id FROM lieu WHERE code = 'TAMBOHO')),
('CL19', 1, '2025-02-25 22:15:00', (SELECT id FROM lieu WHERE code = 'COLBERT')),

-- Réservations pour mars 2025
('CL20', 2, '2025-03-02 13:00:00', (SELECT id FROM lieu WHERE code = 'SAKAM')),
('CL21', 3, '2025-03-07 16:30:00', (SELECT id FROM lieu WHERE code = 'NOVO')),
('CL22', 2, '2025-03-12 09:45:00', (SELECT id FROM lieu WHERE code = 'ROYAL')),
('CL23', 5, '2025-03-17 12:15:00', (SELECT id FROM lieu WHERE code = 'PALISSANDRE')),
('CL24', 2, '2025-03-22 18:00:00', (SELECT id FROM lieu WHERE code = 'RIBAUD')),
('CL25', 3, '2025-03-27 20:30:00', (SELECT id FROM lieu WHERE code = 'CRYSTAL')),

-- Réservations supplémentaires pour avril 2025
('CL26', 4, '2025-04-01 07:45:00', (SELECT id FROM lieu WHERE code = 'IBIS')),
('CL27', 2, '2025-04-05 15:30:00', (SELECT id FROM lieu WHERE code = 'GREEN')),
('CL28', 3, '2025-04-10 11:15:00', (SELECT id FROM lieu WHERE code = 'CHALET')),
('CL29', 1, '2025-04-15 14:45:00', (SELECT id FROM lieu WHERE code = 'BELVED')),
('CL30', 2, '2025-04-20 17:30:00', (SELECT id FROM lieu WHERE code = 'LOUVRE'));