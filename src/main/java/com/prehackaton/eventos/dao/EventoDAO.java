package com.prehackaton.eventos.dao;

import com.prehackaton.eventos.model.Evento;

import java.util.List;

/**
 * Interfaz DAO para la entidad Evento.
 *
 * Todo el acceso a la base de datos reside exclusivamente en esta capa.
 * Los controladores y vistas NO contienen consultas SQL.
 */
public interface EventoDAO {

    /** Listar todos los eventos ordenados por id. */
    List<Evento> listar();

    /** Buscar un evento por su identificador. Retorna null si no existe. */
    Evento buscarPorId(Integer id);

    /** Insertar un nuevo evento (id auto-generado). */
    void insertar(Evento evento);

    /** Actualizar un evento existente por su id. */
    void actualizar(Evento evento);

    /** Eliminar un evento por su id (eliminación física). */
    void eliminar(Integer id);

    /** Verificar si ya existe un evento con el nombre indicado. */
    boolean existeNombre(String nombre);

    /**
     * Verificar si existe un evento con el nombre indicado, excluyendo el id
     * dado (útil al editar para no marcar duplicado al mismo registro).
     */
    boolean existeNombreExcluyendo(String nombre, Integer idExcluir);

    /** Liberar recursos de la conexión. */
    void cerrar();
}
