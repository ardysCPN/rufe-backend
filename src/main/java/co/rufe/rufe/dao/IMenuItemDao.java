package co.rufe.rufe.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import co.rufe.rufe.model.MenuItem;

public interface IMenuItemDao {
    MenuItem save(MenuItem menuItem);

    // Cambiados de Long a Integer
    Optional<MenuItem> findById(Integer id);
    boolean existsById(Integer id);
    boolean deleteById(Integer id); // Retorna boolean para indicar si se eliminó o no

    List<MenuItem> findAll();
    List<MenuItem> findByParentId(Integer parentId); // Cambiado de Long a Integer
    MenuItem update(MenuItem menuItem); // Asumiendo que esta es una operación de bajo nivel que el DAO maneja

    boolean existsByNombreItem(String nombreItem);
    Optional<MenuItem> findByNombreItem(String nombreItem);
    Optional<MenuItem> findByNombreItemAndParentId(String nombreItem, Integer parentId); // Cambiado de Long a Integer

    // Modificado para usar List<Integer> ya que los IDs de MenuItem son Integer
    List<MenuItem> findAllById(List<Integer> menuItemIds); // Para buscar múltiples MenuItems por sus IDs

    // Si tu findByIds usa un Set, asegúrate de que sea Set<Integer>
    List<MenuItem> findByIds(Set<Integer> ids); // Cambiado de Set<Long> a Set<Integer>

}
