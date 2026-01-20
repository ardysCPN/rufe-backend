package co.rufe.rufe.dao.impl;

import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import co.rufe.rufe.dao.IMenuItemDao;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.model.MenuItem;
import co.rufe.rufe.util.CustomRowMappers;

@Repository
public class MenuItemDaoImpl implements IMenuItemDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public MenuItemDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        String sql = "INSERT INTO menu_items (parent_id, nombre_item, ruta, icono, orden) " +
                     "VALUES (:parentId, :nombreItem, :ruta, :icono, :orden)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("parentId", menuItem.getParentId(), Types.BIGINT); // Puede ser NULL
        params.addValue("nombreItem", menuItem.getNombreItem());
        params.addValue("ruta", menuItem.getRuta());
        params.addValue("icono", menuItem.getIcono());
        params.addValue("orden", menuItem.getOrden());

        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        Integer newId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        menuItem.setId(newId);
        return menuItem;
    }

    @Override
    public Optional<MenuItem> findById(Integer id) {
        String sql = "SELECT id, parent_id, nombre_item, ruta, icono, orden FROM menu_items WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.MENU_ITEM_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<MenuItem> findAll() {
        String sql = "SELECT id, parent_id, nombre_item, ruta, icono, orden FROM menu_items ORDER BY orden";
        return namedParameterJdbcTemplate.query(sql, CustomRowMappers.MENU_ITEM_ROW_MAPPER);
    }

    @Override
    public List<MenuItem> findByParentId(Integer parentId) {
        String sql = "SELECT id, parent_id, nombre_item, ruta, icono, orden FROM menu_items WHERE parent_id = :parentId ORDER BY orden";
        MapSqlParameterSource params = new MapSqlParameterSource("parentId", parentId);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.MENU_ITEM_ROW_MAPPER);
    }

    @Override
    public MenuItem update(MenuItem menuItem) {
        String sql = "UPDATE menu_items SET parent_id = :parentId, nombre_item = :nombreItem, ruta = :ruta, " +
                     "icono = :icono, orden = :orden WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("parentId", menuItem.getParentId(), Types.BIGINT);
        params.addValue("nombreItem", menuItem.getNombreItem());
        params.addValue("ruta", menuItem.getRuta());
        params.addValue("icono", menuItem.getIcono());
        params.addValue("orden", menuItem.getOrden());
        params.addValue("id", menuItem.getId());

        int rowsAffected = namedParameterJdbcTemplate.update(sql, params);
        if (rowsAffected == 0) {
            throw new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + menuItem.getId());
        }
        return menuItem;
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM menu_items WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM menu_items WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByNombreItem(String nombreItem) {
        String sql = "SELECT COUNT(*) FROM menu_items WHERE nombre_item = :nombreItem";
        MapSqlParameterSource params = new MapSqlParameterSource("nombreItem", nombreItem);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public List<MenuItem> findByIds(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        // Usamos una cláusula IN para buscar múltiples IDs
        String sql = "SELECT id, parent_id, nombre_item, ruta, icono, orden FROM menu_items WHERE id IN (:ids) ORDER BY orden";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.MENU_ITEM_ROW_MAPPER);
    }

    @Override
    public Optional<MenuItem> findByNombreItem(String nombreItem) { // <-- ¡Aquí está la implementación!
        String sql = "SELECT id, parent_id, nombre_item, ruta, icono, orden FROM menu_items WHERE nombre_item = :nombreItem";
        MapSqlParameterSource params = new MapSqlParameterSource("nombreItem", nombreItem);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.MENU_ITEM_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<MenuItem> findAllById(List<Integer> menuItemIds) {
        if (menuItemIds == null || menuItemIds.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT id, parent_id, nombre_item, ruta, icono, orden FROM menu_items WHERE id IN (:ids) ORDER BY orden";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", menuItemIds);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.MENU_ITEM_ROW_MAPPER);
    }

    @Override
    public Optional<MenuItem> findByNombreItemAndParentId(String nombreItem, Integer parentId) {
        String sql = "SELECT id, parent_id, nombre_item, ruta, icono, orden FROM menu_items WHERE nombre_item = :nombreItem AND parent_id = :parentId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombreItem", nombreItem);
        params.addValue("parentId", parentId, Types.BIGINT);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.MENU_ITEM_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
