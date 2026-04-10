package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IMenuDao;
import co.rufe.rufe.model.Menu;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuDaoImpl implements IMenuDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public MenuDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<Menu> menuRowMapper = (rs, rowNum) -> Menu.builder()
            .id(rs.getLong("id"))
            .idMenu(rs.getObject("id_menu") != null ? rs.getLong("id_menu") : null)
            .idTipoMenu(rs.getInt("id_tipo_menu"))
            .routerUrl(rs.getString("router_url"))
            .nombreOpcion(rs.getString("nombre_opcion"))
            .icono(rs.getString("icono"))
            .orden(rs.getInt("orden"))
            .offlineCompatible(rs.getBoolean("offline_compatible"))
            .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
            .build();

    @Override
    public List<Menu> findByRolId(Long rolId) {
        String sql = "SELECT m.* FROM menu m " +
                "JOIN menu_roles mr ON m.id = mr.menu_id " +
                "WHERE mr.rol_id = :rolId " +
                "ORDER BY m.orden";

        MapSqlParameterSource params = new MapSqlParameterSource("rolId", rolId);
        return namedParameterJdbcTemplate.query(sql, params, menuRowMapper);
    }

    @Override
    public List<Menu> findAll() {
        String sql = "SELECT * FROM menu ORDER BY orden";
        return namedParameterJdbcTemplate.query(sql, menuRowMapper);
    }

    @Override
    public java.util.Optional<Menu> findById(Long id) {
        String sql = "SELECT * FROM menu WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return java.util.Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, menuRowMapper));
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return java.util.Optional.empty();
        }
    }

    @Override
    public void update(Menu menu) {
        String sql = "UPDATE menu SET id_menu = :idMenu, id_tipo_menu = :idTipoMenu, router_url = :routerUrl, " +
                     "nombre_opcion = :nombreOpcion, icono = :icono, orden = :orden, " +
                     "offline_compatible = :offlineCompatible WHERE id = :id";
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idMenu", menu.getIdMenu());
        params.addValue("idTipoMenu", menu.getIdTipoMenu());
        params.addValue("routerUrl", menu.getRouterUrl());
        params.addValue("nombreOpcion", menu.getNombreOpcion());
        params.addValue("icono", menu.getIcono());
        params.addValue("orden", menu.getOrden());
        params.addValue("offlineCompatible", menu.getOfflineCompatible());
        params.addValue("id", menu.getId());

        namedParameterJdbcTemplate.update(sql, params);
    }
}
