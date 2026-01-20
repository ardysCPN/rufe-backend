package co.rufe.rufe.service.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.rufe.rufe.dao.IMenuItemDao;
import co.rufe.rufe.dao.IMenuItemPermisoDao; // Nueva inyección
import co.rufe.rufe.dao.IPermisoDao;       // Nueva inyección (para obtener los PermisoResponse)
import co.rufe.rufe.dto.menu.MenuItemRequest;
import co.rufe.rufe.dto.menu.MenuItemResponse;
import co.rufe.rufe.dto.permiso.PermisoResponse;
import co.rufe.rufe.exception.DuplicateResourceException;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.mapper.MenuItemMapper;
import co.rufe.rufe.mapper.PermisoMapper; // Necesario para mapear Permiso a PermisoResponse
import co.rufe.rufe.model.MenuItem;
import co.rufe.rufe.model.MenuItemPermiso;
import co.rufe.rufe.model.Permiso;
import co.rufe.rufe.service.IMenuItemService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MenuItemServiceImpl implements IMenuItemService {

    private final IMenuItemDao menuItemDao;
    private final IMenuItemPermisoDao menuItemPermisoDao; // Nuevo
    private final IPermisoDao permisoDao; // Nuevo

    public MenuItemServiceImpl(IMenuItemDao menuItemDao, IMenuItemPermisoDao menuItemPermisoDao, IPermisoDao permisoDao) {
        this.menuItemDao = menuItemDao;
        this.menuItemPermisoDao = menuItemPermisoDao;
        this.permisoDao = permisoDao;
    }

    @Override
    @Transactional
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        log.info("Creando ítem de menú: {}", request.getNombreItem());

        // La unicidad del nombre puede ser por parentId si quieres nombres duplicados en diferentes ramas.
        // Aquí asumimos que nombreItem debe ser único globalmente.
        // Si quieres que sea único por parentId, modifica esta validación y el DAO.
        if (menuItemDao.findByNombreItemAndParentId(request.getNombreItem(), request.getParentId()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un ítem de menú con el nombre '" + request.getNombreItem() + "' bajo este padre.");
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
    public MenuItemResponse getMenuItemById(Integer id) { // ID a Integer
        log.debug("Buscando ítem de menú con ID: {}", id);
        MenuItem menuItem = menuItemDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + id));
        return MenuItemMapper.toResponse(menuItem);
    }

    @Override
    public List<MenuItemResponse> getAllMenuItems() {
        log.debug("Obteniendo todos los ítems de menú.");
        List<MenuItem> allItems = menuItemDao.findAll();
        return allItems.stream()
                .map(MenuItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> getRootMenuItems() {
        log.debug("Obteniendo ítems de menú raíz y sus sub-ítems.");
        List<MenuItem> allMenuItems = menuItemDao.findAll(); // Obtener todos los ítems una vez

        return allMenuItems.stream()
                .filter(item -> item.getParentId() == null)
                .sorted(Comparator.comparing(MenuItem::getOrden))
                .map(item -> {
                    MenuItemResponse response = MenuItemMapper.toResponse(item);
                    response.setSubItems(buildMenuItemTree(item.getId(), allMenuItems));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> getSubMenuItems(Integer parentId) { // ID a Integer
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
    public MenuItemResponse updateMenuItem(Integer id, MenuItemRequest request) { // ID a Integer
        log.info("Actualizando ítem de menú con ID: {}", id);
        MenuItem existingItem = menuItemDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + id));

        // Verificar si el nuevo nombre del ítem ya existe bajo el mismo padre y es diferente al actual
        if (!existingItem.getNombreItem().equals(request.getNombreItem())) {
            if (menuItemDao.findByNombreItemAndParentId(request.getNombreItem(), request.getParentId()).isPresent()) {
                throw new DuplicateResourceException("Ya existe otro ítem de menú con el nombre '" + request.getNombreItem() + "' bajo este padre.");
            }
        }

        // Validar si el nuevo parentId existe, si se proporciona y es diferente
        if (request.getParentId() != null && !Objects.equals(request.getParentId(), existingItem.getParentId())) { // Usar Objects.equals para manejar nulls
            if (!menuItemDao.existsById(request.getParentId())) {
                throw new ResourceNotFoundException("El nuevo ítem padre con ID " + request.getParentId() + " no fue encontrado.");
            }
            // Prevenir ciclos en el árbol de menú (ej. un ítem no puede ser su propio padre o sub-padre)
            if (id.equals(request.getParentId())) {
                throw new IllegalArgumentException("Un ítem de menú no puede ser su propio padre.");
            }
            // Aquí podrías añadir una verificación más robusta para evitar ciclos si es necesario.
            // Para ello, necesitarías una función recursiva que verifique si el 'id' es un ancestro de 'request.getParentId()'.
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
    public void deleteMenuItem(Integer id) { // ID a Integer
        log.info("Intentando eliminar ítem de menú con ID: {}", id);
        if (!menuItemDao.existsById(id)) {
            throw new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + id);
        }
        // Primero, eliminar las relaciones en menu_item_permisos para este menuItem
        menuItemPermisoDao.deleteByMenuItemId(id);
        log.debug("Relaciones de permisos para MenuItem ID {} eliminadas.", id);

        // Segundo, actualizar los items que lo tienen como parent_id a null
        updateChildrenParentToNull(id);
        log.debug("Hijos de MenuItem ID {} actualizados a parent_id = null.", id);

        boolean deleted = menuItemDao.deleteById(id);
        if (!deleted) { // El DAO podría no lanzar excepción si no encuentra el ID, sino retornar false
            throw new ResourceNotFoundException("Fallo al eliminar el ítem de menú con ID: " + id + ". Posiblemente no existe o hubo un problema en la base de datos.");
        }
        log.info("Ítem de menú con ID {} eliminado exitosamente.", id);
    }

    /**
     * Helper method to update children's parent_id to null when a parent is deleted.
     */
    private void updateChildrenParentToNull(Integer parentId) { // ID a Integer
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
    private List<MenuItemResponse> buildMenuItemTree(Integer parentId, List<MenuItem> allMenuItems) { // ID a Integer
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

    @Override
    public MenuItemResponse getMenuItemByNombre(String nombreItem) {
        log.debug("Buscando ítem de menú con nombre: {}", nombreItem);
        MenuItem menuItem = menuItemDao.findByNombreItem(nombreItem)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de menú no encontrado con nombre: " + nombreItem));
        return MenuItemMapper.toResponse(menuItem);
    }

    // --- Métodos para gestionar Permisos ASOCIADOS a un MenuItem (Visibilidad) ---
    @Override
    @Transactional
    public void assignPermisoToMenuItem(Integer menuItemId, Integer permisoId) {
        log.info("Asignando Permiso ID {} a MenuItem ID {}.", permisoId, menuItemId);
        // Validar que el MenuItem y el Permiso existan
        if (!menuItemDao.existsById(menuItemId)) {
            throw new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + menuItemId);
        }
        if (!permisoDao.existsById(permisoId)) {
            throw new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId);
        }
        if (menuItemPermisoDao.existsMenuItemPermiso(menuItemId, permisoId)) {
            throw new DuplicateResourceException("La relación entre MenuItem ID " + menuItemId + " y Permiso ID " + permisoId + " ya existe.");
        }
        try {
            menuItemPermisoDao.assignMenuItemPermiso(menuItemId, permisoId);
            log.info("Permiso ID {} asignado a MenuItem ID {}.", permisoId, menuItemId);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al asignar permiso a MenuItem: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error al asignar el permiso al ítem de menú. Verifique los IDs e intente de nuevo.");
        }
    }

    @Override
    @Transactional
    public void revokePermisoFromMenuItem(Integer menuItemId, Integer permisoId) {
        log.info("Revocando Permiso ID {} de MenuItem ID {}.", permisoId, menuItemId);
        // La validación de existencia es opcional aquí. deleteMenuItemPermiso manejará si no existe.
        menuItemPermisoDao.revokeMenuItemPermiso(menuItemId, permisoId);
        log.info("Permiso ID {} revocado de MenuItem ID {}.", permisoId, menuItemId);
    }

    @Override
    public List<PermisoResponse> getPermisosByMenuItemId(Integer menuItemId) {
        log.debug("Obteniendo Permisos para MenuItem ID: {}", menuItemId);
        if (!menuItemDao.existsById(menuItemId)) {
            throw new ResourceNotFoundException("Ítem de menú no encontrado con ID: " + menuItemId);
        }
        List<MenuItemPermiso> menuItemPermisos = menuItemPermisoDao.findByMenuItemId(menuItemId);
        List<Integer> permisoIds = menuItemPermisos.stream()
                .map(MenuItemPermiso::getPermisoId)
                .collect(Collectors.toList());

        List<Permiso> permisos = permisoDao.findAllById(permisoIds);
        return permisos.stream()
                .map(PermisoMapper::toResponse)
                .collect(Collectors.toList());
    }

    // --- Método para obtener el menú dinámico basado en los permisos del usuario ---
    @Override
    public List<MenuItemResponse> getDynamicMenuForUser(Collection<String> userPermissions) {
        log.debug("Generando menú dinámico para usuario con permisos: {}", userPermissions);

        // 1. Obtener todos los ítems de menú activos y sus permisos asociados
        // Podríamos tener un DAO query específico para esto si la DB lo permite eficientemente
        List<MenuItem> allMenuItems = menuItemDao.findAll();

        // 2. Filtrar los MenuItem que el usuario puede ver
        // Un MenuItem es visible si:
        // a) No tiene ningún permiso asociado (visible para todos autenticados)
        // b) Tiene permisos asociados Y el usuario posee AL MENOS UNO de esos permisos.
        Set<Integer> visibleMenuItemIds = new HashSet<>();

        for (MenuItem item : allMenuItems) {
            // Obtener los IDs de los permisos requeridos para este MenuItem
            List<MenuItemPermiso> requiredMenuItemPermisos = menuItemPermisoDao.findByMenuItemId(item.getId());

            // Si el MenuItem no tiene permisos asignados, es visible por defecto para el usuario autenticado
            if (requiredMenuItemPermisos.isEmpty()) {
                // visibleMenuItemIds.add(item.getId());  // validar, ya que no se puede mostrar un MenuItem sin permisos
                continue;
            }

            // Si tiene permisos, verificar si el usuario tiene al menos uno de ellos
            boolean hasRequiredPermission = requiredMenuItemPermisos.stream()
                .map(MenuItemPermiso::getPermisoId)
                .anyMatch(permisoId -> {
                    Optional<Permiso> permisoOptional = permisoDao.findById(permisoId);
                    return permisoOptional.isPresent() && userPermissions.contains(permisoOptional.get().getNombrePermiso());
                });

            if (hasRequiredPermission) {
                visibleMenuItemIds.add(item.getId());
            }
        }

        // 3. Incluir padres de ítems visibles que quizás no tengan permisos directos
        // Esto asegura que la estructura del árbol se mantenga.
        Set<Integer> finalVisibleMenuItemIds = new HashSet<>(visibleMenuItemIds);
        for (MenuItem item : allMenuItems) {
            if (visibleMenuItemIds.contains(item.getId())) {
                Integer parentId = item.getParentId();
                while (parentId != null) {
                    finalVisibleMenuItemIds.add(parentId);
                    MenuItem parentItem = null;
                    for (MenuItem m : allMenuItems) {
                        if (m.getId().equals(parentId)) {
                            parentItem = m;
                            break;
                        }
                    }
                    parentId = (parentItem != null) ? parentItem.getParentId() : null;
                }
            }
        }


        // 4. Construir el árbol de menú solo con los ítems visibles
        List<MenuItem> filteredMenuItems = allMenuItems.stream()
                .filter(item -> finalVisibleMenuItemIds.contains(item.getId()))
                .collect(Collectors.toList());

        // 5. Devolver solo los ítems raíz del menú visible, con sus sub-ítems recursivamente
        return filteredMenuItems.stream()
                .filter(item -> item.getParentId() == null) // Solo ítems de nivel superior
                .sorted(Comparator.comparing(MenuItem::getOrden))
                .map(item -> {
                    MenuItemResponse response = MenuItemMapper.toResponse(item);
                    // Construir recursivamente los sub-ítems filtrados
                    response.setSubItems(buildFilteredMenuItemTree(item.getId(), filteredMenuItems));
                    return response;
                })
                .collect(Collectors.toList());
    }


    /**
     * Helper method to recursively build the menu tree for filtered items.
     */
    private List<MenuItemResponse> buildFilteredMenuItemTree(Integer parentId, List<MenuItem> filteredMenuItems) {
        return filteredMenuItems.stream()
                .filter(item -> Objects.equals(item.getParentId(), parentId))
                .sorted(Comparator.comparing(MenuItem::getOrden))
                .map(item -> {
                    MenuItemResponse response = MenuItemMapper.toResponse(item);
                    List<MenuItemResponse> children = buildFilteredMenuItemTree(item.getId(), filteredMenuItems);
                    response.setSubItems(children.isEmpty() ? null : children);
                    return response;
                })
                .collect(Collectors.toList());
    }
}