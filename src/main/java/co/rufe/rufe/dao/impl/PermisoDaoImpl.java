// C:\microservicio-rufe\rufe\src\main\java\co\rufe\rufe\dao\impl\PermisoDaoImpl.java
package co.rufe.rufe.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import co.rufe.rufe.dao.IPermisoDao;
import co.rufe.rufe.model.Permiso;
import co.rufe.rufe.util.CustomRowMappers;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PermisoDaoImpl implements IPermisoDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PermisoDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Optional<Permiso> findById(Long id) {
        String sql = "SELECT id, nombre_permiso, descripcion, recurso FROM permisos WHERE id = :id";
        try {
            var params = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource();
            params.addValue("id", id);
            return Optional.ofNullable(
                namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.PERMISO_ROW_MAPPER)
            );
        } catch (EmptyResultDataAccessException e) {
            log.warn("Permiso con ID {} no encontrado.", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Permiso> findByNombrePermiso(String nombrePermiso) {
        String sql = "SELECT id, nombre_permiso, descripcion, recurso FROM permisos WHERE nombre_permiso = :nombrePermiso";
        var params = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource();
        params.addValue("nombrePermiso", nombrePermiso);
        try {
            return Optional.ofNullable(
                namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.PERMISO_ROW_MAPPER)
            );
        } catch (EmptyResultDataAccessException e) {
            log.warn("Permiso con nombre {} no encontrado.", nombrePermiso);
            return Optional.empty();
        }
    }

    @Override
    public List<Permiso> findAll() {
        String sql = "SELECT id, nombre_permiso, descripcion, recurso FROM permisos";
        return namedParameterJdbcTemplate.query(sql, new org.springframework.jdbc.core.namedparam.MapSqlParameterSource(), CustomRowMappers.PERMISO_ROW_MAPPER);
    }

    @Override
    public List<Permiso> findByRolId(Long rolId) {
        String sql = "SELECT p.id, p.nombre_permiso, p.descripcion, p.recurso FROM permisos p " +
                     "JOIN rol_permisos rp ON p.id = rp.permiso_id " +
                     "WHERE rp.rol_id = :rolId";
        var params = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource();
        params.addValue("rolId", rolId);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.PERMISO_ROW_MAPPER);
    }

    @Override
    public Permiso save(Permiso permiso) {
        if (permiso.getId() == null) {
            String sql = "INSERT INTO permisos (nombre_permiso, descripcion, recurso) VALUES (:nombrePermiso, :descripcion, :recurso)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            var params = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource();
            params.addValue("nombrePermiso", permiso.getNombrePermiso());
            params.addValue("descripcion", permiso.getDescripcion());
            params.addValue("recurso", permiso.getRecurso());
            namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            permiso.setId(keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null);
        } else {
            String sql = "UPDATE permisos SET nombre_permiso = :nombrePermiso, descripcion = :descripcion, recurso = :recurso WHERE id = :id";
            var params = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource();
            params.addValue("nombrePermiso", permiso.getNombrePermiso());
            params.addValue("descripcion", permiso.getDescripcion());
            params.addValue("recurso", permiso.getRecurso());
            params.addValue("id", permiso.getId());
            namedParameterJdbcTemplate.update(sql, params);
        }
        return permiso;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM permisos WHERE id = :id";
        org.springframework.jdbc.core.namedparam.MapSqlParameterSource params = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource();
        params.addValue("id", id);
        namedParameterJdbcTemplate.update(sql, params);
    }
}
