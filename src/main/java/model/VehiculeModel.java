package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import entity.Vehicule;
import util.DB;

public class VehiculeModel {

    /**
     * Retourne la liste de tous les véhicules en base.
     * Columns attendues : id, reference, nbr_place, type, date_disponibilite
     */
    public static List<Vehicule> findAll() throws SQLException {
        List<Vehicule> list = new ArrayList<>();
        String sql = "SELECT id, reference, nbr_place, type, COALESCE(date_disponibilite, '1900-01-01 00:00:00') AS date_disponibilite FROM vehicule";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String reference = rs.getString("reference");
                int nbrPlace = rs.getInt("nbr_place");
                String type = rs.getString("type");
                String dateDisponibilite = rs.getTimestamp("date_disponibilite").toString();
                list.add(new Vehicule(id, reference, nbrPlace, type, dateDisponibilite));
            }
        }
        return list;
    }

    /**
     * Trouve un véhicule par son ID
     */
    public static Vehicule findById(int id) throws SQLException {
        String sql = "SELECT id, reference, nbr_place, type, COALESCE(date_disponibilite, '1900-01-01 00:00:00') AS date_disponibilite FROM vehicule WHERE id = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String reference = rs.getString("reference");
                    int nbrPlace = rs.getInt("nbr_place");
                    String type = rs.getString("type");
                    String dateDisponibilite = rs.getTimestamp("date_disponibilite").toString();
                    return new Vehicule(id, reference, nbrPlace, type, dateDisponibilite);
                }
            }
        }
        return null;
    }

    /**
     * Sauvegarde un nouveau véhicule en base
     */
    public static void save(Vehicule vehicule) throws SQLException {
        String sql = "INSERT INTO vehicule (reference, nbr_place, type, date_disponibilite) VALUES (?, ?, ?, ?)";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vehicule.getReference());
            ps.setInt(2, vehicule.getNbrPlace());
            ps.setString(3, vehicule.getType());
            ps.setTimestamp(4, Timestamp.valueOf(vehicule.getDateDisponibilite()));
            ps.executeUpdate();
        }
    }

    /**
     * Met à jour un véhicule existant
     */
    public static void update(Vehicule vehicule) throws SQLException {
        String sql = "UPDATE vehicule SET reference = ?, nbr_place = ?, type = ?, date_disponibilite = ? WHERE id = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, vehicule.getReference());
            ps.setInt(2, vehicule.getNbrPlace());
            ps.setString(3, vehicule.getType());
            ps.setTimestamp(4, Timestamp.valueOf(vehicule.getDateDisponibilite()));
            ps.setInt(5, vehicule.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Supprime un véhicule par son ID
     */
    public static void delete(int id) throws SQLException {
        String sql = "DELETE FROM vehicule WHERE id = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

}