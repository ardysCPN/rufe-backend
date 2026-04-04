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
            .nombre(rs.getString("nombre"))
            .descripcion(rs.getString("descripcion"))
            .unidadMedida(rs.getString("unidad_medida"))
            .build();

    @Override
    public List<AyudaCatalogo> findAll() {
        return jdbcTemplate.query("SELECT * FROM ayuda_catalogo ORDER BY id ASC", rowMapper);
    }

    @Override
    public AyudaCatalogo findById(Integer id) {
        List<AyudaCatalogo> r = jdbcTemplate.query("SELECT * FROM ayuda_catalogo WHERE id = ?", rowMapper, id);
        return r.isEmpty() ? null : r.get(0);
    }

    @Override
    public AyudaCatalogo save(AyudaCatalogo item) {
        if (item.getId() != null) {
            jdbcTemplate.update("UPDATE ayuda_catalogo SET nombre = ?, descripcion = ?, unidad_medida = ? WHERE id = ?",
                    item.getNombre(), item.getDescripcion(), item.getUnidadMedida(), item.getId());
            return item;
        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO ayuda_catalogo (nombre, descripcion, unidad_medida) VALUES (?, ?, ?)",
                        new String[] { "id" });
                ps.setString(1, item.getNombre());
                ps.setString(2, item.getDescripcion());
                ps.setString(3, item.getUnidadMedida());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                item.setId(keyHolder.getKey().intValue());
            }
            return item;
        }
    }
}
