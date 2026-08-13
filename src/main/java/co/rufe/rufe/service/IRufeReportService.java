package co.rufe.rufe.service;

public interface IRufeReportService {

    byte[] generarReporteRufe(Long organizacionId, boolean isAdmin);

    byte[] generarReporteRufeExcel(Long organizacionId, boolean isAdmin);

}

