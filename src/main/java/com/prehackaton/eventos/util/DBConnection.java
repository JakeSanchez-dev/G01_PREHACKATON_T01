package com.prehackaton.eventos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de conexión JDBC reutilizable en la capa util.
 *
 * Proporciona una conexión única a la base de datos MySQL db_eventos y
 * métodos de cierre seguro de recursos JDBC.
 *
 * NOTA: Las credenciales están definidas como constantes para facilitar la
 * prueba en el entorno del pre-hackatón. En entorno productivo se recomienda
 * externalizarlas (archivo .properties / variables de entorno).
 */
public final class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/db_eventos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "secret123";

    private static Connection conn = null;

    private DBConnection() {
        // Clase de utilidad: no se instancia
    }

    /**
     * Obtiene (y reutiliza) la conexión JDBC a la base de datos.
     *
     * @return Connection abierta
     * @throws SQLException si no se puede establecer la conexión
     */
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return conn;
    }

    /**
     * Cierra una conexión de forma segura.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Cierra una conexión y un statement de forma segura.
     */
    public static void close(Connection conn, Statement stmt) {
        close(stmt);
        close(conn);
    }

    /**
     * Cierra conexión, statement y resultSet de forma segura.
     */
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
