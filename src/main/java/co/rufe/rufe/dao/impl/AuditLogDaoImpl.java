package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IAuditLogDao;
import co.rufe.rufe.model.AuditLog;
import co.rufe.rufe.util.CustomRowMappers;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditLogDaoImpl implements IAuditLogDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AuditLogDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void save(AuditLog auditLog) {
        String sql = "INSERT INTO audit_log (organizacion_id, usuario_id, accion, recurso, detalle, ip_address, fecha_creacion) "
                +
                "VALUES (:organizacionId, :usuarioId, :accion, :recurso, :detalle, :ipAddress, NOW())";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", auditLog.getOrganizacionId());
        params.addValue("usuarioId", auditLog.getUsuarioId());
        params.addValue("accion", auditLog.getAccion());
        params.addValue("recurso", auditLog.getRecurso());
        params.addValue("detalle", auditLog.getDetalle());
        params.addValue("ipAddress", auditLog.getIpAddress());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<AuditLog> findByOrganizacionId(Long organizacionId) {
        String sql = "SELECT id, organizacion_id, usuario_id, accion, recurso, detalle, ip_address, fecha_creacion " +
                "FROM audit_log WHERE organizacion_id = :organizacionId ORDER BY fecha_creacion DESC";
        MapSqlParameterSource params = new MapSqlParameterSource("organizacionId", organizacionId);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.AUDIT_LOG_ROW_MAPPER);
    }
}
