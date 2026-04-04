package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IAyudasEntregadasDao;
import co.rufe.rufe.model.AyudasEntregadas;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AyudasEntregadasDaoImpl implements IAyudasEntregadasDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AyudasEntregadas> rowMapper = (rs, rowNum) -> {
        AyudasEntregadas ae = AyudasEntregadas.builder()
            .id(rs.getLong("id"))
            .organizacionId(rs.getLong("organizacion_id"))
            .registroRufeId(rs.getLong("registro_rufe_id"))
            .ayudaCatalogoId(rs.getInt("ayuda_catalogo_id"))
            .cantidad(rs.getBigDecimal("cantidad"))
            .firmaDigital(rs.getString("firma_digital"))
            .evidenciaFotoUrl(rs.getString("evidencia_foto_url"))
            .fechaEntrega(rs.getTimestamp("fecha_entrega").toLocalDateTime())
            .build();
            
        ae.setAyudaCatalogo(co.rufe.rufe.model.AyudaCatalogo.builder()
            .id(rs.getInt("ayuda_catalogo_id"))
            .nombre(rs.getString("nombre"))
            .descripcion(rs.getString("descripcion"))
            .unidadMedida(rs.getString("unidad_medida"))
            .build());
            
        return ae;
    };

    @Override
    public AyudasEntregadas save(AyudasEntregadas a) {
        String sql = "INSERT INTO ayudas_entregadas (organizacion_id, registro_rufe_id, ayuda_catalogo_id, cantidad, firma_digital, evidencia_foto_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, a.getOrganizacionId());
            ps.setLong(2, a.getRegistroRufeId());
            ps.setInt(3, a.getAyudaCatalogoId());
            ps.setBigDecimal(4, a.getCantidad());
            ps.setString(5, a.getFirmaDigital());
            ps.setString(6, a.getEvidenciaFotoUrl());
            return ps;
        }, keyHolder);
        
        a.setId(((Number) keyHolder.getKeys().get("id")).longValue());
        return a;
    }

    @Override
    public List<AyudasEntregadas> findByOrganizacionId(Long organizacionId) {
        String sql = "SELECT ae.*, a.nombre, a.descripcion, a.unidad_medida FROM ayudas_entregadas ae " +
                     "JOIN ayuda_catalogo a ON ae.ayuda_catalogo_id = a.id WHERE ae.organizacion_id = ? ORDER BY ae.fecha_entrega DESC";
        return jdbcTemplate.query(sql, rowMapper, organizacionId);
    }

    @Override
    public List<AyudasEntregadas> findByRegistroRufeId(Long registroRufeId) {
        String sql = "SELECT ae.*, a.nombre, a.descripcion, a.unidad_medida FROM ayudas_entregadas ae " +
                     "JOIN ayuda_catalogo a ON ae.ayuda_catalogo_id = a.id WHERE ae.registro_rufe_id = ? ORDER BY ae.fecha_entrega DESC";
        return jdbcTemplate.query(sql, rowMapper, registroRufeId);
    }
}
