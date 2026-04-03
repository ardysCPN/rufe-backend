package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IAyudaCatalogoDao;
import co.rufe.rufe.model.AyudaCatalogo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
}
