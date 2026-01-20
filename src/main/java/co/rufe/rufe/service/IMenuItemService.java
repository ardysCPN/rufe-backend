package co.rufe.rufe.service;

import java.util.Collection; 
import java.util.List;

import co.rufe.rufe.dto.menu.MenuItemRequest;
import co.rufe.rufe.dto.menu.MenuItemResponse;
import co.rufe.rufe.dto.permiso.PermisoResponse;

public interface IMenuItemService {

    // --- Métodos CRUD para MenuItem ---
    MenuItemResponse createMenuItem(MenuItemRequest request);
    MenuItemResponse getMenuItemById(Integer id); 
    List<MenuItemResponse> getAllMenuItems();
    List<MenuItemResponse> getRootMenuItems();
    List<MenuItemResponse> getSubMenuItems(Integer parentId); 
    MenuItemResponse updateMenuItem(Integer id, MenuItemRequest request); 
    void deleteMenuItem(Integer id); 
    MenuItemResponse getMenuItemByNombre(String nombreItem);


    // --- Métodos para gestionar Permisos ASOCIADOS a un MenuItem (Visibilidad) ---
    // Esto es para la tabla 'menu_item_permisos'
    void assignPermisoToMenuItem(Integer menuItemId, Integer permisoId);
    void revokePermisoFromMenuItem(Integer menuItemId, Integer permisoId);
    List<PermisoResponse> getPermisosByMenuItemId(Integer menuItemId);

    // --- Método para obtener el menú dinámico basado en los permisos del usuario ---
    // userPermissions son los strings de los permisos (ej. "organizaciones:leer")
    List<MenuItemResponse> getDynamicMenuForUser(Collection<String> userPermissions);
}