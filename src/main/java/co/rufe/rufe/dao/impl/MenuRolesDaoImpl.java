package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IMenuRolesDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuRolesDaoImpl implements IMenuRolesDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public MenuRolesDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void assignMenuToRol(Long rolId, Long menuId) {
        String sql = "INSERT INTO menu_roles (rol_id, menu_id) VALUES (:rolId, :menuId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rolId", rolId);
        params.addValue("menuId", menuId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void revokeMenuFromRol(Long rolId, Long menuId) {
        String sql = "DELETE FROM menu_roles WHERE rol_id = :rolId AND menu_id = :menuId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rolId", rolId);
        params.addValue("menuId", menuId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteByRolId(Long rolId) {
        String sql = "DELETE FROM menu_roles WHERE rol_id = :rolId";
        MapSqlParameterSource params = new MapSqlParameterSource("rolId", rolId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<Long> findMenuIdsByRolId(Long rolId) {
        String sql = "SELECT menu_id FROM menu_roles WHERE rol_id = :rolId";
        MapSqlParameterSource params = new MapSqlParameterSource("rolId", rolId);
        return namedParameterJdbcTemplate.queryForList(sql, params, Long.class);
    }
}
