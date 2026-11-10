package com.prehackaton.eventos.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad Evento del módulo Integrante 1.
 *
 * Campos: id_evento, nombre, descripcion, fecha, lugar, modalidad, estado.
 */
public class Evento {

    private Integer idEvento;
    private String nombre;
    private String descripcion;
    private LocalDate fecha;
    private String lugar;
    private String modalidad;
    private String estado;

    public Evento() {
    }

    public Evento(Integer idEvento, String nombre, String descripcion, LocalDate fecha,
                  String lugar, String modalidad, String estado) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.lugar = lugar;
        this.modalidad = modalidad;
        this.estado = estado;
    }

    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento)) return false;
        Evento evento = (Evento) o;
        return Objects.equals(idEvento, evento.idEvento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEvento);
    }

    @Override
    public String toString() {
        return "Evento{" +
                "idEvento=" + idEvento +
                ", nombre='" + nombre + '\'' +
                ", fecha=" + fecha +
                ", lugar='" + lugar + '\'' +
                ", modalidad='" + modalidad + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
