package co.rufe.rufe.service;

import co.rufe.rufe.dto.menu.MenuItemRequest;
import co.rufe.rufe.dto.menu.MenuItemResponse;

import java.util.List;

public interface IMenuItemService {
    MenuItemResponse createMenuItem(MenuItemRequest request);
    MenuItemResponse getMenuItemById(Long id);
    List<MenuItemResponse> getAllMenuItems();
    List<MenuItemResponse> getRootMenuItems(); // Items sin parent_id
    List<MenuItemResponse> getSubMenuItems(Long parentId);
    MenuItemResponse updateMenuItem(Long id, MenuItemRequest request);
    void deleteMenuItem(Long id);
    // Métodos para asignar/revocar permisos
    void assignPermissionToRole(Long rolId, Long menuItemId);
    void revokePermissionFromRole(Long rolId, Long menuItemId);
    List<Long> getMenuItemIdsByRoleId(Long rolId);
    MenuItemResponse getMenuItemByNombre(String nombreItem);
    void assignMenuItemToRole(Long rolId, Long menuItemId);
    void revokeMenuItemFromRole(Long rolId, Long menuItemId);
    List<MenuItemResponse> getMenuItemsByRolId(Long rolId);
}
