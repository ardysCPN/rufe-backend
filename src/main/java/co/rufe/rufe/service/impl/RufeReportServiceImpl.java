package co.rufe.rufe.service.impl;

import co.rufe.rufe.dao.IRegistroRufeDao;
import co.rufe.rufe.service.IRufeReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RufeReportServiceImpl implements IRufeReportService {
    private final IRegistroRufeDao registroRufeDao;

    public RufeReportServiceImpl(IRegistroRufeDao registroRufeDao) {
        this.registroRufeDao = registroRufeDao;
    }

    @Override
    public byte[] generarReporteRufe(Long eventoId) {
        try {
            InputStream jrxmlStream = getClass().getResourceAsStream("/reports/rufe_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("eventoId", eventoId);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(java.util.Collections.emptyList());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return outputStream.toByteArray();
        } catch (JRException e) {
            throw new RuntimeException("Error generando reporte PDF", e);
        }
    }

    @Override
    public byte[] generarReporteRufeExcel(Long organizacionId, boolean isAdmin) {
        List<Map<String, Object>> datos = registroRufeDao.obtenerDatosReporteExcel(organizacionId, isAdmin);

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            SXSSFSheet sheet = workbook.createSheet("Registros RUFE");

            if (!datos.isEmpty()) {
                // Escribir cabeceras basados en las claves del primer mapa devuelto por SQL
                int rowNum = 0;
                Row header = sheet.createRow(rowNum++);
                int colIndex = 0;
                for (String key : datos.get(0).keySet()) {
                    header.createCell(colIndex++).setCellValue(key);
                }

                // Volcar datos
                for (Map<String, Object> fila : datos) {
                    Row row = sheet.createRow(rowNum++);
                    colIndex = 0;
                    for (Object val : fila.values()) {
                        row.createCell(colIndex++).setCellValue(val != null ? val.toString() : "");
                    }
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.dispose();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando el listado Excel desde datos", e);
        }
    }
}
