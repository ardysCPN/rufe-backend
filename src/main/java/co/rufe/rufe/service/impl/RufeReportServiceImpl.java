package co.rufe.rufe.service.impl;

import co.rufe.rufe.service.IRufeReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class RufeReportServiceImpl implements IRufeReportService {

    @Override
    public byte[] generarReporteRufe(Long eventoId) {
        try {
            // Cargar el JRXML
            InputStream jrxmlStream = getClass().getResourceAsStream("/reports/rufe_report.jrxml");

            // Compilar el reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

            // Parámetros
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("eventoId", eventoId);

            // Datos (en un caso real, obtendrías esto de la base de datos)
            // Por ahora, usamos un JRBeanCollectionDataSource vacío o datos de prueba
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(java.util.Collections.emptyList());

            // Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Exportar a PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            return outputStream.toByteArray();

        } catch (JRException e) {
            throw new RuntimeException("Error generando reporte PDF", e);
        }
    }

    @Override
    public byte[] generarReporteRufeExcel(Long eventoId) {
        try {
            // Cargar el JRXML
            InputStream jrxmlStream = getClass().getResourceAsStream("/reports/rufe_report.jrxml");

            // Compilar el reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

            // Parámetros
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("eventoId", eventoId);

            // Datos
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(java.util.Collections.emptyList());

            // Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Exportar a Excel
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();

            return outputStream.toByteArray();

        } catch (JRException e) {
            throw new RuntimeException("Error generando reporte Excel", e);
        }
    }
}
