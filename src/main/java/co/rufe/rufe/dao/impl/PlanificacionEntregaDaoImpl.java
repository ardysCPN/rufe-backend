package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IPlanificacionEntregaDao;
import co.rufe.rufe.model.PlanificacionEntrega;
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
public class PlanificacionEntregaDaoImpl implements IPlanificacionEntregaDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PlanificacionEntregaDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public PlanificacionEntrega save(PlanificacionEntrega plan) {
        String sql = "INSERT INTO planificacion_entregas (organizacion_id, evento_id, registro_rufe_id, ayuda_catalogo_id, cantidad, estado, fecha_creacion) " +
                     "VALUES (:organizacionId, :eventoId, :registroRufeId, :ayudaCatalogoId, :cantidad, :estado, NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", plan.getOrganizacionId());
        params.addValue("eventoId", plan.getEventoId());
        params.addValue("registroRufeId", plan.getRegistroRufeId());
        params.addValue("ayudaCatalogoId", plan.getAyudaCatalogoId());
        params.addValue("cantidad", plan.getCantidad());
        params.addValue("estado", plan.getEstado() != null ? plan.getEstado() : "PENDIENTE");

        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        plan.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return plan;
    }

    @Override
    public Optional<PlanificacionEntrega> findById(Long id) {
        String sql = "SELECT * FROM planificacion_entregas WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            PlanificacionEntrega plan = namedParameterJdbcTemplate.queryForObject(sql, params, this::mapRowToPlan);
            return Optional.ofNullable(plan);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<PlanificacionEntrega> findAllByEventoId(Long eventoId) {
        String sql = "SELECT p.*, r.direccion as nombre_beneficiario, a.nombre as nombre_articulo " +
                     "FROM planificacion_entregas p " +
                     "JOIN registros_rufe r ON p.registro_rufe_id = r.id " +
                     "JOIN ayuda_catalogo a ON p.ayuda_catalogo_id = a.id " +
                     "WHERE p.evento_id = :eventoId";
        MapSqlParameterSource params = new MapSqlParameterSource("eventoId", eventoId);
        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            PlanificacionEntrega p = mapRowToPlan(rs, rowNum);
            p.setNombreBeneficiario(rs.getString("nombre_beneficiario"));
            p.setNombreArticulo(rs.getString("nombre_articulo"));
            return p;
        });
    }

    @Override
    public List<PlanificacionEntrega> findAllByOrganizacionIdAndEstado(Long organizacionId, String estado) {
        String sql = "SELECT p.*, r.direccion as nombre_beneficiario, a.nombre as nombre_articulo " +
                     "FROM planificacion_entregas p " +
                     "JOIN registros_rufe r ON p.registro_rufe_id = r.id " +
                     "JOIN ayuda_catalogo a ON p.ayuda_catalogo_id = a.id " +
                     "WHERE p.organizacion_id = :organizacionId AND p.estado = :estado";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", organizacionId);
        params.addValue("estado", estado);
        
        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            PlanificacionEntrega p = mapRowToPlan(rs, rowNum);
            p.setNombreBeneficiario(rs.getString("nombre_beneficiario"));
            p.setNombreArticulo(rs.getString("nombre_articulo"));
            return p;
        });
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM planificacion_entregas WHERE id = :id";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }

    @Override
    public void updateEstado(Long id, String estado) {
        String sql = "UPDATE planificacion_entregas SET estado = :estado WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("estado", estado);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public boolean existsByRegistroRufeIdAndAyudaCatalogoIdAndEstado(Long rufeId, Integer ayudaId, String estado) {
        String sql = "SELECT COUNT(*) FROM planificacion_entregas WHERE registro_rufe_id = :rufeId AND ayuda_catalogo_id = :ayudaId AND estado = :estado";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rufeId", rufeId);
        params.addValue("ayudaId", ayudaId);
        params.addValue("estado", estado);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    private PlanificacionEntrega mapRowToPlan(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return PlanificacionEntrega.builder()
                .id(rs.getLong("id"))
                .organizacionId(rs.getLong("organizacion_id"))
                .eventoId(rs.getLong("evento_id"))
                .registroRufeId(rs.getLong("registro_rufe_id"))
                .ayudaCatalogoId(rs.getInt("ayuda_catalogo_id"))
                .cantidad(rs.getDouble("cantidad"))
                .estado(rs.getString("estado"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .build();
    }
}
