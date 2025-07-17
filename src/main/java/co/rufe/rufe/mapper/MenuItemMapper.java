package co.rufe.rufe.mapper;

import co.rufe.rufe.dto.menu.MenuItemRequest;
import co.rufe.rufe.dto.menu.MenuItemResponse;
import co.rufe.rufe.model.MenuItem;

public class MenuItemMapper {

    public static MenuItem toModel(MenuItemRequest request) {
        if (request == null) {
            return null;
        }
        MenuItem menuItem = new MenuItem();
        menuItem.setParentId(request.getParentId());
        menuItem.setNombreItem(request.getNombreItem());
        menuItem.setRuta(request.getRuta());
        menuItem.setIcono(request.getIcono());
        menuItem.setOrden(request.getOrden());
        return menuItem;
    }

    public static MenuItemResponse toResponse(MenuItem menuItem) {
        if (menuItem == null) {
            return null;
        }
        MenuItemResponse response = new MenuItemResponse();
        response.setId(menuItem.getId());
        response.setParentId(menuItem.getParentId());
        response.setNombreItem(menuItem.getNombreItem());
        response.setRuta(menuItem.getRuta());
        response.setIcono(menuItem.getIcono());
        response.setOrden(menuItem.getOrden());
        // El campo 'subItems' se llenará en la lógica de servicio/controlador si se construye un árbol
        return response;
    }
}
