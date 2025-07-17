package co.rufe.rufe.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.rufe.rufe.dao.IMenuItemDao;
import co.rufe.rufe.dao.IRolDao; // Para validar si el rol existe
import co.rufe.rufe.dao.IRolPermisoDao;
import co.rufe.rufe.dto.menu.MenuItemRequest;
import co.rufe.rufe.dto.menu.MenuItemResponse;
import co.rufe.rufe.exception.DuplicateResourceException;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.mapper.MenuItemMapper;
import co.rufe.rufe.model.MenuItem;
import co.rufe.rufe.service.IMenuItemService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MenuItemServiceImpl implements IMenuItemService {

    private final IMenuItemDao menuItemDao;
    private final IRolPermisoDao rolPermisoDao;
    private final IRolDao rolDao;

    public MenuItemServiceImpl(IMenuItemDao menuItemDao, IRolPermisoDao rolPermisoDao, IRolDao rolDao) {
        this.menuItemDao = menuItemDao;
        this.rolPermisoDao = rolPermisoDao;
        this.rolDao = rolDao;
    }

    @Override
    @Transactional
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        log.info("Creando ítem de menú: {}", request.getNombreItem());

        if (menuItemDao.existsByNombreItem(request.getNombreItem())) {
            throw new DuplicateResourceException("Ya existe un ítem de menú con el nombre '" + request.getNombreItem() + "'.");
        }

        // Validar si el parentId existe, si se proporciona
        if (request.getParentId() != null && !menuItemDao.existsById(request.getParentId())) {
            throw new ResourceNotFoundException("El ítem padre con ID " + request.getParentId() + " no fue encontrado.");
        }

        MenuItem menuItem = MenuItemMapper.toModel(request);
        try {
            MenuItem savedItem = menuItemDao.save(menuItem);
            log.info("Ítem de menú con ID {} creado exitosamente.", savedItem.getId());
            return MenuItemMapper.toResponse(savedItem);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al crear ítem de menú: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error al crear el ítem de menú. Verifique los datos e intente de nuevo.");
        }
    }

    @Override
    public MenuItemResponse getMenuItemById(Long id) {
        log.debug("Buscando ítem de menú con ID: {}", id);
        MenuItem menuItem = menuItemDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + id));
        return MenuItemMapper.toResponse(menuItem);
    }

    @Override
    public List<MenuItemResponse> getAllMenuItems() {
        log.debug("Obteniendo todos los ítems de menú.");
        List<MenuItem> allItems = menuItemDao.findAll();

        // Convertir a DTOs
        List<MenuItemResponse> allResponses = allItems.stream()
                .map(MenuItemMapper::toResponse)
                .collect(Collectors.toList());

        // Opcional: Construir una estructura de árbol aquí si es necesario para el frontend
        // Para una estructura de árbol, puedes procesar esta lista.
        // Dejo la lógica de construcción de árbol fuera del service para mantenerlo más enfocado en CRUD.
        // El Controller o una utilidad de frontend se encargaría de esto.
        return allResponses;
    }

    @Override
    public List<MenuItemResponse> getRootMenuItems() {
        log.debug("Obteniendo ítems de menú raíz.");
        List<MenuItem> rootItems = menuItemDao.findAll().stream()
                .filter(item -> item.getParentId() == null)
                .sorted(Comparator.comparing(MenuItem::getOrden))
                .collect(Collectors.toList());

        // Mapear a DTOs y construir sub-items recursivamente
        return rootItems.stream()
                .map(item -> {
                    MenuItemResponse response = MenuItemMapper.toResponse(item);
                    response.setSubItems(buildMenuItemTree(item.getId(), menuItemDao.findAll())); // Podríamos optimizar esto
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> getSubMenuItems(Long parentId) {
        log.debug("Obteniendo sub-ítems para parentId: {}", parentId);
        // Validar que el parentId exista
        if (!menuItemDao.existsById(parentId)) {
            throw new ResourceNotFoundException("El ítem padre con ID " + parentId + " no fue encontrado.");
        }
        return menuItemDao.findByParentId(parentId).stream()
                .map(MenuItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request) {
        log.info("Actualizando ítem de menú con ID: {}", id);
        MenuItem existingItem = menuItemDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + id));

        // Verificar si el nuevo nombre del ítem ya existe y es diferente al actual
        if (!existingItem.getNombreItem().equals(request.getNombreItem()) &&
                menuItemDao.existsByNombreItem(request.getNombreItem())) {
            throw new DuplicateResourceException("Ya existe otro ítem de menú con el nombre '" + request.getNombreItem() + "'.");
        }

        // Validar si el nuevo parentId existe, si se proporciona y es diferente
        if (request.getParentId() != null && !request.getParentId().equals(existingItem.getParentId())) {
            if (!menuItemDao.existsById(request.getParentId())) {
                throw new ResourceNotFoundException("El nuevo ítem padre con ID " + request.getParentId() + " no fue encontrado.");
            }
            // Prevenir ciclos en el árbol de menú (ej. un ítem no puede ser su propio padre o sub-padre)
            // Una verificación más robusta implicaría una consulta recursiva en la BD o en memoria.
            if (id.equals(request.getParentId())) {
                throw new IllegalArgumentException("Un ítem de menú no puede ser su propio padre.");
            }
            // Aquí podríamos añadir lógica para evitar ciclos más profundos si el árbol es muy grande y dinámico.
        }

        existingItem.setParentId(request.getParentId());
        existingItem.setNombreItem(request.getNombreItem());
        existingItem.setRuta(request.getRuta());
        existingItem.setIcono(request.getIcono());
        existingItem.setOrden(request.getOrden());

        try {
            MenuItem updatedItem = menuItemDao.update(existingItem);
            log.info("Ítem de menú con ID {} actualizado exitosamente.", id);
            return MenuItemMapper.toResponse(updatedItem);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al actualizar ítem de menú con ID {}: {}", id, e.getMessage(), e);
            throw new IllegalArgumentException("Error al actualizar el ítem de menú. Verifique los datos e intente de nuevo.");
        }
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        log.info("Intentando eliminar ítem de menú con ID: {}", id);
        if (!menuItemDao.existsById(id)) {
            throw new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + id);
        }
        // Primero, eliminar cualquier permiso asociado a este menuItem
        rolPermisoDao.deleteByMenuItemId(id);
        // Segundo, actualizar los items que lo tienen como parent_id a null o reasignarlos
        // Por simplicidad, los items hijos quedarán sin padre (parent_id = null)
        updateChildrenParentToNull(id);

        boolean deleted = menuItemDao.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Fallo al eliminar el ítem de menú con ID: " + id);
        }
        log.info("Ítem de menú con ID {} eliminado exitosamente.", id);
    }

    /**
     * Helper method to update children's parent_id to null when a parent is deleted.
     */
    private void updateChildrenParentToNull(Long parentId) {
        List<MenuItem> children = menuItemDao.findByParentId(parentId);
        for (MenuItem child : children) {
            child.setParentId(null);
            menuItemDao.update(child); // Actualizar cada hijo
        }
    }

    /**
     * Helper method to recursively build the menu tree.
     * This is a simple in-memory recursive approach. For very large menus, consider
     * a specialized DB query (e.g., WITH RECURSIVE) or a more optimized approach.
     */
    private List<MenuItemResponse> buildMenuItemTree(Long parentId, List<MenuItem> allMenuItems) {
        return allMenuItems.stream()
                .filter(item -> Objects.equals(item.getParentId(), parentId))
                .sorted(Comparator.comparing(MenuItem::getOrden))
                .map(item -> {
                    MenuItemResponse response = MenuItemMapper.toResponse(item);
                    List<MenuItemResponse> children = buildMenuItemTree(item.getId(), allMenuItems);
                    response.setSubItems(children.isEmpty() ? null : children); // Set null if no sub-items
                    return response;
                })
                .collect(Collectors.toList());
    }

    // Métodos para RolPermiso (asignación/revocación de permisos)
    @Override
    @Transactional
    public void assignPermissionToRole(Long rolId, Long menuItemId) {
        log.info("Asignando permiso de MenuItem ID {} a Rol ID {}.", menuItemId, rolId);
        // Validar que el rol y el menuItem existan
        if (!rolDao.existsById(rolId)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
        }
        if (!menuItemDao.existsById(menuItemId)) {
            throw new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + menuItemId);
        }
        if (rolPermisoDao.existsPermission(rolId, menuItemId)) {
            throw new DuplicateResourceException("El permiso para el Rol ID " + rolId + " y MenuItem ID " + menuItemId + " ya existe.");
        }
        try {
            rolPermisoDao.assignPermission(rolId, menuItemId);
            log.info("Permiso de MenuItem ID {} asignado a Rol ID {}.", menuItemId, rolId);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al asignar permiso: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error al asignar el permiso. Verifique los IDs e intente de nuevo.");
        }
    }

    @Override
    @Transactional
    public void revokePermissionFromRole(Long rolId, Long menuItemId) {
        log.info("Revocando permiso de MenuItem ID {} de Rol ID {}.", menuItemId, rolId);
        // Opcional: Validar que el permiso realmente exista antes de intentar borrar.
        // Si no existe, simplemente el método delete no hará nada y no lanzará error.
        // if (!rolPermisoDao.existsPermission(rolId, menuItemId)) {
        //     throw new ResourceNotFoundException("El permiso para el Rol ID " + rolId + " y MenuItem ID " + menuItemId + " no existe.");
        // }
        rolPermisoDao.revokePermission(rolId, menuItemId);
        log.info("Permiso de MenuItem ID {} revocado de Rol ID {}.", menuItemId, rolId);
    }

    @Override
    public List<Long> getMenuItemIdsByRoleId(Long rolId) {
        log.debug("Obteniendo IDs de ítems de menú para Rol ID: {}", rolId);
        if (!rolDao.existsById(rolId)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
        }
        return rolPermisoDao.findByRolId(rolId).stream()
                .map(rp -> rp.getMenuItemId())
                .collect(Collectors.toList());
    }

    @Override
    public MenuItemResponse getMenuItemByNombre(String nombreItem) {
        log.debug("Buscando ítem de menú con nombre: {}", nombreItem);
        MenuItem menuItem = menuItemDao.findByNombreItem(nombreItem)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de menú no encontrado con nombre: " + nombreItem));
        return MenuItemMapper.toResponse(menuItem);
    }

    @Override
    @Transactional
    public void assignMenuItemToRole(Long rolId, Long menuItemId) {
        log.info("Asignando MenuItem ID {} a Rol ID {}.", menuItemId, rolId);
        if (!rolDao.existsById(rolId)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
        }
        if (!menuItemDao.existsById(menuItemId)) {
            throw new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + menuItemId);
        }
        if (rolPermisoDao.existsPermission(rolId, menuItemId)) {
            throw new DuplicateResourceException("El permiso para el Rol ID " + rolId + " y MenuItem ID " + menuItemId + " ya existe.");
        }
        try {
            rolPermisoDao.assignPermission(rolId, menuItemId);
            log.info("MenuItem ID {} asignado a Rol ID {}.", menuItemId, rolId);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al asignar MenuItem a Rol: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error al asignar el ítem de menú al rol. Verifique los IDs e intente de nuevo.");
        }
    }

    @Override
    @Transactional
    public void revokeMenuItemFromRole(Long rolId, Long menuItemId) {
        log.info("Revocando MenuItem ID {} de Rol ID {}.", menuItemId, rolId);
        rolPermisoDao.revokePermission(rolId, menuItemId);
        log.info("MenuItem ID {} revocado de Rol ID {}.", menuItemId, rolId);
    }

    @Override
    public List<MenuItemResponse> getMenuItemsByRolId(Long rolId) {
        log.debug("Obteniendo ítems de menú para Rol ID: {}", rolId);
        if (!rolDao.existsById(rolId)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
        }
        List<Long> menuItemIds = rolPermisoDao.findByRolId(rolId).stream()
                .map(rp -> rp.getMenuItemId())
                .collect(Collectors.toList());
        List<MenuItem> menuItems = menuItemDao.findAllById(menuItemIds);
        return menuItems.stream()
                .map(MenuItemMapper::toResponse)
                .collect(Collectors.toList());
    }
}
