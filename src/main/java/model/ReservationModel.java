package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import entity.Reservation;
import util.DB;

public class ReservationModel {

    /**
     * Retourne la liste de toutes les réservations en base.
     * Colonnes attendues : id, id_client, nbr_passager, date_heure_arrivee,
     * id_hotel_arrivee
     */
    public static List<Reservation> findAll() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT id, id_client, nbr_passager, date_heure_arrivee, id_hotel_arrivee FROM reservation ORDER BY date_heure_arrivee DESC";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String idClient = rs.getString("id_client");
                int nbrPassager = rs.getInt("nbr_passager");
                String dateHeureArrivee = rs.getTimestamp("date_heure_arrivee").toString();
                int idHotelArrivee = rs.getInt("id_hotel_arrivee");
                list.add(new Reservation(id, idClient, nbrPassager, dateHeureArrivee, idHotelArrivee));
            }
        }
        return list;
    }

    /**
     * Filtre les réservations par date (toutes les réservations d'un jour)
     * 
     * @param dateString La date à filtrer au format String (YYYY-MM-DD)
     */
    public static List<Reservation> findByDateString(String dateString) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT id, id_client, nbr_passager, date_heure_arrivee, id_hotel_arrivee " +
                "FROM reservation " +
                "WHERE DATE(date_heure_arrivee) = ? " +
                "ORDER BY date_heure_arrivee ASC";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(LocalDate.parse(dateString)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String idClient = rs.getString("id_client");
                    int nbrPassager = rs.getInt("nbr_passager");
                    String dateHeureArrivee = rs.getTimestamp("date_heure_arrivee").toString();
                    int idHotelArrivee = rs.getInt("id_hotel_arrivee");
                    list.add(new Reservation(id, idClient, nbrPassager, dateHeureArrivee, idHotelArrivee));
                }
            }
        }
        return list;
    }

    /**
     * Filtre les réservations par date (toutes les réservations d'un jour)
     * 
     * @param date La date à filtrer au format LocalDate
     */
    public static List<Reservation> findByDate(LocalDate date) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT id, id_client, nbr_passager, date_heure_arrivee, id_hotel_arrivee " +
                "FROM reservation " +
                "WHERE DATE(date_heure_arrivee) = ? " +
                "ORDER BY date_heure_arrivee ASC";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            // JDBC expects a SQL Date for DATE(...) comparisons
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String idClient = rs.getString("id_client");
                    int nbrPassager = rs.getInt("nbr_passager");
                    String dateHeureArrivee = rs.getTimestamp("date_heure_arrivee").toString();
                    int idHotelArrivee = rs.getInt("id_hotel_arrivee");
                    list.add(new Reservation(id, idClient, nbrPassager, dateHeureArrivee, idHotelArrivee));
                }
            }
        }
        return list;
    }

    /**
     * Trouve une réservation par son ID
     */
    public static Reservation findById(int id) throws SQLException {
        String sql = "SELECT id, id_client, nbr_passager, date_heure_arrivee, id_hotel_arrivee FROM reservation WHERE id = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String idClient = rs.getString("id_client");
                    int nbrPassager = rs.getInt("nbr_passager");
                    String dateHeureArrivee = rs.getTimestamp("date_heure_arrivee").toString();
                    int idHotelArrivee = rs.getInt("id_hotel_arrivee");
                    return new Reservation(id, idClient, nbrPassager, dateHeureArrivee, idHotelArrivee);
                }
            }
        }
        return null;
    }

    /**
     * Sauvegarde une nouvelle réservation en base
     */
    // public static void save(Reservation reservation) throws SQLException {
    // String sql = "INSERT INTO reservation (id_client, nbr_passager,
    // date_heure_arrivee, id_hotel_arrivee) VALUES (?, ?, ?, ?)";
    // try (Connection c = DB.getConnection();
    // PreparedStatement ps = c.prepareStatement(sql,
    // Statement.RETURN_GENERATED_KEYS)) {
    // ps.setString(1, reservation.getIdClient());
    // ps.setInt(2, reservation.getNbrPassager());
    // // Convertir la chaîne en Timestamp (format attendu: YYYY-MM-DD HH:MM:SS)
    // Timestamp ts = Timestamp.valueOf(reservation.getDateHeureArrivee());
    // ps.setTimestamp(3, ts);
    // ps.setInt(4, reservation.getIdHotelArrivee());
    // ps.executeUpdate();
    // try (ResultSet gk = ps.getGeneratedKeys()) {
    // if (gk.next()) {
    // reservation.setId(gk.getInt(1));
    // }
    // }

    // // Après insertion, tenter une assignation immédiate selon les règles de
    // gestion
    // try {
    // boolean assigned = AssignationModel.forceAssignReservation(reservation);
    // if (assigned) {
    // System.out.println("ReservationModel.save: assignation créée pour
    // reservation=" + reservation.getId());
    // } else {
    // System.out.println("ReservationModel.save: aucun véhicule correspondant pour
    // reservation=" + reservation.getId());
    // }
    // } catch (SQLException ex) {
    // System.out.println("ReservationModel.save: erreur lors de l'assignation
    // automatique: " + ex.getMessage());
    // // ne pas remonter l'exception pour ne pas bloquer l'enregistrement de la
    // réservation
    // }
    // }
    // }

    public static void save(Reservation reservation) throws SQLException {

        String sql = "INSERT INTO reservation (id_client, nbr_passager, date_heure_arrivee, id_hotel_arrivee) VALUES (?, ?, ?, ?)";

        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, reservation.getIdClient());
            ps.setInt(2, reservation.getNbrPassager());

            Timestamp ts = Timestamp.valueOf(reservation.getDateHeureArrivee());
            ps.setTimestamp(3, ts);

            ps.setInt(4, reservation.getIdHotelArrivee());

            ps.executeUpdate();

            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    reservation.setId(gk.getInt(1));
                }
            }
        }
    }

    /**
     * Met à jour une réservation existante
     */
    public static void update(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservation SET id_client = ?, nbr_passager = ?, date_heure_arrivee = ?, id_hotel_arrivee = ? WHERE id = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, reservation.getIdClient());
            ps.setInt(2, reservation.getNbrPassager());
            Timestamp ts = Timestamp.valueOf(reservation.getDateHeureArrivee());
            ps.setTimestamp(3, ts);
            ps.setInt(4, reservation.getIdHotelArrivee());
            ps.setInt(5, reservation.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Supprime une réservation par son ID
     */
    public static void delete(int id) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
