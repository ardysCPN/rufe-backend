package co.rufe.rufe.service.impl;

import co.rufe.rufe.dao.IMenuDao;
import co.rufe.rufe.dto.menu.MenuDTO;
import co.rufe.rufe.model.Menu;
import co.rufe.rufe.service.IMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements IMenuService {

    private final IMenuDao menuDao;

    public MenuServiceImpl(IMenuDao menuDao) {
        this.menuDao = menuDao;
    }

    @Override
    public List<MenuDTO> getMenuByRolId(Long rolId) {
        List<Menu> allMenuItems = menuDao.findByRolId(rolId);
        return buildMenuTree(allMenuItems);
    }

    private List<MenuDTO> buildMenuTree(List<Menu> allItems) {
        // Mapear DTOs
        List<MenuDTO> allDTOs = allItems.stream().map(this::toDTO).collect(Collectors.toList());

        // Agrupar por parentId (idMenu)
        Map<Long, List<MenuDTO>> childrenMap = allItems.stream()
                .filter(item -> item.getIdMenu() != null)
                .map(this::toDTO)
                .collect(Collectors.groupingBy(dto -> {
                    /*
                     * Recuperamos el parent ID del modelo original dado que el DTO no lo tiene
                     * explícito,
                     * pero necesitamos una forma más limpia.
                     * 
                     * Mejor estrategia: Map<Id, DTO> y Map<ParentId, List<DTO>>
                     */
                    return allItems.stream()
                            .filter(m -> m.getId().equals(dto.getId()))
                            .findFirst()
                            .map(Menu::getIdMenu)
                            .orElse(0L);
                }));

        // Estrategia simplificada:
        // 1. Convertir todos a DTOs y guardarlos en un mapa ID -> DTO
        Map<Long, MenuDTO> dtoMap = allItems.stream()
                .collect(Collectors.toMap(Menu::getId, this::toDTO));

        List<MenuDTO> rootNodes = new ArrayList<>();

        // 2. Iterar sobre los items originales para armar la jerarquía
        for (Menu item : allItems) {
            MenuDTO currentDTO = dtoMap.get(item.getId());
            if (item.getIdMenu() == null) {
                // Es raíz
                rootNodes.add(currentDTO);
            } else {
                // Es hijo
                MenuDTO parentDTO = dtoMap.get(item.getIdMenu());
                if (parentDTO != null) {
                    if (parentDTO.getChildren() == null) {
                        parentDTO.setChildren(new ArrayList<>());
                    }
                    parentDTO.getChildren().add(currentDTO);
                }
            }
        }

        // 3. Ordenar (si se requiere, el SQL ya ordena, pero los hijos necesitan orden
        // también)
        sortRecursively(rootNodes);

        return rootNodes;
    }

    // Método helper para ordenamiento recursivo
    private void sortRecursively(List<MenuDTO> nodes) {
        if (nodes == null)
            return;

        // Ordenar nodos actuales
        nodes.sort(Comparator.comparing(MenuDTO::getOrden));

        // Ordenar hijos
        for (MenuDTO node : nodes) {
            sortRecursively(node.getChildren());
        }
    }

    private MenuDTO toDTO(Menu menu) {
        return MenuDTO.builder()
                .id(menu.getId())
                .nombre(menu.getNombreOpcion())
                .ruta(menu.getRouterUrl())
                .icono(menu.getIcono())
                .orden(menu.getOrden())
                .build();
    }
}
