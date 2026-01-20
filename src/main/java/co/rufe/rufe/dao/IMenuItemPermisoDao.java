// C:\microservicio-rufe\rufe\src\main\java\co\rufe\rufe\dao\IMenuItemPermisoDao.java
package co.rufe.rufe.dao;

import java.util.List;
import java.util.Set;

import co.rufe.rufe.model.MenuItemPermiso;

public interface IMenuItemPermisoDao {
    // Asigna un permiso a un MenuItem
    void assignMenuItemPermiso(Integer menuItemId, Integer permisoId);

    // Revoca un permiso de un MenuItem
    void revokeMenuItemPermiso(Integer menuItemId, Integer permisoId);

    // Verifica si un MenuItem tiene un permiso específico
    boolean existsMenuItemPermiso(Integer menuItemId, Integer permisoId);

    // Obtiene todas las relaciones de MenuItem-Permiso para un MenuItem dado
    List<MenuItemPermiso> findByMenuItemId(Integer menuItemId);

    // Obtiene todas las relaciones de MenuItem-Permiso para un Permiso dado
    List<MenuItemPermiso> findByPermisoId(Integer permisoId);

    // Elimina todas las relaciones para un MenuItem (cuando se borra el MenuItem)
    void deleteByMenuItemId(Integer menuItemId);

    // Elimina todas las relaciones para un Permiso (cuando se borra el Permiso)
    void deleteByPermisoId(Integer permisoId);
    
    // Nuevo método para obtener los IDs de MenuItem basados en una lista de nombres de permisos
    Set<Integer> findMenuItemIdsByPermisoNames(Set<String> permisoNames);
}
