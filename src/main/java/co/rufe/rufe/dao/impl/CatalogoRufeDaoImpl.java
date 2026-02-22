package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.ICatalogoRufeDao;
import co.rufe.rufe.model.catalogo.*;
import co.rufe.rufe.util.CustomRowMappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class CatalogoRufeDaoImpl implements ICatalogoRufeDao {

    private final JdbcTemplate jdbcTemplate;

    public CatalogoRufeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Implementaciones para TipoUbicacionBien
    @Override
    public List<TipoUbicacionBien> findAllTipoUbicacionBien() {
        String sql = "SELECT id, nombre FROM tipo_ubicacion_bien ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.TIPO_UBICACION_BIEN_ROW_MAPPER);
    }

    @Override
    public Optional<TipoUbicacionBien> findTipoUbicacionBienById(Integer id) {
        String sql = "SELECT id, nombre FROM tipo_ubicacion_bien WHERE id = ?";
        try {
            return Optional
                    .ofNullable(jdbcTemplate.queryForObject(sql, CustomRowMappers.TIPO_UBICACION_BIEN_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Implementaciones para TipoAlojamientoActual
    @Override
    public List<TipoAlojamientoActual> findAllTipoAlojamientoActual() {
        String sql = "SELECT id, nombre FROM tipo_alojamiento_actual ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.TIPO_ALOJAMIENTO_ACTUAL_ROW_MAPPER);
    }

    @Override
    public Optional<TipoAlojamientoActual> findTipoAlojamientoActualById(Integer id) {
        String sql = "SELECT id, nombre FROM tipo_alojamiento_actual WHERE id = ?";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, CustomRowMappers.TIPO_ALOJAMIENTO_ACTUAL_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Implementaciones para FormaTenenciaBien
    @Override
    public List<FormaTenenciaBien> findAllFormaTenenciaBien() {
        String sql = "SELECT id, nombre FROM forma_tenencia_bien ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.FORMA_TENENCIA_BIEN_ROW_MAPPER);
    }

    @Override
    public Optional<FormaTenenciaBien> findFormaTenenciaBienById(Integer id) {
        String sql = "SELECT id, nombre FROM forma_tenencia_bien WHERE id = ?";
        try {
            return Optional
                    .ofNullable(jdbcTemplate.queryForObject(sql, CustomRowMappers.FORMA_TENENCIA_BIEN_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Implementaciones para EstadoBien
    @Override
    public List<EstadoBien> findAllEstadoBien() {
        String sql = "SELECT id, nombre FROM estado_bien ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.ESTADO_BIEN_ROW_MAPPER);
    }

    @Override
    public Optional<EstadoBien> findEstadoBienById(Integer id) {
        String sql = "SELECT id, nombre FROM estado_bien WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, CustomRowMappers.ESTADO_BIEN_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Implementaciones para TipoBien
    @Override
    public List<TipoBien> findAllTipoBien() {
        String sql = "SELECT id, nombre FROM tipo_bien ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.TIPO_BIEN_ROW_MAPPER);
    }

    @Override
    public Optional<TipoBien> findTipoBienById(Integer id) {
        String sql = "SELECT id, nombre FROM tipo_bien WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, CustomRowMappers.TIPO_BIEN_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Implementaciones para TipoDocumento
    @Override
    public List<TipoDocumento> findAllTipoDocumento() {
        String sql = "SELECT id, nombre FROM tipo_documento ORDER BY id";
        return jdbcTemplate.query(sql, CustomRowMappers.TIPO_DOCUMENTO_ROW_MAPPER);
    }

    @Override
    public Optional<TipoDocumento> findTipoDocumentoById(Integer id) {
        String sql = "SELECT id, nombre FROM tipo_documento WHERE id = ?";
        try {
            return Optional
                    .ofNullable(jdbcTemplate.queryForObject(sql, CustomRowMappers.TIPO_DOCUMENTO_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Implementaciones para Parentesco
    @Override
    public List<Parentesco> findAllParentesco() {
        String sql = "SELECT id, nombre FROM parentesco ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.PARENTESCO_ROW_MAPPER);
    }

    @Override
    public Optional<Parentesco> findParentescoById(Integer id) {
        String sql = "SELECT id, nombre FROM parentesco WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, CustomRowMappers.PARENTESCO_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Implementaciones para Genero
    @Override
    public List<Genero> findAllGenero() {
        String sql = "SELECT id, nombre FROM genero ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.GENERO_ROW_MAPPER);
    }

    @Override
    public Optional<Genero> findGeneroById(Integer id) {
        String sql = "SELECT id, nombre FROM genero WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, CustomRowMappers.GENERO_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Implementaciones para PertenenciaEtnica
    @Override
    public List<PertenenciaEtnica> findAllPertenenciaEtnica() {
        String sql = "SELECT id, nombre FROM pertenencia_etnica ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.PERTENENCIA_ETNICA_ROW_MAPPER);
    }

    @Override
    public Optional<PertenenciaEtnica> findPertenenciaEtnicaById(Integer id) {
        String sql = "SELECT id, nombre FROM pertenencia_etnica WHERE id = ?";
        try {
            return Optional
                    .ofNullable(jdbcTemplate.queryForObject(sql, CustomRowMappers.PERTENENCIA_ETNICA_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Departamento> findAllDepartamentos() {
        String sql = "SELECT id, nombre FROM departamento ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.DEPARTAMENTO_ROW_MAPPER);
    }

    @Override
    public List<Municipio> findAllMunicipios() {
        String sql = "SELECT id, nombre, departamento_id FROM municipio ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.MUNICIPIO_ROW_MAPPER);
    }

    @Override
    public List<Evento> findAllEventos() {
        String sql = "SELECT id, nombre FROM evento ORDER BY nombre";
        return jdbcTemplate.query(sql, CustomRowMappers.EVENTO_ROW_MAPPER);
    }

    @Override
    public void create(String tableName, String nombre) {
        log.info("Creando item en tabla {}: {}", tableName, nombre);
        String sql = String.format("INSERT INTO %s (nombre) VALUES (?)", tableName);
        jdbcTemplate.update(sql, nombre);
    }

    @Override
    public void update(String tableName, Integer id, String nombre) {
        log.info("Actualizando item en tabla {} ID {}: {}", tableName, id, nombre);
        String sql = String.format("UPDATE %s SET nombre = ? WHERE id = ?", tableName);
        jdbcTemplate.update(sql, nombre, id);
    }

    @Override
    public void delete(String tableName, Integer id) {
        log.info("Eliminando item en tabla {} ID {}", tableName, id);
        String sql = String.format("DELETE FROM %s WHERE id = ?", tableName);
        jdbcTemplate.update(sql, id);
    }
}
