package co.rufe.rufe.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import co.rufe.rufe.model.MenuItem;

public interface IMenuItemDao {
    MenuItem save(MenuItem menuItem);
    Optional<MenuItem> findById(Long id);
    List<MenuItem> findAll();
    List<MenuItem> findByParentId(Long parentId);
    MenuItem update(MenuItem menuItem);
    boolean deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByNombreItem(String nombreItem);
    List<MenuItem> findByIds(Set<Long> ids);
    Optional<MenuItem> findByNombreItem(String nombreItem);
    List<MenuItem> findAllById(List<Long> menuItemIds);
}
