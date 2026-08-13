package co.rufe.rufe.service.impl;

import co.rufe.rufe.dao.IAuditLogDao;
import co.rufe.rufe.model.AuditLog;
import co.rufe.rufe.service.IAuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AuditLogServiceImpl implements IAuditLogService {

    private final IAuditLogDao auditLogDao;

    public AuditLogServiceImpl(IAuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    @Override
    public void log(Long organizacionId, Long usuarioId, String accion, String recurso, String detalle,
            String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .organizacionId(organizacionId)
                .usuarioId(usuarioId)
                .accion(accion)
                .recurso(recurso)
                .detalle(detalle)
                .ipAddress(ipAddress)
                .build();

        try {
            auditLogDao.save(auditLog);
        } catch (Exception e) {
            log.error("Error saving audit log: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> getLogsByOrganizacion(Long organizacionId) {
        return auditLogDao.findByOrganizacionId(organizacionId);
    }

    @Override
    public List<AuditLog> getAllLogs() {
        return auditLogDao.findAll();
    }
}

