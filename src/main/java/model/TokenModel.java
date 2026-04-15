package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import util.DB;

public class TokenModel {

    /**
     * Vérifie si le token existe et que sa date d'expiration est dans le futur.
     */
    public static boolean isValid(String token) throws SQLException {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT date_expiration FROM token WHERE token = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp exp = rs.getTimestamp("date_expiration");
                    if (exp != null) {
                        return exp.after(new Timestamp(System.currentTimeMillis()));
                    }
                }
            }
        }
        return false;
    }
}
