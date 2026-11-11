package com.prehackaton.eventos.model;

import com.prehackaton.eventos.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link EventoDAO}.
 *
 * Todas las consultas SQL están contenidas exclusivamente en esta clase.
 */
public class EventoDAOImpl implements EventoDAO {

    private final Connection conn;

    public EventoDAOImpl() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    @Override
    public List<Evento> listar() {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT id_evento, nombre, descripcion, fecha, lugar, modalidad, estado "
                + "FROM eventos ORDER BY id_evento";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearEvento(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar eventos", e);
        }
        return lista;
    }

    @Override
    public Evento buscarPorId(Integer id) {
        String sql = "SELECT id_evento, nombre, descripcion, fecha, lugar, modalidad, estado "
                + "FROM eventos WHERE id_evento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEvento(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar evento por id", e);
        }
        return null;
    }

    @Override
    public void insertar(Evento evento) {
        validarEvento(evento);
        String sql = "INSERT INTO eventos (nombre, descripcion, fecha, lugar, modalidad, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, evento.getNombre());
            ps.setString(2, evento.getDescripcion());
            ps.setDate(3, evento.getFecha() != null
                    ? java.sql.Date.valueOf(evento.getFecha()) : null);
            ps.setString(4, evento.getLugar());
            ps.setString(5, evento.getModalidad());
            ps.setString(6, evento.getEstado());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo insertar el evento, no se afectaron filas.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    evento.setIdEvento(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar evento", e);
        }
    }

    @Override
    public void actualizar(Evento evento) {
        if (evento.getIdEvento() == null) {
            throw new IllegalArgumentException("El evento debe tener un id para actualizarse.");
        }
        validarEvento(evento);
        String sql = "UPDATE eventos SET nombre = ?, descripcion = ?, fecha = ?, lugar = ?, "
                + "modalidad = ?, estado = ? WHERE id_evento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, evento.getNombre());
            ps.setString(2, evento.getDescripcion());
            ps.setDate(3, evento.getFecha() != null
                    ? java.sql.Date.valueOf(evento.getFecha()) : null);
            ps.setString(4, evento.getLugar());
            ps.setString(5, evento.getModalidad());
            ps.setString(6, evento.getEstado());
            ps.setInt(7, evento.getIdEvento());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se encontró el evento con id " + evento.getIdEvento());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar evento", e);
        }
    }

    @Override
    public void eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del evento no puede ser nulo.");
        }
        String sql = "DELETE FROM eventos WHERE id_evento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se encontró el evento con id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar evento", e);
        }
    }

    @Override
    public boolean existeNombre(String nombre) {
        return existeNombreConExclusion(nombre, null);
    }

    @Override
    public boolean existeNombreExcluyendo(String nombre, Integer idExcluir) {
        return existeNombreConExclusion(nombre, idExcluir);
    }

    private boolean existeNombreConExclusion(String nombre, Integer idExcluir) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        String sql;
        if (idExcluir == null) {
            sql = "SELECT COUNT(*) FROM eventos WHERE nombre = ?";
        } else {
            sql = "SELECT COUNT(*) FROM eventos WHERE nombre = ? AND id_evento <> ?";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            if (idExcluir != null) {
                ps.setInt(2, idExcluir);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar duplicidad de nombre de evento", e);
        }
        return false;
    }

    private void validarEvento(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo.");
        }
        if (evento.getNombre() == null || evento.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del evento es obligatorio.");
        }
        if (evento.getFecha() == null) {
            throw new IllegalArgumentException("La fecha del evento es obligatoria.");
        }
    }

    private Evento mapearEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setIdEvento(rs.getInt("id_evento"));
        evento.setNombre(rs.getString("nombre"));
        evento.setDescripcion(rs.getString("descripcion"));
        Date fechaSql = rs.getDate("fecha");
        evento.setFecha(fechaSql != null ? fechaSql.toLocalDate() : null);
        evento.setLugar(rs.getString("lugar"));
        evento.setModalidad(rs.getString("modalidad"));
        evento.setEstado(rs.getString("estado"));
        return evento;
    }

    @Override
    public void cerrar() {
        // La conexión es ÚNICA y compartida por todos los módulos de la
        // aplicación (Eventos y Participantes): cerrarla aquí rompería el
        // CRUD de las demás pestañas. Se cierra al finalizar la aplicación.
    }
}
