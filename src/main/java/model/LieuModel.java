package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Lieu;
import util.DB;

public class LieuModel {

    /**
     * Retourne la liste des lieux dont le libelle contient 'hotel' (insensible à la
     * casse)
     */
    public static List<Lieu> findHotels() throws SQLException {
        List<Lieu> list = new ArrayList<>();
        String sql = "SELECT id, libelle, code FROM lieu WHERE LOWER(libelle) LIKE ? ORDER BY libelle";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%hotel%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String libelle = rs.getString("libelle");
                    String code = rs.getString("code");
                    list.add(new Lieu(id, libelle, code));
                }
            }
        }
        // Si aucun lieu contenant 'hotel' n'a été trouvé, retourner tous les lieux
        if (list.isEmpty()) {
            String sqlAll = "SELECT id, libelle, code FROM lieu ORDER BY libelle";
            try (Connection c = DB.getConnection();
                    PreparedStatement ps = c.prepareStatement(sqlAll);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String libelle = rs.getString("libelle");
                    String code = rs.getString("code");
                    list.add(new Lieu(id, libelle, code));
                }
            }
        }
        return list;
    }

    /**
     * Récupère un lieu par son ID
     */
    public static Lieu findById(int id) throws SQLException {
        String sql = "SELECT id, libelle, code FROM lieu WHERE id = ?";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Lieu(rs.getInt("id"), rs.getString("libelle"), rs.getString("code"));
                }
            }
        }
        return null;
    }

    /**
     * Retourne tous les lieux
     */
    public static List<Lieu> findAll() throws SQLException {
        List<Lieu> list = new ArrayList<>();
        String sql = "SELECT id, libelle, code FROM lieu ORDER BY libelle";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Lieu(rs.getInt("id"), rs.getString("libelle"), rs.getString("code")));
            }
        }
        return list;
    }

    /**
     * Retourne le lieu aéroport (le premier matching 'aeroport' ou 'airport',
     * case-insensitive)
     */
    public static Lieu findAirport() throws SQLException {
        String sql = "SELECT id, libelle, code FROM lieu WHERE LOWER(libelle) LIKE ? OR LOWER(code) LIKE ? LIMIT 1";
        try (Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%aeroport%");
            ps.setString(2, "%aeroport%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Lieu(rs.getInt("id"), rs.getString("libelle"), rs.getString("code"));
                }
            }
        }
        return null;
    }

    /**
     * Retourne la distance entre deux lieux (en km)
     */
    public static double getDistance(int idFrom, int idTo) throws SQLException {
        String sql = "SELECT distance_km FROM distance WHERE id_from = ? AND id_to = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFrom);
            ps.setInt(2, idTo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("distance_km");
                }
            }
        }
        return Double.MAX_VALUE; // Pas de route directe
    }

    /**
     * Retourne toutes les distances depuis un lieu donné
     */
    public static Map<Integer, Double> getDistancesFrom(int idFrom) throws SQLException {
        Map<Integer, Double> distances = new HashMap<>();
        String sql = "SELECT id_to, distance_km FROM distance WHERE id_from = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFrom);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    distances.put(rs.getInt("id_to"), rs.getDouble("distance_km"));
                }
            }
        }
        return distances;
    }
}
