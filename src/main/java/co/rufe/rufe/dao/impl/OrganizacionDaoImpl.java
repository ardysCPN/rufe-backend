package co.rufe.rufe.dao.impl;


import co.rufe.rufe.dao.IOrganizacionDao;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.model.Organizacion;
import co.rufe.rufe.util.CustomRowMappers;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class OrganizacionDaoImpl implements IOrganizacionDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate; // Para operaciones simples sin parámetros nombrados

    public OrganizacionDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Organizacion save(Organizacion organizacion) {
        String sql = "INSERT INTO organizaciones (nombre_organizacion, activa, fecha_creacion, fecha_actualizacion) " +
                     "VALUES (:nombreOrganizacion, :activa, NOW(), NOW())";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombreOrganizacion", organizacion.getNombreOrganizacion());
        params.addValue("activa", organizacion.getActiva() != null ? organizacion.getActiva() : true, Types.BOOLEAN);

        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        long newId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        organizacion.setId(newId);
        // La entidad en Java no tiene las fechas actualizadas al momento de la inserción,
        // pero la BD sí las maneja. Para tener una entidad completa, se puede hacer un findById() aquí.
        // Por simplicidad, solo seteamos el ID y asumimos que las fechas se manejarán en la BD.
        return organizacion;
    }

    @Override
    public Optional<Organizacion> findById(Long id) {
        String sql = "SELECT id, nombre_organizacion, activa, fecha_creacion, fecha_actualizacion FROM organizaciones WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.ORGANIZACION_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Organizacion> findByNombreOrganizacion(String nombreOrganizacion) {
        String sql = "SELECT id, nombre_organizacion, activa, fecha_creacion, fecha_actualizacion FROM organizaciones WHERE nombre_organizacion = :nombreOrganizacion";
        MapSqlParameterSource params = new MapSqlParameterSource("nombreOrganizacion", nombreOrganizacion);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.ORGANIZACION_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Organizacion> findAll() {
        String sql = "SELECT id, nombre_organizacion, activa, fecha_creacion, fecha_actualizacion FROM organizaciones ORDER BY nombre_organizacion";
        return namedParameterJdbcTemplate.query(sql, CustomRowMappers.ORGANIZACION_ROW_MAPPER);
    }

    @Override
    public Organizacion update(Organizacion organizacion) {
        String sql = "UPDATE organizaciones SET nombre_organizacion = :nombreOrganizacion, activa = :activa, fecha_actualizacion = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombreOrganizacion", organizacion.getNombreOrganizacion());
        params.addValue("activa", organizacion.getActiva());
        params.addValue("id", organizacion.getId());

        int rowsAffected = namedParameterJdbcTemplate.update(sql, params);
        if (rowsAffected == 0) {
            throw new ResourceNotFoundException("Organización no encontrada con ID: " + organizacion.getId());
        }
        // Para tener las fechas actualizadas, idealmente se podría recargar el objeto:
        // return findById(organizacion.getId()).orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada después de actualizar."));
        // Por simplicidad y evitar otra consulta, devolvemos el objeto con el ID.
        return organizacion;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM organizaciones WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM organizaciones WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByNombreOrganizacion(String nombreOrganizacion) {
        String sql = "SELECT COUNT(*) FROM organizaciones WHERE nombre_organizacion = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nombreOrganizacion);
        return count != null && count > 0;
    }
}
