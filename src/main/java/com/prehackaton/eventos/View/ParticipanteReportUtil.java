
package com.prehackaton.eventos.util;

import com.prehackaton.eventos.model.Participante;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperReport;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipanteReportUtil {

    private static final String RUTA_REPORTE =
            "/com/prehackaton/eventos/report/reporte_participantes.jrxml";

    private static JasperPrint generarReporte(List<Participante> participantes)
            throws JRException {

        InputStream archivo =
                ParticipanteReportUtil.class.getResourceAsStream(RUTA_REPORTE);

        if (archivo == null) {
            throw new JRException(
                    "No se encontró el archivo reporte_participantes.jrxml"
            );
        }

        try (InputStream input = archivo) {

            JasperReport reporte =
                    JasperCompileManager.compileReport(input);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(
                    "TITULO",
                    "REPORTE DE PARTICIPANTES POR INSTITUCIÓN"
            );

            JRBeanCollectionDataSource datos =
                    new JRBeanCollectionDataSource(participantes);

            return JasperFillManager.fillReport(
                    reporte,
                    parametros,
                    datos
            );
        } catch (java.io.IOException e) {
            throw new JRException("Error al leer el archivo del reporte", e);
        }
    }

    // Mostrar el reporte en JasperViewer
    public static void mostrarReporte(List<Participante> participantes)
            throws JRException {

        JasperPrint impresion = generarReporte(participantes);

        JasperViewer.viewReport(impresion, false);
    }

    // Exportar el reporte a PDF
    public static void exportarPDF(
            List<Participante> participantes,
            String ruta
    ) throws JRException {

        JasperPrint impresion = generarReporte(participantes);

        JasperExportManager.exportReportToPdfFile(
                impresion,
                ruta
        );
    }
}
