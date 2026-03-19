package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import entity.Assignation;
import entity.Lieu;
import entity.Reservation;
import entity.Vehicule;
import util.DB;

public class AssignationModel {

    private static final double AVERAGE_SPEED_KMH = 50.0;
    private static final int WAITING_TIME_MINUTES = 30;
    private static final LocalTime GROUPING_START_TIME = LocalTime.of(8, 0);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static class ReservationWork {
        private final Reservation reservation;
        private int remainingPassengers;

        private ReservationWork(Reservation reservation, int remainingPassengers) {
            this.reservation = reservation;
            this.remainingPassengers = remainingPassengers;
        }
    }

    /**
     * Récupère toutes les assignations pour une date donnée
     */
    public static List<Assignation> findByDate(String date) throws SQLException {
        List<Assignation> list = new ArrayList<>();
        // Find assignations for reservations occurring on the given date.
        // We join with reservation and compare DATE(reservation.date_heure_arrivee) = ?
        String sql = "SELECT a.id, a.id_vehicule, a.id_reservation, a.nbr_passager_assigne, a.date_assignation, a.date_depart "
                +
                "FROM assignation a " +
                "JOIN reservation r ON r.id = a.id_reservation " +
                "WHERE DATE(r.date_heure_arrivee) = ? " +
                "ORDER BY a.date_assignation DESC";

        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            // Use SQL Date for DATE(...) comparison
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Assignation(
                            rs.getInt("id"),
                            rs.getInt("id_vehicule"),
                            rs.getInt("id_reservation"),
                            rs.getInt("nbr_passager_assigne"),
                            rs.getString("date_assignation"),
                            rs.getString("date_depart")));
                }
            }
        }
        return list;
    }

    /**
     * Récupère les véhicules disponibles à une date/heure donnée
     * Un véhicule est disponible si:
     * 1. Il a assez de places
     * 2. Sa date_disponibilite est avant ou égale à dateHeureArrivee
     * 3. Il n'est pas assigné à une autre réservation à ce moment
     */
    public static List<Vehicule> findAvailableVehicles(String dateHeureArrivee, int nbrPassager) throws SQLException {
        List<Vehicule> list = new ArrayList<>();
        String sql = "SELECT v.id, v.reference, v.nbr_place, v.type, COALESCE(v.date_disponibilite, '1900-01-01 00:00:00') AS date_disponibilite "
                +
                "FROM vehicule v " +
                "WHERE v.nbr_place >= ? " +
                "AND COALESCE(v.date_disponibilite, '1900-01-01 00:00:00') <= ? " +
                "AND v.id NOT IN ( " +
                "  SELECT id_vehicule FROM assignation " +
                "  WHERE date_assignation <= ? AND (date_depart IS NULL OR date_depart > ?) " +
                ") " +
                "ORDER BY v.nbr_place ASC, TRIM(v.type) DESC";

        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, nbrPassager);
            // dateHeureArrivee is expected as 'YYYY-MM-DD HH:MM:SS'
            Timestamp ts = Timestamp.valueOf(dateHeureArrivee);
            ps.setTimestamp(2, ts);
            ps.setTimestamp(3, ts);
            ps.setTimestamp(4, ts);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String dateDisp = rs.getTimestamp("date_disponibilite").toString();
                    list.add(new Vehicule(
                            rs.getInt("id"),
                            rs.getString("reference"),
                            rs.getInt("nbr_place"),
                            rs.getString("type"),
                            dateDisp));
                }
            }
        }
        return list;
    }

    /**
     * Assigne un véhicule à une réservation
     */
    // public static void assignVehicle(int idVehicule, int idReservation, String
    // dateDepart) throws SQLException {
    // String sql = "INSERT INTO assignation (id_vehicule, id_reservation,
    // date_depart) VALUES (?, ?, ?)";
    // try (Connection c = DB.getConnection();
    // PreparedStatement ps = c.prepareStatement(sql)) {
    // ps.setInt(1, idVehicule);
    // ps.setInt(2, idReservation);
    // ps.setString(3, dateDepart);
    // ps.executeUpdate();
    // System.out.println("Assignation créée: vehicule=" + idVehicule + "
    // reservation=" + idReservation);
    // }
    // }

    public static void assignVehicle(int idVehicule, int idReservation, String dateDepart) throws SQLException {
        Reservation reservation = ReservationModel.findById(idReservation);
        int nbrPassagerAssigne = reservation != null ? reservation.getNbrPassager() : 1;
        assignVehicle(idVehicule, idReservation, dateDepart, nbrPassagerAssigne);
    }

    public static void assignVehicle(int idVehicule, int idReservation, String dateDepart, int nbrPassagerAssigne)
            throws SQLException {
        String sql = "INSERT INTO assignation (id_vehicule, id_reservation, nbr_passager_assigne, date_depart) VALUES (?, ?, ?, ?)";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idVehicule);
            ps.setInt(2, idReservation);
            ps.setInt(3, nbrPassagerAssigne);

            if (dateDepart == null) {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(4, Timestamp.valueOf(dateDepart));
            }

            ps.executeUpdate();

            System.out.println("Assignation créée: vehicule=" + idVehicule + " reservation=" + idReservation
                    + " passagersAssignes=" + nbrPassagerAssigne);

            // Mise à jour de la disponibilité du véhicule après assignation
            if (dateDepart != null) {
                updateVehicleAvailability(idVehicule, dateDepart);
            }
        }
    }

    /**
     * Assigne automatiquement les véhicules aux réservations d'une date donnée
     * Applique les règles de sélection:
     * 1. Minimal places restants
     * 2. Si égal, prendre le véhicule avec le moins de trajets sur la date
     * 3. Si égal, prendre diesel
     * 4. Si toujours égal, prendre random
     * Ne réassigne que les réservations qui n'ont pas encore d'assignation
     */
    // public static void autoAssignVehicles(String date) throws SQLException {
    // // Récupère les réservations de cette date
    // List<Reservation> reservations = ReservationModel.findByDateString(date);
    // System.out.println("autoAssignVehicles: found " + reservations.size() + "
    // reservations for date=" + date);

    // for (Reservation res : reservations) {
    // // Récupère les véhicules disponibles
    // List<Vehicule> available = findAvailableVehicles(res.getDateHeureArrivee(),
    // res.getNbrPassager());
    // System.out.println("Reservation " + res.getId() + " nbrPassager=" +
    // res.getNbrPassager() + " availableVehicles=" + (available == null ? 0 :
    // available.size()));

    // // Vérifie si cette réservation a déjà une assignation
    // Assignation existing = findByReservation(res.getId());
    // if (existing != null) {
    // System.out.println("Reservation " + res.getId() + " déjà assignée -> skip");
    // continue; // Déjà assignée, passer à la suivante
    // }

    // if (available == null || available.isEmpty()) {
    // System.out.println("Aucun véhicule disponible pour réservation " +
    // res.getId());
    // continue; // Pas de véhicule disponible
    // }

    // // Applique les règles de sélection
    // Vehicule selected = selectBestVehicle(available, res.getNbrPassager());
    // System.out.println("Reservation " + res.getId() + " selected vehicle: " +
    // (selected != null ? selected.getId() : "null"));

    // if (selected != null) {
    // assignVehicle(selected.getId(), res.getId(), null);
    // }
    // }
    // }

    public static void autoAssignVehicles(String date) throws SQLException {
        List<Reservation> reservations = ReservationModel.findByDateString(date);
        System.out.println("autoAssignVehicles: " + reservations.size()
                + " réservations pour date=" + date + " (groupement=" + WAITING_TIME_MINUTES + " min)");

        if (reservations.isEmpty()) {
            return;
        }

        // Récupère tous les véhicules une seule fois
        List<Vehicule> allVehicles = VehiculeModel.findAll();
        // Charge initiale: places déjà occupées par des réservations déjà assignées ce
        // jour.
        Map<Integer, Integer> vehicleLoad = getVehiclePassengerLoad(date);
        Map<Integer, Integer> assignedByReservation = getAssignedPassengerCountByReservationForDate(date);

        TreeMap<LocalDateTime, List<Reservation>> groupedByInterval = groupByIntervalInternal(reservations);
        List<ReservationWork> pending = new ArrayList<>();

        for (Map.Entry<LocalDateTime, List<Reservation>> intervalEntry : groupedByInterval.entrySet()) {
            LocalDateTime intervalStart = intervalEntry.getKey();
            List<Reservation> currentIntervalReservations = intervalEntry.getValue();

            List<ReservationWork> toProcess = new ArrayList<>(pending);
            for (Reservation res : currentIntervalReservations) {
                int alreadyAssigned = assignedByReservation.getOrDefault(res.getId(), 0);
                int remaining = res.getNbrPassager() - alreadyAssigned;
                if (remaining > 0) {
                    toProcess.add(new ReservationWork(res, remaining));
                }
            }
            toProcess.sort((a, b) -> Integer.compare(b.remainingPassengers, a.remainingPassengers));

            LocalDateTime departureDateTime = findLatestArrival(currentIntervalReservations);
            if (departureDateTime == null) {
                departureDateTime = intervalStart;
            }
            String departureForInterval = departureDateTime.format(DATE_TIME_FORMATTER);

            System.out.println("Intervalle " + intervalStart + " : " + toProcess.size()
                    + " réservations à traiter (" + pending.size() + " en priorité reportée)");

            List<ReservationWork> nextPending = new ArrayList<>();

            // Boucle d'assignation : continuer tant qu'on peut assigner quelque chose
            // Retrier à chaque itération pour prioriser les petites réservations complètes
            boolean assignmentMade = true;
            while (assignmentMade) {
                assignmentMade = false;

                // Trier par taille croissante pour prioriser l'assignation des petites
                // réservations EN ENTIER
                toProcess.sort((a, b) -> Integer.compare(a.remainingPassengers, b.remainingPassengers));

                Iterator<ReservationWork> it = toProcess.iterator();
                while (it.hasNext()) {
                    ReservationWork work = it.next();

                    if (work.remainingPassengers <= 0) {
                        it.remove();
                        continue;
                    }

                    // Stratégie 1 : Chercher un véhicule partiellement rempli EN PRIORITÉ
                    // (pour compléter plutôt que fragmenter une grosse réservation)
                    Vehicule selected = findPartiallyFilledVehicle(allVehicles, vehicleLoad, 1, date);

                    // Stratégie 2 : Si pas de partiellement rempli, chercher le meilleur fit
                    if (selected == null) {
                        selected = selectBestVehicleByRemainingSeats(allVehicles, vehicleLoad,
                                work.remainingPassengers, date);
                    }

                    // Stratégie 3 : Si aucun véhicule ne peut contenir la réservation entièrement,
                    // chercher au moins 1 place pour fragmenter minimalement
                    // Cela garantit que les petites réservations sont fragmentées EN PRIORITÉ
                    if (selected == null) {
                        selected = selectBestVehicleByRemainingSeats(allVehicles, vehicleLoad, 1, date);
                    }

                    if (selected != null) {
                        int capacityLeft = selected.getNbrPlace() - vehicleLoad.getOrDefault(selected.getId(), 0);
                        if (capacityLeft > 0) {
                            int chunk = Math.min(work.remainingPassengers, capacityLeft);
                            assignVehicle(selected.getId(), work.reservation.getId(), departureForInterval, chunk);

                            vehicleLoad.put(selected.getId(), vehicleLoad.getOrDefault(selected.getId(), 0) + chunk);
                            assignedByReservation.put(work.reservation.getId(),
                                    assignedByReservation.getOrDefault(work.reservation.getId(), 0) + chunk);
                            work.remainingPassengers -= chunk;
                            assignmentMade = true;
                        }
                    }

                    // Retirer si assignée entièrement
                    if (work.remainingPassengers == 0) {
                        it.remove();
                    }
                }
            }

            // Ajouter les réservations toujours non assignées au pending
            nextPending.addAll(toProcess);
            pending = nextPending;
        }

        if (!pending.isEmpty()) {
            System.out.println("autoAssignVehicles: " + pending.size()
                    + " réservation(s) non assignée(s) après tous les intervalles");
        }
    }

    /**
     * Regroupe des réservations par intervalles fixes (08:00 +
     * WAITING_TIME_MINUTES)
     * et retourne un Map ordonné chronologiquement.
     */
    public static Map<String, List<Reservation>> groupReservationsByInterval(List<Reservation> reservations) {
        LinkedHashMap<String, List<Reservation>> result = new LinkedHashMap<>();
        if (reservations == null || reservations.isEmpty()) {
            return result;
        }

        TreeMap<LocalDateTime, List<Reservation>> grouped = groupByIntervalInternal(reservations);
        for (Map.Entry<LocalDateTime, List<Reservation>> entry : grouped.entrySet()) {
            LocalDateTime start = entry.getKey();
            LocalDateTime end = start.plusMinutes(WAITING_TIME_MINUTES);
            String key = String.format("%s | %s - %s",
                    start.toLocalDate(),
                    start.toLocalTime().format(TIME_FORMATTER),
                    end.toLocalTime().format(TIME_FORMATTER));
            result.put(key, entry.getValue());
        }

        return result;
    }

    private static TreeMap<LocalDateTime, List<Reservation>> groupByIntervalInternal(List<Reservation> reservations) {
        TreeMap<LocalDateTime, List<Reservation>> grouped = new TreeMap<>();
        for (Reservation r : reservations) {
            LocalDateTime arrival = parseReservationDateTime(r.getDateHeureArrivee());
            LocalDateTime intervalStart = computeIntervalStart(arrival);
            grouped.computeIfAbsent(intervalStart, k -> new ArrayList<>()).add(r);
        }

        for (List<Reservation> group : grouped.values()) {
            group.sort(Comparator.comparing(Reservation::getDateHeureArrivee));
        }

        return grouped;
    }

    private static LocalDateTime computeIntervalStart(LocalDateTime arrival) {
        LocalDate date = arrival.toLocalDate();
        LocalDateTime anchor = LocalDateTime.of(date, GROUPING_START_TIME);

        // Si une réservation arrive avant 08:00, on la place dans le premier groupement
        // de la journée.
        if (arrival.isBefore(anchor)) {
            return anchor;
        }

        long minutesSinceAnchor = java.time.Duration.between(anchor, arrival).toMinutes();
        long bucketIndex = minutesSinceAnchor / WAITING_TIME_MINUTES;
        return anchor.plusMinutes(bucketIndex * WAITING_TIME_MINUTES);
    }

    private static LocalDateTime parseReservationDateTime(String dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("dateTime de réservation null");
        }
        return Timestamp.valueOf(dateTime).toLocalDateTime();
    }

    private static LocalDateTime findLatestArrival(List<Reservation> reservations) {
        LocalDateTime latest = null;
        for (Reservation r : reservations) {
            LocalDateTime arrival = parseReservationDateTime(r.getDateHeureArrivee());
            if (latest == null || arrival.isAfter(latest)) {
                latest = arrival;
            }
        }
        return latest;
    }

    /**
     * Retourne la charge (total passagers assignés) par véhicule
     * pour les réservations arrivées à la date donnée.
     */
    private static Map<Integer, Integer> getVehiclePassengerLoad(String date) throws SQLException {
        Map<Integer, Integer> load = new HashMap<>();
        String sql = "SELECT a.id_vehicule, SUM(a.nbr_passager_assigne) AS total_passagers " +
                "FROM assignation a " +
                "JOIN reservation r ON r.id = a.id_reservation " +
                "WHERE DATE(r.date_heure_arrivee) = ? " +
                "GROUP BY a.id_vehicule";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    load.put(rs.getInt("id_vehicule"), rs.getInt("total_passagers"));
                }
            }
        }
        return load;
    }

    private static Map<Integer, Integer> getAssignedPassengerCountByReservationForDate(String date)
            throws SQLException {
        Map<Integer, Integer> assigned = new HashMap<>();
        String sql = "SELECT a.id_reservation, SUM(a.nbr_passager_assigne) AS total_assignes " +
                "FROM assignation a " +
                "JOIN reservation r ON r.id = a.id_reservation " +
                "WHERE DATE(r.date_heure_arrivee) = ? " +
                "GROUP BY a.id_reservation";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assigned.put(rs.getInt("id_reservation"), rs.getInt("total_assignes"));
                }
            }
        }
        return assigned;
    }

    /**
     * Parmi les véhicules déjà utilisés ce jour (charge > 0), retourne celui
     * qui peut encore accueillir nbrPassager supplémentaires et dont la place
     * restante après ajout est minimale (best-fit). Retourne null si aucun ne
     * convient.
     */
    private static Vehicule findPartiallyFilledVehicle(List<Vehicule> allVehicles,
            Map<Integer, Integer> vehicleLoad, int nbrPassager, String date) throws SQLException {
        List<Vehicule> bestCandidates = new ArrayList<>();
        int bestRemaining = Integer.MAX_VALUE;

        for (Vehicule v : allVehicles) {
            int currentLoad = vehicleLoad.getOrDefault(v.getId(), 0);
            if (currentLoad == 0)
                continue; // pas encore utilisé ce jour

            int remaining = v.getNbrPlace() - currentLoad;
            if (remaining >= nbrPassager) {
                int remainingAfter = remaining - nbrPassager;
                if (remainingAfter < bestRemaining) {
                    bestRemaining = remainingAfter;
                    bestCandidates.clear();
                    bestCandidates.add(v);
                } else if (remainingAfter == bestRemaining) {
                    bestCandidates.add(v);
                }
            }
        }

        return pickByTripCountThenTypeThenRandom(bestCandidates, date);
    }

    /**
     * Sélectionne le meilleur véhicule en tenant compte des places déjà occupées.
     * - véhicule admissible: capacité restante >= nbrPassager
     * - priorité: places restantes minimales après affectation
     * - tie-break: diesel, puis aléatoire
     */
    private static Vehicule selectBestVehicleByRemainingSeats(List<Vehicule> allVehicles,
            Map<Integer, Integer> vehicleLoad, int nbrPassager, String date) throws SQLException {
        List<Vehicule> candidates = new ArrayList<>();
        int bestRemainingAfter = Integer.MAX_VALUE;

        for (Vehicule v : allVehicles) {
            int occupied = vehicleLoad.getOrDefault(v.getId(), 0);
            int remaining = v.getNbrPlace() - occupied;
            if (remaining < nbrPassager) {
                continue;
            }

            int remainingAfter = remaining - nbrPassager;
            if (remainingAfter < bestRemainingAfter) {
                bestRemainingAfter = remainingAfter;
                candidates.clear();
                candidates.add(v);
            } else if (remainingAfter == bestRemainingAfter) {
                candidates.add(v);
            }
        }

        if (!candidates.isEmpty()) {
            return pickByTripCountThenTypeThenRandom(candidates, date);
        }

        // Split fallback: aucun véhicule ne peut accueillir la réservation complète,
        // prendre le véhicule avec le plus de places restantes pour en affecter une
        // partie.
        List<Vehicule> splitCandidates = new ArrayList<>();
        int bestRemaining = -1;
        for (Vehicule v : allVehicles) {
            int occupied = vehicleLoad.getOrDefault(v.getId(), 0);
            int remaining = v.getNbrPlace() - occupied;
            if (remaining <= 0) {
                continue;
            }
            if (remaining > bestRemaining) {
                bestRemaining = remaining;
                splitCandidates.clear();
                splitCandidates.add(v);
            } else if (remaining == bestRemaining) {
                splitCandidates.add(v);
            }
        }

        if (splitCandidates.isEmpty()) {
            return null;
        }
        return pickByTripCountThenTypeThenRandom(splitCandidates, date);
    }

    /**
     * Départage final: moins de trajets sur la date, puis préférence diesel,
     * puis tirage aléatoire.
     */
    private static Vehicule pickByTripCountThenTypeThenRandom(List<Vehicule> candidates, String date)
            throws SQLException {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        int minTrips = Integer.MAX_VALUE;
        List<Vehicule> leastTripCandidates = new ArrayList<>();
        for (Vehicule v : candidates) {
            int tripCount = countTripsByVehicleAndDate(v.getId(), date);
            if (tripCount < minTrips) {
                minTrips = tripCount;
                leastTripCandidates.clear();
                leastTripCandidates.add(v);
            } else if (tripCount == minTrips) {
                leastTripCandidates.add(v);
            }
        }

        if (leastTripCandidates.size() == 1) {
            return leastTripCandidates.get(0);
        }

        List<Vehicule> dieselCandidates = new ArrayList<>();
        for (Vehicule v : leastTripCandidates) {
            String t = v.getType();
            if (t != null) {
                t = t.trim();
            }
            if (t != null && (t.equalsIgnoreCase("d") || t.equalsIgnoreCase("diesel"))) {
                dieselCandidates.add(v);
            }
        }

        Random rnd = new Random();
        if (!dieselCandidates.isEmpty()) {
            return dieselCandidates.get(rnd.nextInt(dieselCandidates.size()));
        }
        return leastTripCandidates.get(rnd.nextInt(leastTripCandidates.size()));
    }

    /**
     * Retourne les IDs de toutes les réservations déjà assignées.
     */
    public static Set<Integer> findAllAssignedReservationIds() throws SQLException {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT id_reservation FROM assignation";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("id_reservation"));
            }
        }
        return ids;
    }

    /**
     * Assigne automatiquement un véhicule pour une seule réservation
     * Utilise les mêmes règles que autoAssignVehicles mais ne traite qu'une
     * réservation
     */
    // public static void autoAssignForReservation(Reservation res) throws
    // SQLException {
    // if (res == null) return;
    // System.out.println("autoAssignForReservation: reservation=" + res);
    // // Vérifie si cette réservation a déjà une assignation
    // Assignation existing = findByReservation(res.getId());
    // if (existing != null) {
    // System.out.println("autoAssignForReservation: already assigned -> " +
    // existing.getId());
    // return; // déjà assignée
    // }

    // // Récupère les véhicules disponibles pour ce créneau
    // List<Vehicule> available = findAvailableVehicles(res.getDateHeureArrivee(),
    // res.getNbrPassager());
    // System.out.println("autoAssignForReservation: available vehicles=" +
    // (available == null ? 0 : available.size()));
    // if (available == null || available.isEmpty()) {
    // System.out.println("autoAssignForReservation: no vehicles available for
    // reservation " + res.getId());
    // return; // pas de véhicule disponible
    // }

    // Vehicule selected = selectBestVehicle(available, res.getNbrPassager());
    // System.out.println("autoAssignForReservation: selected=" + (selected != null
    // ? selected.getId() : "null"));
    // if (selected != null) {
    // assignVehicle(selected.getId(), res.getId(), null);
    // }
    // }

    public static void autoAssignForReservation(Reservation res) throws SQLException {
        if (res == null)
            return;
        System.out.println("autoAssignForReservation: reservation=" + res);

        String date = res.getDateHeureArrivee().substring(0, 10);
        int alreadyAssigned = findAssignedPassengerCountByReservation().getOrDefault(res.getId(), 0);
        int remaining = res.getNbrPassager() - alreadyAssigned;
        if (remaining <= 0) {
            return;
        }

        List<Vehicule> allVehicles = VehiculeModel.findAll();
        Map<Integer, Integer> vehicleLoad = getVehiclePassengerLoad(date);
        while (remaining > 0) {
            Vehicule selected = findPartiallyFilledVehicle(allVehicles, vehicleLoad, 1, date);
            if (selected == null) {
                selected = selectBestVehicleByRemainingSeats(allVehicles, vehicleLoad, remaining, date);
            }
            if (selected == null) {
                System.out.println(
                        "autoAssignForReservation: no vehicle with remaining seats for reservation " + res.getId());
                break;
            }
            int capacityLeft = selected.getNbrPlace() - vehicleLoad.getOrDefault(selected.getId(), 0);
            if (capacityLeft <= 0) {
                break;
            }
            int chunk = Math.min(remaining, capacityLeft);
            assignVehicle(selected.getId(), res.getId(), res.getDateHeureArrivee(), chunk);
            vehicleLoad.put(selected.getId(), vehicleLoad.getOrDefault(selected.getId(), 0) + chunk);
            remaining -= chunk;
        }
    }

    /**
     * Force une assignation pour une réservation en une seule requête SQL :
     * - filtre vehicules avec nbr_place >= nbrPassager
     * - exclut véhicules déjà occupés au moment d'arrivée
     * - ordonne par places restantes (asc) et préférence diesel
     * Si trouvé, crée l'enregistrement dans assignation et retourne true.
     */
    // public static boolean forceAssignReservation(Reservation res) throws
    // SQLException {
    // if (res == null) return false;
    // String dateHeure = res.getDateHeureArrivee();
    // int nbrPass = res.getNbrPassager();
    // String sql = "SELECT v.id FROM vehicule v " +
    // "WHERE v.nbr_place >= ? " +
    // "AND v.id NOT IN (SELECT id_vehicule FROM assignation WHERE date_assignation
    // <= ? AND (date_depart IS NULL OR date_depart > ?)) " +
    // "ORDER BY (v.nbr_place - ?) ASC, CASE WHEN LOWER(TRIM(v.type)) IN
    // ('d','diesel') THEN 0 ELSE 1 END ASC LIMIT 1";
    // try (Connection c = DB.getConnection();
    // PreparedStatement ps = c.prepareStatement(sql)) {
    // ps.setInt(1, nbrPass);
    // Timestamp ts = Timestamp.valueOf(dateHeure);
    // ps.setTimestamp(2, ts);
    // ps.setTimestamp(3, ts);
    // ps.setInt(4, nbrPass);
    // try (ResultSet rs = ps.executeQuery()) {
    // if (rs.next()) {
    // int vehId = rs.getInt(1);
    // // Insert assignation
    // assignVehicle(vehId, res.getId(), null);
    // System.out.println("forceAssignReservation: assignation created veh=" + vehId
    // + " res=" + res.getId());
    // return true;
    // } else {
    // System.out.println("forceAssignReservation: no candidate vehicle for res=" +
    // res.getId());
    // }
    // }
    // }
    // return false;
    // }
    public static boolean forceAssignReservation(Reservation res) throws SQLException {
        if (res == null)
            return false;

        String date = res.getDateHeureArrivee().substring(0, 10);
        int alreadyAssigned = findAssignedPassengerCountByReservation().getOrDefault(res.getId(), 0);
        int remaining = res.getNbrPassager() - alreadyAssigned;
        if (remaining <= 0) {
            return true;
        }

        List<Vehicule> allVehicles = VehiculeModel.findAll();
        Map<Integer, Integer> vehicleLoad = getVehiclePassengerLoad(date);
        boolean assignedAny = false;
        while (remaining > 0) {
            Vehicule selected = findPartiallyFilledVehicle(allVehicles, vehicleLoad, 1, date);
            if (selected == null) {
                selected = selectBestVehicleByRemainingSeats(allVehicles, vehicleLoad, remaining, date);
            }
            if (selected == null) {
                break;
            }

            int capacityLeft = selected.getNbrPlace() - vehicleLoad.getOrDefault(selected.getId(), 0);
            if (capacityLeft <= 0) {
                break;
            }

            int chunk = Math.min(remaining, capacityLeft);
            assignVehicle(selected.getId(), res.getId(), res.getDateHeureArrivee(), chunk);
            vehicleLoad.put(selected.getId(), vehicleLoad.getOrDefault(selected.getId(), 0) + chunk);
            remaining -= chunk;
            assignedAny = true;
        }

        if (remaining <= 0) {
            return true;
        }

        System.out.println("forceAssignReservation: assignation partielle/indisponible pour res=" + res.getId()
                + " restant=" + remaining + " passager(s)");
        return assignedAny;
    }

    /**
     * Récupère l'assignation pour une réservation
     */
    public static Assignation findByReservation(int idReservation) throws SQLException {
        String sql = "SELECT id, id_vehicule, id_reservation, nbr_passager_assigne, date_assignation, date_depart " +
                "FROM assignation WHERE id_reservation = ? ORDER BY id ASC LIMIT 1";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idReservation);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Assignation(
                            rs.getInt("id"),
                            rs.getInt("id_vehicule"),
                            rs.getInt("id_reservation"),
                            rs.getInt("nbr_passager_assigne"),
                            rs.getString("date_assignation"),
                            rs.getString("date_depart"));
                }
            }
        }
        return null;
    }

    /**
     * Récupère les assignations d'un véhicule à une date donnée
     */
    public static List<Assignation> findByVehicleAndDate(int idVehicule, String date) throws SQLException {
        List<Assignation> list = new ArrayList<>();
        String sql = "SELECT a.id, a.id_vehicule, a.id_reservation, a.nbr_passager_assigne, a.date_assignation, a.date_depart "
                +
                "FROM assignation a " +
                "JOIN reservation r ON r.id = a.id_reservation " +
                "WHERE a.id_vehicule = ? AND DATE(r.date_heure_arrivee) = ? " +
                "ORDER BY date_assignation ASC";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idVehicule);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Assignation(
                            rs.getInt("id"),
                            rs.getInt("id_vehicule"),
                            rs.getInt("id_reservation"),
                            rs.getInt("nbr_passager_assigne"),
                            rs.getString("date_assignation"),
                            rs.getString("date_depart")));
                }
            }
        }
        return list;
    }

    /**
     * Compte le nombre total de trajets (assignations) réalisés par un véhicule.
     */
    public static int countTripsByVehicle(int idVehicule) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM assignation WHERE id_vehicule = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idVehicule);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    /**
     * Compte le nombre de trajets d'un véhicule pour une date donnée
     * (date basée sur l'arrivée des réservations).
     */
    public static int countTripsByVehicleAndDate(int idVehicule, String date) throws SQLException {
        String sql = "SELECT COUNT(*) AS total " +
                "FROM assignation a " +
                "JOIN reservation r ON r.id = a.id_reservation " +
                "WHERE a.id_vehicule = ? AND DATE(r.date_heure_arrivee) = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idVehicule);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    /**
     * Retourne la somme des passagers déjà assignés par réservation (toutes dates).
     */
    public static Map<Integer, Integer> findAssignedPassengerCountByReservation() throws SQLException {
        Map<Integer, Integer> map = new HashMap<>();
        String sql = "SELECT id_reservation, SUM(nbr_passager_assigne) AS total_assignes " +
                "FROM assignation GROUP BY id_reservation";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getInt("id_reservation"), rs.getInt("total_assignes"));
            }
        }
        return map;
    }

    /**
     * Supprime une assignation
     */
    public static void delete(int id) throws SQLException {
        String sql = "DELETE FROM assignation WHERE id = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Met à jour la date de départ d'une assignation
     */
    public static void updateDateDepart(int id, String dateDepart) throws SQLException {
        String sql = "UPDATE assignation SET date_depart = ? WHERE id = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dateDepart);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static List<Map<String, Object>> getVehiculesAvecReservations() throws SQLException {

        List<Map<String, Object>> list = new ArrayList<>();

        String sql = "SELECT v.id vehicule_id, v.reference, v.nbr_place, " +
                "r.id reservation_id, r.id_client, r.nbr_passager, r.date_heure_arrivee " +
                "FROM vehicule v " +
                "LEFT JOIN assignation a ON v.id = a.id_vehicule " +
                "LEFT JOIN reservation r ON r.id = a.id_reservation " +
                "ORDER BY v.id";

        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Map<String, Object> row = new HashMap<>();

                row.put("vehicule_id", rs.getInt("vehicule_id"));
                row.put("reference", rs.getString("reference"));
                row.put("nbr_place", rs.getInt("nbr_place"));
                row.put("reservation_id", rs.getInt("reservation_id"));
                row.put("client", rs.getString("id_client"));
                row.put("passagers", rs.getInt("nbr_passager"));
                row.put("date", rs.getTimestamp("date_heure_arrivee"));

                list.add(row);
            }
        }

        return list;
    }

    /**
     * Calcule le trajet optimal pour un ensemble de lieux (hôtels)
     * en utilisant l'algorithme Nearest Neighbor à partir de l'aéroport.
     * Tri alphabétique en cas d'égalité de distance.
     * Retourne une liste de lieux triés : départ -> ... -> aéroport (retour)
     */
    public static List<Lieu> computeOptimalRoute(List<Lieu> hotels) throws SQLException {
        if (hotels == null || hotels.isEmpty()) {
            return new ArrayList<>();
        }

        // Récupère l'aéroport
        Lieu airport = LieuModel.findAirport();
        if (airport == null) {
            // Pas d'aéroport trouvé, retourner les hôtels dans l'ordre alphabétique
            hotels.sort((a, b) -> a.getLibelle().compareTo(b.getLibelle()));
            return hotels;
        }

        List<Lieu> route = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        // Ajouter l'aéroport comme point de départ
        route.add(airport);
        visited.add(airport.getId());

        int currentId = airport.getId();

        // Nearest Neighbor: sélectionner le lieu le plus proche non visité
        while (visited.size() < hotels.size() + 1) {
            Lieu nextLieu = null;
            double minDistance = Double.MAX_VALUE;

            // Récupérer les distances depuis le lieu actuel
            Map<Integer, Double> distances = LieuModel.getDistancesFrom(currentId);

            for (Lieu hotel : hotels) {
                if (visited.contains(hotel.getId())) {
                    continue;
                }

                double dist = distances.getOrDefault(hotel.getId(), Double.MAX_VALUE);

                // Tie-break: si même distance, ordonner alphabétiquement
                if (dist < minDistance || (dist == minDistance && nextLieu != null &&
                        hotel.getLibelle().compareTo(nextLieu.getLibelle()) < 0)) {
                    minDistance = dist;
                    nextLieu = hotel;
                }
            }

            if (nextLieu != null) {
                route.add(nextLieu);
                visited.add(nextLieu.getId());
                currentId = nextLieu.getId();
            } else {
                break;
            }
        }

        // Retourner à l'aéroport
        route.add(airport);

        return route;
    }

    /**
     * Estime le temps total du trajet en minutes à partir des distances réelles
     * avec une vitesse moyenne fixe.
     */
    public static long estimateAirportReturnMinutes(List<Lieu> route) throws SQLException {
        if (route == null || route.size() < 2) {
            return 0L;
        }

        double totalDistanceKm = 0.0;

        for (int i = 0; i < route.size() - 1; i++) {
            Lieu from = route.get(i);
            Lieu to = route.get(i + 1);
            if (from == null || to == null) {
                continue;
            }

            double distance = LieuModel.getDistance(from.getId(), to.getId());

            // Fallback simple si la route inverse est la seule renseignée en base.
            if (distance == Double.MAX_VALUE) {
                distance = LieuModel.getDistance(to.getId(), from.getId());
            }

            if (distance != Double.MAX_VALUE) {
                totalDistanceKm += distance;
            }
        }

        return Math.max(0L, Math.round((totalDistanceKm / AVERAGE_SPEED_KMH) * 60.0));
    }

    /**
     * Met à jour la date de disponibilité d'un véhicule
     * Cette date représente quand le véhicule redevient entièrement libre,
     * en fonction de la date de départ et du temps estimé du trajet.
     * 
     * @param idVehicule       L'ID du véhicule
     * @param dateDepart       La date/heure de départ du trajet (format: YYYY-MM-DD
     *                         HH:MM:SS)
     * @param estimatedMinutes La durée estimée du trajet en minutes
     */
    public static void updateVehicleAvailability(int idVehicule, String dateDepart, long estimatedMinutes)
            throws SQLException {
        if (dateDepart == null) {
            return; // Ne pas mettre à jour si pas de date de départ
        }

        try {
            LocalDateTime departure = LocalDateTime.parse(dateDepart, DATE_TIME_FORMATTER);
            LocalDateTime availability = departure.plusMinutes(estimatedMinutes);
            String availabilityStr = availability.format(DATE_TIME_FORMATTER);

            String sql = "UPDATE vehicule SET date_disponibilite = ? WHERE id = ?";
            try (Connection c = DB.getConnection();
                    PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(availabilityStr));
                ps.setInt(2, idVehicule);
                ps.executeUpdate();

                System.out.println("Vehicle availability updated: vehicleId=" + idVehicule +
                        " newAvailability=" + availabilityStr);
            }
        } catch (Exception e) {
            System.err.println("Error updating vehicle availability: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Met à jour la date de disponibilité d'un véhicule avec une durée par défaut
     * Si la durée estimée n'est pas fournie, on utilise 30 minutes
     * 
     * @param idVehicule L'ID du véhicule
     * @param dateDepart La date/heure de départ du trajet
     */
    public static void updateVehicleAvailability(int idVehicule, String dateDepart)
            throws SQLException {
        updateVehicleAvailability(idVehicule, dateDepart, WAITING_TIME_MINUTES);
    }
}
