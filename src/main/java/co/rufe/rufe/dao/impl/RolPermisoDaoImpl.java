// C:\microservicio-rufe\rufe\src\main\java\co\rufe\rufe\dao\impl\RolPermisoDaoImpl.java
package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IRolPermisoDao;
import co.rufe.rufe.model.RolPermiso;
import co.rufe.rufe.util.CustomRowMappers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RolPermisoDaoImpl implements IRolPermisoDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public RolPermisoDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void assignPermission(Long rolId, Integer permisoId) { // <-- Cambiado a Integer permisoId
        String sql = "INSERT INTO rol_permisos (rol_id, permiso_id) VALUES (:rolId, :permisoId) ON CONFLICT (rol_id, permiso_id) DO NOTHING";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rolId", rolId);
        params.addValue("permisoId", permisoId); // <-- Usando permisoId
        try {
            namedParameterJdbcTemplate.update(sql, params);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("No se pudo asignar el permiso. Verifique los IDs de rol y/o permiso. Puede que el permiso ya exista.", e);
        }
    }

    @Override
    public void revokePermission(Long rolId, Integer permisoId) { // <-- Cambiado a Integer permisoId
        String sql = "DELETE FROM rol_permisos WHERE rol_id = :rolId AND permiso_id = :permisoId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rolId", rolId);
        params.addValue("permisoId", permisoId); // <-- Usando permisoId
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<RolPermiso> findByRolId(Long rolId) {
        String sql = "SELECT rol_id, permiso_id FROM rol_permisos WHERE rol_id = :rolId";
        MapSqlParameterSource params = new MapSqlParameterSource("rolId", rolId);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.ROL_PERMISO_ROW_MAPPER);
    }

    @Override
    public boolean existsPermission(Long rolId, Integer permisoId) { // <-- Cambiado a Integer permisoId
        String sql = "SELECT COUNT(*) FROM rol_permisos WHERE rol_id = :rolId AND permiso_id = :permisoId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rolId", rolId);
        params.addValue("permisoId", permisoId); // <-- Usando permisoId
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean deleteByPermisoId(Integer permisoId) { // <-- Nuevo método
        String sql = "DELETE FROM rol_permisos WHERE permiso_id = :permisoId";
        MapSqlParameterSource params = new MapSqlParameterSource("permisoId", permisoId);
        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean deleteByRolId(Long rolId) { // <-- Nuevo método
        String sql = "DELETE FROM rol_permisos WHERE rol_id = :rolId";
        MapSqlParameterSource params = new MapSqlParameterSource("rolId", rolId);
        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }

}