package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import entity.Assignation;
import entity.Reservation;
import entity.Vehicule;
import util.DB;

public class AssignationModel {

    /**
     * Récupère toutes les assignations pour une date donnée
     */
    public static List<Assignation> findByDate(String date) throws SQLException {
        List<Assignation> list = new ArrayList<>();
        // Find assignations for reservations occurring on the given date.
        // We join with reservation and compare DATE(reservation.date_heure_arrivee) = ?
        String sql = "SELECT a.id, a.id_vehicule, a.id_reservation, a.date_assignation, a.date_depart " +
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
                            rs.getString("date_assignation"),
                            rs.getString("date_depart")));
                }
            }
        }
        return list;
    }

    /**
     * Récupère les véhicules disponibles à une date/heure donnée
     * Un véhicule est disponible s'il n'est pas assigné à une réservation à ce
     * moment
     */
    public static List<Vehicule> findAvailableVehicles(String dateHeureArrivee, int nbrPassager) throws SQLException {
        List<Vehicule> list = new ArrayList<>();
        String sql = "SELECT v.id, v.reference, v.nbr_place, v.type " +
                "FROM vehicule v " +
                "WHERE v.nbr_place >= ? " +
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Vehicule(
                            rs.getInt("id"),
                            rs.getString("reference"),
                            rs.getInt("nbr_place"),
                            rs.getString("type")));
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
        String sql = "INSERT INTO assignation (id_vehicule, id_reservation, date_depart) VALUES (?, ?, ?)";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idVehicule);
            ps.setInt(2, idReservation);

            if (dateDepart == null) {
                ps.setNull(3, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(3, Timestamp.valueOf(dateDepart));
            }

            ps.executeUpdate();

            System.out.println("Assignation créée: vehicule=" + idVehicule + " reservation=" + idReservation);
        }
    }

    /**
     * Assigne automatiquement les véhicules aux réservations d'une date donnée
     * Applique les règles de sélection:
     * 1. Minimal places restants
     * 2. Si égal, prendre diesel
     * 3. Si tous diesel, prendre random
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
        // Récupère et trie les réservations par nombre de passagers décroissant (plus
        // grand groupe en premier)
        List<Reservation> reservations = ReservationModel.findByDateString(date);
        reservations.sort((a, b) -> b.getNbrPassager() - a.getNbrPassager());
        System.out.println("autoAssignVehicles: " + reservations.size()
                + " réservations pour date=" + date + " (triées par nbr_passager DESC)");

        // Récupère tous les véhicules une seule fois
        List<Vehicule> allVehicles = VehiculeModel.findAll();
        // Charge initiale: places déjà occupées par des réservations déjà assignées ce jour.
        Map<Integer, Integer> vehicleLoad = getVehiclePassengerLoad(date);

        for (Reservation res : reservations) {
            // Vérifie si cette réservation a déjà une assignation
            Assignation existing = findByReservation(res.getId());
            if (existing != null) {
                System.out.println("Reservation " + res.getId() + " déjà assignée -> skip");
                continue;
            }

            // Règle prioritaire: remplir d'abord les véhicules déjà entamés.
            Vehicule selected = findPartiallyFilledVehicle(allVehicles, vehicleLoad, res.getNbrPassager());
            if (selected == null) {
                // Aucun véhicule partiellement rempli ne convient: on ouvre alors la sélection globale.
                selected = selectBestVehicleByRemainingSeats(allVehicles, vehicleLoad, res.getNbrPassager());
            }
            System.out.println("Reservation " + res.getId() + " nbrPassager=" + res.getNbrPassager()
                    + " véhicule sélectionné=" + (selected != null ? selected.getId() : "null"));

            if (selected == null) {
                System.out.println("Aucun véhicule avec places disponibles pour réservation " + res.getId());
                continue;
            }

            assignVehicle(selected.getId(), res.getId(), res.getDateHeureArrivee());
            vehicleLoad.put(selected.getId(), vehicleLoad.getOrDefault(selected.getId(), 0) + res.getNbrPassager());
        }
    }

    /**
     * Retourne la charge (total passagers assignés) par véhicule
     * pour les réservations arrivées à la date donnée.
     */
    private static Map<Integer, Integer> getVehiclePassengerLoad(String date) throws SQLException {
        Map<Integer, Integer> load = new HashMap<>();
        String sql = "SELECT a.id_vehicule, SUM(r.nbr_passager) AS total_passagers " +
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

    /**
     * Parmi les véhicules déjà utilisés ce jour (charge > 0), retourne celui
     * qui peut encore accueillir nbrPassager supplémentaires et dont la place
     * restante après ajout est minimale (best-fit). Retourne null si aucun ne
     * convient.
     */
    private static Vehicule findPartiallyFilledVehicle(List<Vehicule> allVehicles,
            Map<Integer, Integer> vehicleLoad, int nbrPassager) {
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

        return pickByTypeThenRandom(bestCandidates);
    }

    /**
     * Sélectionne le meilleur véhicule en tenant compte des places déjà occupées.
     * - véhicule admissible: capacité restante >= nbrPassager
     * - priorité: places restantes minimales après affectation
     * - tie-break: diesel, puis aléatoire
     */
    private static Vehicule selectBestVehicleByRemainingSeats(List<Vehicule> allVehicles,
            Map<Integer, Integer> vehicleLoad, int nbrPassager) {
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

        if (candidates.isEmpty()) {
            return null;
        }
        return pickByTypeThenRandom(candidates);
    }

    /**
     * Départage final: préférence diesel, puis tirage aléatoire.
     */
    private static Vehicule pickByTypeThenRandom(List<Vehicule> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        List<Vehicule> dieselCandidates = new ArrayList<>();
        for (Vehicule v : candidates) {
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
        return candidates.get(rnd.nextInt(candidates.size()));
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
        // Vérifie si cette réservation a déjà une assignation
        Assignation existing = findByReservation(res.getId());
        if (existing != null) {
            System.out.println("autoAssignForReservation: already assigned -> " + existing.getId());
            return; // déjà assignée
        }

        String date = res.getDateHeureArrivee().substring(0, 10);
        List<Vehicule> allVehicles = VehiculeModel.findAll();
        Map<Integer, Integer> vehicleLoad = getVehiclePassengerLoad(date);
        Vehicule selected = selectBestVehicleByRemainingSeats(allVehicles, vehicleLoad, res.getNbrPassager());

        System.out.println("autoAssignForReservation: selected=" + (selected != null ? selected.getId() : "null"));
        if (selected != null) {
            // 🚨 Correction : passer la date de réservation comme date_depart
            assignVehicle(selected.getId(), res.getId(), res.getDateHeureArrivee());
        } else {
            System.out.println("autoAssignForReservation: no vehicle with remaining seats for reservation " + res.getId());
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
        List<Vehicule> allVehicles = VehiculeModel.findAll();
        Map<Integer, Integer> vehicleLoad = getVehiclePassengerLoad(date);
        Vehicule selected = selectBestVehicleByRemainingSeats(allVehicles, vehicleLoad, res.getNbrPassager());

        if (selected != null) {
            // 🚨 Correction : passer la date de réservation comme date_depart
            assignVehicle(selected.getId(), res.getId(), res.getDateHeureArrivee());
            System.out.println("forceAssignReservation: assignation created veh="
                    + selected.getId() + " res=" + res.getId());
            return true;
        }

        System.out.println("forceAssignReservation: aucun véhicule avec places disponibles pour res="
                + res.getId());

        return false;
    }

    /**
     * Récupère l'assignation pour une réservation
     */
    public static Assignation findByReservation(int idReservation) throws SQLException {
        String sql = "SELECT id, id_vehicule, id_reservation, date_assignation, date_depart " +
                "FROM assignation WHERE id_reservation = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idReservation);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Assignation(
                            rs.getInt("id"),
                            rs.getInt("id_vehicule"),
                            rs.getInt("id_reservation"),
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
        String sql = "SELECT id, id_vehicule, id_reservation, date_assignation, date_depart " +
                "FROM assignation " +
                "WHERE id_vehicule = ? AND DATE(date_assignation) = ? " +
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
                            rs.getString("date_assignation"),
                            rs.getString("date_depart")));
                }
            }
        }
        return list;
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
}
