package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IEvidenciaRufeDao;
import co.rufe.rufe.model.EvidenciaRufe;
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
public class EvidenciaRufeDaoImpl implements IEvidenciaRufeDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<EvidenciaRufe> rowMapper = (rs, rowNum) -> EvidenciaRufe.builder()
            .id(rs.getLong("id"))
            .registroRufeId(rs.getLong("registro_rufe_id"))
            .tipoEvidencia(rs.getString("tipo_evidencia"))
            .fotoUrl(rs.getString("foto_url"))
            .fechaCarga(rs.getTimestamp("fecha_carga").toLocalDateTime())
            .build();

    @Override
    public EvidenciaRufe save(EvidenciaRufe evidencia) {
        String sql = "INSERT INTO evidencias_rufe (registro_rufe_id, tipo_evidencia, foto_url) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, evidencia.getRegistroRufeId());
            ps.setString(2, evidencia.getTipoEvidencia());
            ps.setString(3, evidencia.getFotoUrl());
            return ps;
        }, keyHolder);
        evidencia.setId((Long) keyHolder.getKeys().get("id"));
        return evidencia;
    }

    @Override
    public List<EvidenciaRufe> findByRegistroRufeId(Long registroRufeId) {
        return jdbcTemplate.query("SELECT * FROM evidencias_rufe WHERE registro_rufe_id = ?", rowMapper, registroRufeId);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM evidencias_rufe WHERE id = ?", id);
    }
}
