package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Entity.Reservation;
import config.DBConfig;

public class ReservationRepository {

    static {
        try { Class.forName("org.postgresql.Driver"); } catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
    }

    public Reservation save(Reservation r) throws Exception {
        String sql = "INSERT INTO reservations(idclient, nombre_passagers, dateheure_arrivee, hotel_arrivee) VALUES(?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getIdClient());
            ps.setInt(2, r.getNombrePassagers());
            // dateHeureArrivee expected as 'yyyy-MM-dd HH:mm:ss'
            try {
                Timestamp ts = Timestamp.valueOf(r.getDateHeureArrivee());
                ps.setTimestamp(3, ts);
            } catch (Exception ex) {
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            }
            if (r.getHotelArrivee() == null) ps.setObject(4, null);
            else ps.setInt(4, r.getHotelArrivee());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
        }
        return r;
    }

    public List<Reservation> findAll() throws Exception {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT id, idclient, nombre_passagers, dateheure_arrivee, hotel_arrivee FROM reservations";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Reservation r = new Reservation(rs.getInt("id"), rs.getString("idclient"), rs.getInt("nombre_passagers"), rs.getTimestamp("dateheure_arrivee").toString(), (Integer) rs.getObject("hotel_arrivee"));
                list.add(r);
            }
        }
        return list;
    }
}
