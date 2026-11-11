
package com.prehackaton.eventos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de conexión JDBC reutilizable en la capa util.
 *
 * Proporciona una conexión a la base de datos MySQL db_eventos
 * y métodos de cierre seguro de recursos JDBC.
 */
public final class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3307/db_eventos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USER = "root";
    private static final String PASSWORD = "secret123";

    private static Connection conn = null;

    private DBConnection() {
        // Clase de utilidad: no se instancia
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed() || !conn.isValid(3)) {
            cerrarConexion();
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return conn;
    }

    public static void cerrarConexion() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
            conn = null;
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(stmt);
        close(conn);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
        close(conn);
    }

    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
