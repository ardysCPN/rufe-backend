package co.rufe.rufe.service;

public interface IRufeReportService {

    byte[] generarReporteRufe(Long eventoId);

    byte[] generarReporteRufeExcel(Long organizacionId, boolean isAdmin);

}
