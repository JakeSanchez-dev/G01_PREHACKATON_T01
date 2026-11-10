package com.prehackaton.eventos.util;

import com.prehackaton.eventos.model.Evento;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Utilidad para ejecutar el reporte de JasperReports "Reporte General de
 * Eventos y Estados".
 *
 * Los datos se obtienen desde la capa DAO (una List de Eventos) y se pasan
 * al reporte mediante un JRBeanCollectionDataSource, por lo que el reporte
 * no contiene consultas SQL.
 */
public final class ReportUtil {

    private static final String REPORT_JRXML_PATH =
            "/com/prehackaton/eventos/report/reporte_eventos.jrxml";

    private ReportUtil() {
        // Clase de utilidad: no se instancia
    }

    /**
     * Compila el .jrxml, lo llena con la lista de eventos y lo muestra en
     * JasperViewer para visualización directa desde JavaFX.
     *
     * @param eventos lista de eventos a reportar
     * @throws JRException si hay error compilando o llenando el reporte
     */
    public static void mostrarReporte(List<Evento> eventos) throws JRException {
        InputStream jrxmlStream = ReportUtil.class.getResourceAsStream(REPORT_JRXML_PATH);
        if (jrxmlStream == null) {
            throw new JRException("No se encontró el recurso del reporte: " + REPORT_JRXML_PATH);
        }

        JasperReport reporte = JasperCompileManager.compileReport(jrxmlStream);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("REPORT_TITLE", "Reporte General de Eventos y Estados");
        parametros.put("SUBTITLE", "Módulo Integrante 1 - Gestión de Eventos");

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(eventos);
        JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, dataSource);

        // JasperViewer abre una ventana independiente (no bloquea la aplicación)
        JasperViewer viewer = new JasperViewer(jasperPrint, false);
        viewer.setTitle("Reporte General de Eventos y Estados");
        viewer.setVisible(true);
    }

    /**
     * Compila el .jrxml, lo llena con la lista de eventos y lo exporta a PDF.
     *
     * @param eventos  lista de eventos a reportar
     * @param outPath  ruta destino del archivo PDF
     * @throws JRException si hay error compilando, llenando o exportando
     */
    public static void exportarReporteAPdf(List<Evento> eventos, String outPath) throws JRException {
        InputStream jrxmlStream = ReportUtil.class.getResourceAsStream(REPORT_JRXML_PATH);
        if (jrxmlStream == null) {
            throw new JRException("No se encontró el recurso del reporte: " + REPORT_JRXML_PATH);
        }

        JasperReport reporte = JasperCompileManager.compileReport(jrxmlStream);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("REPORT_TITLE", "Reporte General de Eventos y Estados");
        parametros.put("SUBTITLE", "Módulo Integrante 1 - Gestión de Eventos");

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(eventos);
        JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, dataSource);

        JasperExportManager.exportReportToPdfFile(jasperPrint, outPath);
    }
}
