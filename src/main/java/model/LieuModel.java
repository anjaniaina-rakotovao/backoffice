package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Lieu;
import util.DB;

public class LieuModel {

    /**
     * Retourne la liste des lieux dont le libelle contient 'hotel' (insensible à la casse)
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
}
