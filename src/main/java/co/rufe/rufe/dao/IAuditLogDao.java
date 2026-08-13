package co.rufe.rufe.dao;

import co.rufe.rufe.model.AuditLog;
import java.util.List;

public interface IAuditLogDao {
    void save(AuditLog auditLog);

    List<AuditLog> findByOrganizacionId(Long organizacionId);

    /** Retorna todos los logs sin filtro de organización. Solo para SUPERADMIN. */
    List<AuditLog> findAll();
}

