package co.rufe.rufe.service;

import co.rufe.rufe.model.AuditLog;
import java.util.List;

public interface IAuditLogService {
    void log(Long organizacionId, Long usuarioId, String accion, String recurso, String detalle, String ipAddress);

    List<AuditLog> getLogsByOrganizacion(Long organizacionId);

    /** Retorna todos los logs de todas las organizaciones. Solo para SUPERADMIN. */
    List<AuditLog> getAllLogs();
}

