# Test final - entrees et resultats attendus

## 1) Pre-conditions

1. Executer d'abord `reset_tables_data.sql`.
2. Executer ensuite `test_final.sql`.
3. Lancer l'auto-assignation pour la date `2026-03-20`.

Options pour l'etape 3:

- Depuis l'UI planning: ouvrir la page planning, choisir `2026-03-20`, cliquer `Afficher`, puis cliquer `Lancer assignation automatique`.
- Depuis endpoint HTTP: `POST /planning/auto-assign` avec le parametre `date=2026-03-20`.

Important:

- Si le parametre `date` n'est pas envoye, le backend utilise la date du jour. Dans ce cas, aucune assignation ne sera creee pour les reservations du `2026-03-20`.

## 2) Entrees inserees

### Lieux

- 1 aeroport:
  - AER = Aeroport International
- 2 hotels:
  - H01 = Hotel Alpha
  - H02 = Hotel Beta

### Vehicules

- VAN-10: 10 places
- MINI-05: 5 places
- CITY-04: 4 places
- MICRO-02: 2 places

### Reservations (jour unique: 2026-03-20)

- C801: 8 passagers, 08:10, Hotel Alpha
- C302: 3 passagers, 08:40, Hotel Beta
- C103: 1 passager, 08:42, Hotel Alpha
- C604: 6 passagers, 10:00, Hotel Beta

## 3) Resultats attendus apres auto-assignation

### A. Priorite petites reservations avant fragmentation

Cas vise:

- A 08:10, la reservation de 8 remplit partiellement VAN-10 (8/10).
- A l'intervalle suivant (08:30-09:00), la reservation de 1 doit passer avant de fragmenter la reservation de 3.

Attendu:

- C103 (1 passager) est assignee en totalite avant que C302 ne soit fragmentee sur la derniere place de VAN-10.

### B. Split des reservations

Attendu:

- C302 (3 passagers) aura au moins 2 lignes d'assignation (exemple attendu: 1 sur VAN-10 + 2 sur MICRO-02).
- C604 (6 passagers) sera splittee (exemple attendu: 5 sur MINI-05 + 1 sur CITY-04).

### C. Colonne split

Attendu:

- `assignation.nbr_passager_assigne` est renseignee (> 0) pour chaque ligne.
- Plusieurs lignes pour la meme reservation sont autorisees (plus de contrainte unique sur `id_reservation`).

### D. Disponibilite vehicule

Attendu:

- `vehicule.date_disponibilite` est mise a jour apres assignation(s).
- La disponibilite d'un vehicule est >= heure de depart de sa derniere assignation + duree par defaut (30 min dans la logique actuelle).

## 4) Requetes de verification

### 4.1 Vue globale assignations

```sql
SELECT
    a.id,
    a.id_vehicule,
    v.reference,
    a.id_reservation,
    r.id_client,
    r.nbr_passager AS reservation_total,
    a.nbr_passager_assigne,
    a.date_depart
FROM assignation a
JOIN vehicule v ON v.id = a.id_vehicule
JOIN reservation r ON r.id = a.id_reservation
WHERE DATE(r.date_heure_arrivee) = '2026-03-20'
ORDER BY r.date_heure_arrivee, a.id;
```

### 4.2 Verifier que chaque reservation est totalement couverte

```sql
SELECT
    r.id,
    r.id_client,
    r.nbr_passager AS total_reservation,
    COALESCE(SUM(a.nbr_passager_assigne), 0) AS total_assigne,
    (r.nbr_passager - COALESCE(SUM(a.nbr_passager_assigne), 0)) AS restant
FROM reservation r
LEFT JOIN assignation a ON a.id_reservation = r.id
WHERE DATE(r.date_heure_arrivee) = '2026-03-20'
GROUP BY r.id, r.id_client, r.nbr_passager
ORDER BY r.id;
```

### 4.3 Verifier le split (reservations avec plusieurs lignes)

```sql
SELECT
    a.id_reservation,
    r.id_client,
    COUNT(*) AS nb_lignes_assignation,
    SUM(a.nbr_passager_assigne) AS total_assigne
FROM assignation a
JOIN reservation r ON r.id = a.id_reservation
WHERE DATE(r.date_heure_arrivee) = '2026-03-20'
GROUP BY a.id_reservation, r.id_client
HAVING COUNT(*) > 1
ORDER BY a.id_reservation;
```

### 4.4 Verifier disponibilite des vehicules

```sql
SELECT
    v.id,
    v.reference,
    v.nbr_place,
    v.date_disponibilite
FROM vehicule v
ORDER BY v.id;
```

## 5) Points d'acceptation rapides

- Le dataset charge exactement:
  - 3 lieux (1 aeroport + 2 hotels)
  - 4 vehicules (<= 5)
  - 4 reservations sur la meme date
- Les reservations de 3 et 6 sont splittees apres auto-assignation.
- La reservation de 1 est traitee avant de consommer la derniere place de VAN-10 pour la reservation de 3.
- Toutes les lignes d'assignation ont `nbr_passager_assigne > 0`.
