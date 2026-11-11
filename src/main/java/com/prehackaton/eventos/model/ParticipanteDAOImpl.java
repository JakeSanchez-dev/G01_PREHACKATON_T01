
package com.prehackaton.eventos.model;

import com.prehackaton.eventos.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipanteDAOImpl implements ParticipanteDAO {

    private final Connection conn;

    public ParticipanteDAOImpl() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    private Participante mapear(ResultSet rs) throws SQLException {
        return new Participante(
                rs.getInt("id_participante"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("dni"),
                rs.getString("correo"),
                rs.getString("telefono"),
                rs.getString("institucion")
        );
    }

    @Override
    public List<Participante> listar() throws SQLException {
        List<Participante> lista = new ArrayList<>();
        String sql = "SELECT * FROM participantes ORDER BY id_participante DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Participante> buscar(String criterio) throws SQLException {
        List<Participante> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM participantes
                WHERE nombres LIKE ?
                   OR apellidos LIKE ?
                   OR dni LIKE ?
                   OR institucion LIKE ?
                ORDER BY id_participante DESC
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String filtro = "%" + criterio + "%";
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, filtro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public Participante buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM participantes WHERE id_participante = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public boolean insertar(Participante p) throws SQLException {
        String sql = """
                INSERT INTO participantes
                (nombres, apellidos, dni, correo, telefono, institucion)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            establecerDatos(ps, p);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizar(Participante p) throws SQLException {
        String sql = """
                UPDATE participantes
                SET nombres = ?, apellidos = ?, dni = ?,
                    correo = ?, telefono = ?, institucion = ?
                WHERE id_participante = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            establecerDatos(ps, p);
            ps.setInt(7, p.getIdParticipante());
            return ps.executeUpdate() > 0;
        }
    }

    private void establecerDatos(PreparedStatement ps, Participante p)
            throws SQLException {
        ps.setString(1, p.getNombres());
        ps.setString(2, p.getApellidos());
        ps.setString(3, p.getDni());
        ps.setString(4, p.getCorreo());
        ps.setString(5, p.getTelefono());
        ps.setString(6, p.getInstitucion());
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM participantes WHERE id_participante = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existeDni(String dni) throws SQLException {
        String sql = "SELECT COUNT(*) FROM participantes WHERE dni = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public boolean existeDniExcluyendo(String dni, int id)
            throws SQLException {
        String sql = """
                SELECT COUNT(*) FROM participantes
                WHERE dni = ? AND id_participante <> ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}

