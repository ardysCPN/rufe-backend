package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IEventoRealDao;
import co.rufe.rufe.model.EventoReal;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class EventoRealDaoImpl implements IEventoRealDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public EventoRealDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<EventoReal> rowMapper = (rs, rowNum) -> EventoReal.builder()
            .id(rs.getLong("id"))
            .organizacionId(rs.getLong("organizacion_id"))
            .clienteId(rs.getString("cliente_id"))
            .nombreEvento(rs.getString("nombre_evento"))
            .tipoEvento(rs.getString("tipo_evento"))
            .fechaEvento(rs.getDate("fecha_evento").toLocalDate())
            .departamento(rs.getString("departamento"))
            .municipio(rs.getString("municipio"))
            .descripcion(rs.getString("descripcion"))
            .estado(rs.getString("estado"))
            .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
            .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
            .build();

    @Override
    public EventoReal save(EventoReal evento) {
        String sql = "INSERT INTO eventos (organizacion_id, cliente_id, nombre_evento, tipo_evento, " +
                "fecha_evento, departamento, municipio, descripcion, estado, fecha_creacion, fecha_actualizacion) " +
                "VALUES (:organizacionId, :clienteId, :nombreEvento, :tipoEvento, " +
                ":fechaEvento, :departamento, :municipio, :descripcion, :estado, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", evento.getOrganizacionId());
        params.addValue("clienteId", evento.getClienteId());
        params.addValue("nombreEvento", evento.getNombreEvento());
        params.addValue("tipoEvento", evento.getTipoEvento());
        params.addValue("fechaEvento", evento.getFechaEvento());
        params.addValue("departamento", evento.getDepartamento());
        params.addValue("municipio", evento.getMunicipio());
        params.addValue("descripcion", evento.getDescripcion());
        params.addValue("estado", evento.getEstado() != null ? evento.getEstado() : "ABIERTO");

        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        evento.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return evento;
    }

    @Override
    public void update(EventoReal evento) {
        String sql = "UPDATE eventos SET nombre_evento = :nombreEvento, tipo_evento = :tipoEvento, " +
                "fecha_evento = :fechaEvento, departamento = :departamento, municipio = :municipio, " +
                "descripcion = :descripcion, estado = :estado, fecha_actualizacion = NOW() " +
                "WHERE id = :id AND organizacion_id = :organizacionId AND fecha_eliminacion IS NULL";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", evento.getId());
        params.addValue("organizacionId", evento.getOrganizacionId());
        params.addValue("nombreEvento", evento.getNombreEvento());
        params.addValue("tipoEvento", evento.getTipoEvento());
        params.addValue("fechaEvento", evento.getFechaEvento());
        params.addValue("departamento", evento.getDepartamento());
        params.addValue("municipio", evento.getMunicipio());
        params.addValue("descripcion", evento.getDescripcion());
        params.addValue("estado", evento.getEstado());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<EventoReal> findById(Long id) {
        String sql = "SELECT * FROM eventos WHERE id = :id AND fecha_eliminacion IS NULL";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, rowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<EventoReal> findByIdAndOrganizacionId(Long id, Long organizacionId) {
        String sql = "SELECT * FROM eventos WHERE id = :id AND organizacion_id = :organizacionId AND fecha_eliminacion IS NULL";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("organizacionId", organizacionId);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, rowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<EventoReal> findAll() {
        String sql = "SELECT * FROM eventos WHERE fecha_eliminacion IS NULL ORDER BY fecha_actualizacion DESC";
        return namedParameterJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<EventoReal> findAllByOrganizacionId(Long organizacionId) {
        String sql = "SELECT * FROM eventos WHERE organizacion_id = :organizacionId AND fecha_eliminacion IS NULL ORDER BY fecha_actualizacion DESC";
        MapSqlParameterSource params = new MapSqlParameterSource("organizacionId", organizacionId);
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public void deleteLogical(Long id, Long organizacionId) {
        String sql = "UPDATE eventos SET fecha_eliminacion = NOW() WHERE id = :id AND organizacion_id = :organizacionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("organizacionId", organizacionId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public boolean existsByClienteIdAndOrganizacionId(String clienteId, Long organizacionId) {
        String sql = "SELECT COUNT(*) FROM eventos WHERE cliente_id = :clienteId AND organizacion_id = :organizacionId AND fecha_eliminacion IS NULL";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clienteId", clienteId);
        params.addValue("organizacionId", organizacionId);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }
}
