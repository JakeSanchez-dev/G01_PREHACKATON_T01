-- =============================================================================
-- Script SQL - Módulo Integrante 1: Gestión de Eventos
-- Base de datos: db_eventos
-- Tabla: eventos
-- =============================================================================

-- Crear la base de datos (si no existe) con codificación UTF-8
CREATE DATABASE IF NOT EXISTS db_eventos
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE db_eventos;

-- Tabla de eventos
-- id_evento : PK auto-incremental
-- nombre    : obligatorio y único (control de duplicados)
-- descripcion, fecha, lugar, modalidad, estado : datos del evento
CREATE TABLE IF NOT EXISTS eventos (
    id_evento   INT NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha       DATE NOT NULL,
    lugar       VARCHAR(100),
    modalidad   VARCHAR(50),
    estado      VARCHAR(50),
    PRIMARY KEY (id_evento),
    UNIQUE KEY uk_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos de ejemplo para pruebas del reporte y del listado
INSERT INTO eventos (nombre, descripcion, fecha, lugar, modalidad, estado) VALUES
('Conferencia de JavaFX 2026', 'Jornada técnica sobre interfaces ricas con JavaFX.', '2026-11-15', 'Auditorio Principal', 'Presencial', 'Activo'),
('Meetup de Bases de Datos', 'Encuentro mensual de desarrolladores MySQL y PostgreSQL.', '2026-11-22', 'Sala de Conferencias B', 'Presencial', 'Activo'),
('Webinar: Arquitectura MVC + DAO', 'Sesión online sobre buenas prácticas de capas en Java.', '2026-12-01', 'Online', 'Virtual', 'Programado'),
('Hackatón Nacional de Software', 'Competencia de desarrollo de 48 horas.', '2027-01-10', 'Centro de Convenciones', 'Presencial', 'Activo'),
('Capacitación JasperReports', 'Taller de generación de reportes con JasperReports.', '2027-02-05', 'Aula de Computación', 'Híbrido', 'Activo'),
('Seminario de Ciberseguridad', 'Charlas sobre seguridad en aplicaciones web.', '2026-10-20', 'Salón de Eventos', 'Presencial', 'Completado');

-- =============================================================================
-- Fin del script
-- =============================================================================
