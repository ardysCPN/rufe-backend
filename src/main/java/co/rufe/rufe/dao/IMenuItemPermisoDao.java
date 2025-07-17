// C:\microservicio-rufe\rufe\src\main\java\co\rufe\rufe\dao\IMenuItemPermisoDao.java
package co.rufe.rufe.dao;

import java.util.List;
import java.util.Set;

import co.rufe.rufe.model.MenuItemPermiso;

public interface IMenuItemPermisoDao {
    void assignMenuItemPermiso(Integer menuItemId, Integer permisoId);
    void revokeMenuItemPermiso(Integer menuItemId, Integer permisoId);
    List<MenuItemPermiso> findByMenuItemId(Integer menuItemId);
    List<MenuItemPermiso> findByPermisoId(Integer permisoId);
    boolean existsMenuItemPermiso(Integer menuItemId, Integer permisoId);
    boolean deleteByMenuItemId(Integer menuItemId);
    boolean deleteByPermisoId(Integer permisoId);
    
    // Nuevo método para obtener los IDs de MenuItem basados en una lista de nombres de permisos
    Set<Integer> findMenuItemIdsByPermisoNames(Set<String> permisoNames);
}
