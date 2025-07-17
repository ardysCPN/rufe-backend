// C:\microservicio-rufe\rufe\src\main\java\co\rufe\rufe\dao\impl\MenuItemPermisoDaoImpl.java
package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IMenuItemPermisoDao;
import co.rufe.rufe.model.MenuItemPermiso;
import co.rufe.rufe.util.CustomRowMappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class MenuItemPermisoDaoImpl implements IMenuItemPermisoDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public MenuItemPermisoDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void assignMenuItemPermiso(Integer menuItemId, Integer permisoId) {
        String sql = "INSERT INTO menu_item_permisos (menu_item_id, permiso_id) VALUES (:menuItemId, :permisoId) ON CONFLICT (menu_item_id, permiso_id) DO NOTHING";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("menuItemId", menuItemId);
        params.addValue("permisoId", permisoId);
        try {
            namedParameterJdbcTemplate.update(sql, params);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al asignar permiso {} a MenuItem {}: {}", permisoId, menuItemId, e.getMessage());
            throw new IllegalArgumentException("No se pudo asignar el permiso al ítem de menú. Verifique los IDs. Puede que la asignación ya exista.", e);
        }
    }

    @Override
    public void revokeMenuItemPermiso(Integer menuItemId, Integer permisoId) {
        String sql = "DELETE FROM menu_item_permisos WHERE menu_item_id = :menuItemId AND permiso_id = :permisoId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("menuItemId", menuItemId);
        params.addValue("permisoId", permisoId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<MenuItemPermiso> findByMenuItemId(Integer menuItemId) {
        String sql = "SELECT menu_item_id, permiso_id FROM menu_item_permisos WHERE menu_item_id = :menuItemId";
        MapSqlParameterSource params = new MapSqlParameterSource("menuItemId", menuItemId);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.MENU_ITEM_PERMISO_ROW_MAPPER);
    }

    @Override
    public List<MenuItemPermiso> findByPermisoId(Integer permisoId) {
        String sql = "SELECT menu_item_id, permiso_id FROM menu_item_permisos WHERE permiso_id = :permisoId";
        MapSqlParameterSource params = new MapSqlParameterSource("permisoId", permisoId);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.MENU_ITEM_PERMISO_ROW_MAPPER);
    }

    @Override
    public boolean existsMenuItemPermiso(Integer menuItemId, Integer permisoId) {
        String sql = "SELECT COUNT(*) FROM menu_item_permisos WHERE menu_item_id = :menuItemId AND permiso_id = :permisoId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("menuItemId", menuItemId);
        params.addValue("permisoId", permisoId);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean deleteByMenuItemId(Integer menuItemId) {
        String sql = "DELETE FROM menu_item_permisos WHERE menu_item_id = :menuItemId";
        MapSqlParameterSource params = new MapSqlParameterSource("menuItemId", menuItemId);
        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean deleteByPermisoId(Integer permisoId) {
        String sql = "DELETE FROM menu_item_permisos WHERE permiso_id = :permisoId";
        MapSqlParameterSource params = new MapSqlParameterSource("permisoId", permisoId);
        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public Set<Integer> findMenuItemIdsByPermisoNames(Set<String> permisoNames) {
        if (permisoNames == null || permisoNames.isEmpty()) {
            return Collections.emptySet();
        }
        // Esta consulta es crucial para el menú dinámico
        // Selecciona menu_item_id de los items que tienen al menos un permiso en la lista proporcionada.
        String sql = "SELECT DISTINCT mip.menu_item_id FROM menu_item_permisos mip " +
                     "JOIN permisos p ON mip.permiso_id = p.id " +
                     "WHERE p.nombre_permiso IN (:permisoNames)";
        MapSqlParameterSource params = new MapSqlParameterSource("permisoNames", permisoNames);
        List<Integer> ids = namedParameterJdbcTemplate.queryForList(sql, params, Integer.class);
        return new HashSet<>(ids);
    }
}
