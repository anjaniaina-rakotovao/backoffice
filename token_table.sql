-- Table to store API tokens
-- Adjust types if your DB is not MySQL
CREATE TABLE IF NOT EXISTS token (
  id INT AUTO_INCREMENT PRIMARY KEY,
  token VARCHAR(64) NOT NULL UNIQUE,
  date_expiration TIMESTAMP NOT NULL
);

-- Exemple d'insertion (MySQL) : génère un UUID et l'expire dans 7 jours
INSERT INTO token (token, date_expiration)
VALUES (UUID(), DATE_ADD(NOW(), INTERVAL 7 DAY));

-- Alternativement, pour un token spécifique (copier le token retourné par UUID() ci-dessus)
-- INSERT INTO token (token, date_expiration) VALUES ('PUT-YOUR-UUID-HERE', '2026-03-11 12:00:00');

--correct
CREATE TABLE IF NOT EXISTS token (
  id SERIAL PRIMARY KEY,
  token VARCHAR(64) NOT NULL UNIQUE,
  date_expiration TIMESTAMP NOT NULL
);

-- Exemple d'insertion (PostgreSQL) : génère un UUID et l'expire dans 7 jours
INSERT INTO token (token, date_expiration)
VALUES (gen_random_uuid()::text, NOW() + INTERVAL '7 days');