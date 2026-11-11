
package com.prehackaton.eventos.model;

import java.util.List;

public interface ParticipanteDAO {

    List<Participante> listar() throws Exception;

    List<Participante> buscar(String criterio) throws Exception;

    Participante buscarPorId(int id) throws Exception;

    boolean insertar(Participante participante) throws Exception;

    boolean actualizar(Participante participante) throws Exception;

    boolean eliminar(int id) throws Exception;

    boolean existeDni(String dni) throws Exception;

    boolean existeDniExcluyendo(String dni, int id) throws Exception;
}

