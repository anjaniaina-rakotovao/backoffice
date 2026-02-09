package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Entity.Hotel;
import config.DBConfig;

public class HotelRepository {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
    }

    public Hotel save(Hotel h) throws Exception {
        String sql = "INSERT INTO hotels(nom) VALUES(?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, h.getNom());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    h.setId(rs.getInt(1));
                }
            }
        }
        return h;
    }

    public List<Hotel> findAll() throws Exception {
        List<Hotel> list = new ArrayList<>();
        String sql = "SELECT id, nom FROM hotels";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Hotel h = new Hotel(rs.getInt("id"), rs.getString("nom"));
                list.add(h);
            }
        }
        return list;
    }
}
