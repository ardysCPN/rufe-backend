package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IBodegaInventarioDao;
import co.rufe.rufe.model.BodegaInventario;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BodegaInventarioDaoImpl implements IBodegaInventarioDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<BodegaInventario> rowMapper = (rs, rowNum) -> BodegaInventario.builder()
            .id(rs.getLong("id"))
            .organizacionId(rs.getLong("organizacion_id"))
            .ayudaCatalogoId(rs.getInt("ayuda_catalogo_id"))
            .cantidad(rs.getBigDecimal("cantidad"))
            .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
            .build();

    @Override
    public BodegaInventario save(BodegaInventario b) {
        if (b.getId() == null) {
            String sql = "INSERT INTO bodega_inventario (organizacion_id, ayuda_catalogo_id, cantidad) " +
                         "VALUES (?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, b.getOrganizacionId());
                ps.setInt(2, b.getAyudaCatalogoId());
                ps.setBigDecimal(3, b.getCantidad());
                return ps;
            }, keyHolder);
            b.setId((Long) keyHolder.getKeys().get("id"));
            b.setFechaActualizacion(LocalDateTime.now());
        } else {
            updateStock(b.getOrganizacionId(), b.getAyudaCatalogoId(), b.getCantidad());
        }
        return b;
    }

    @Override
    public Optional<BodegaInventario> findById(Long id) {
        String sql = "SELECT * FROM bodega_inventario WHERE id = ?";
        List<BodegaInventario> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<BodegaInventario> findByOrganizacionAndAyuda(Long organizacionId, Integer ayudaCatalogoId) {
        String sql = "SELECT * FROM bodega_inventario WHERE organizacion_id = ? AND ayuda_catalogo_id = ?";
        List<BodegaInventario> results = jdbcTemplate.query(sql, rowMapper, organizacionId, ayudaCatalogoId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<BodegaInventario> findAllByOrganizacion(Long organizacionId) {
        String sql = "SELECT * FROM bodega_inventario WHERE organizacion_id = ? ORDER BY ayuda_catalogo_id ASC";
        return jdbcTemplate.query(sql, rowMapper, organizacionId);
    }

    @Override
    public void updateStock(Long organizacionId, Integer ayudaCatalogoId, BigDecimal newStock) {
        String sql = "UPDATE bodega_inventario SET cantidad = ?, fecha_actualizacion = CURRENT_TIMESTAMP " +
                     "WHERE organizacion_id = ? AND ayuda_catalogo_id = ?";
        jdbcTemplate.update(sql, newStock, organizacionId, ayudaCatalogoId);
    }
}
