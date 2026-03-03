package model; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Vehicule;
import util.DB;

public class VehiculeModel {

    /**
     * Retourne la liste de tous les véhicules en base.
     * Columns attendues : references, nbr_place, type
     */
    public static List<Vehicule> findAll() throws SQLException {
        List<Vehicule> list = new ArrayList<>();
        String sql = "SELECT \"references\" AS reference, nbr_place, type FROM vehicule";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String reference = rs.getString("reference");
                int nbrPlace = rs.getInt("nbr_place");
                String type = rs.getString("type");
                list.add(new Vehicule(reference, nbrPlace, type));
            }
        }
        return list;
    }

}