package util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DB {
    private static DataSource ds;

    static {
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/ProjDB");
        } catch (NamingException e) {
            // Pas de log framework dans ce projet minimal -> printStackTrace
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (ds == null) {
            throw new SQLException("DataSource non initialisé via JNDI: jdbc/ProjDB");
        }
        return ds.getConnection();
    }
}
