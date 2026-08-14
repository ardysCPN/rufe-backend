package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IAyudaCatalogoDao;
import co.rufe.rufe.model.AyudaCatalogo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AyudaCatalogoDaoImpl implements IAyudaCatalogoDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AyudaCatalogo> rowMapper = (rs, rowNum) -> AyudaCatalogo.builder()
            .id(rs.getInt("id"))
            .organizacionId(rs.getObject("organizacion_id", Long.class))
            .nombre(rs.getString("nombre"))
            .descripcion(rs.getString("descripcion"))
            .unidadMedida(rs.getString("unidad_medida"))
            .tipoAyuda(rs.getString("tipo_ayuda"))
            .build();

    @Override
    public List<AyudaCatalogo> findAllByOrganizacionId(Long organizacionId) {
        if (organizacionId == null) {
            return jdbcTemplate.query("SELECT * FROM ayuda_catalogo ORDER BY id ASC", rowMapper);
        }
        return jdbcTemplate.query("SELECT * FROM ayuda_catalogo WHERE organizacion_id IS NULL OR organizacion_id = ? ORDER BY id ASC", rowMapper, organizacionId);
    }

    @Override
    public AyudaCatalogo findById(Integer id) {
        List<AyudaCatalogo> r = jdbcTemplate.query("SELECT * FROM ayuda_catalogo WHERE id = ?", rowMapper, id);
        return r.isEmpty() ? null : r.get(0);
    }

    @Override
    public AyudaCatalogo save(AyudaCatalogo item) {
        if (item.getId() != null) {
            jdbcTemplate.update("UPDATE ayuda_catalogo SET organizacion_id = ?, nombre = ?, descripcion = ?, unidad_medida = ?, tipo_ayuda = ? WHERE id = ?",
                    item.getOrganizacionId(), item.getNombre(), item.getDescripcion(), item.getUnidadMedida(), item.getTipoAyuda(), item.getId());
            return item;
        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO ayuda_catalogo (organizacion_id, nombre, descripcion, unidad_medida, tipo_ayuda) VALUES (?, ?, ?, ?, ?)",
                        new String[] { "id" });
                
                if (item.getOrganizacionId() != null) {
                    ps.setLong(1, item.getOrganizacionId());
                } else {
                    ps.setNull(1, java.sql.Types.BIGINT);
                }
                
                ps.setString(2, item.getNombre());
                ps.setString(3, item.getDescripcion());
                ps.setString(4, item.getUnidadMedida());
                ps.setString(5, item.getTipoAyuda() != null ? item.getTipoAyuda() : "INDIVIDUAL");
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                item.setId(keyHolder.getKey().intValue());
            }
            return item;
        }
    }
}
