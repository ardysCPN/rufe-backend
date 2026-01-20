package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IRolDao;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.model.Rol;
import co.rufe.rufe.util.CustomRowMappers;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class RolDaoImpl implements IRolDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public RolDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Rol save(Rol rol) {
        String sql = "INSERT INTO roles (organizacion_id, nombre_rol, descripcion, fecha_creacion) " +
                     "VALUES (:organizacionId, :nombreRol, :descripcion, NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", rol.getOrganizacionId());
        params.addValue("nombreRol", rol.getNombreRol());
        params.addValue("descripcion", rol.getDescripcion());

        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        long newId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        rol.setId(newId);
        return rol;
    }

    @Override
    public Optional<Rol> findById(Long id) {
        String sql = "SELECT id, organizacion_id, nombre_rol, descripcion, fecha_creacion FROM roles WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.ROL_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Rol> findByOrganizacionIdAndNombreRol(Long organizacionId, String nombreRol) {
        String sql = "SELECT id, organizacion_id, nombre_rol, descripcion, fecha_creacion " +
                     "FROM roles WHERE organizacion_id = :organizacionId AND nombre_rol = :nombreRol";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", organizacionId); 
        params.addValue("nombreRol", nombreRol);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.ROL_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Rol> findByOrganizacionId(Long organizacionId) {
        String sql = "SELECT id, organizacion_id, nombre_rol, descripcion, fecha_creacion " +
                     "FROM roles WHERE organizacion_id = :organizacionId ORDER BY nombre_rol";
        MapSqlParameterSource params = new MapSqlParameterSource("organizacionId", organizacionId);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.ROL_ROW_MAPPER);
    }

    @Override
    public Rol update(Rol rol) {
        String sql = "UPDATE roles SET nombre_rol = :nombreRol, descripcion = :descripcion " +
                     "WHERE id = :id AND organizacion_id = :organizacionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombreRol", rol.getNombreRol());
        params.addValue("descripcion", rol.getDescripcion());
        params.addValue("id", rol.getId());
        params.addValue("organizacionId", rol.getOrganizacionId()); // Asegura que el rol pertenece a la organización correcta

        int rowsAffected = namedParameterJdbcTemplate.update(sql, params);
        if (rowsAffected == 0) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + rol.getId() + " para la organización: " + rol.getOrganizacionId());
        }
        return rol;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM roles WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM roles WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByOrganizacionIdAndNombreRol(Long organizacionId, String nombreRol) {
        String sql = "SELECT COUNT(*) FROM roles WHERE organizacion_id = :organizacionId AND nombre_rol = :nombreRol";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", organizacionId);
        params.addValue("nombreRol", nombreRol);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public Optional<Rol> findByNombreRolAndOrganizacionId(String nombre, Long organizacionId) {
        String sql = "SELECT id, organizacion_id, nombre_rol, descripcion, fecha_creacion " +
                     "FROM roles WHERE nombre_rol = :nombreRol AND organizacion_id = :organizacionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombreRol", nombre);
        params.addValue("organizacionId", organizacionId);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.ROL_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
