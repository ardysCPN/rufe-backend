package co.rufe.rufe.service;

import co.rufe.rufe.dto.menu.MenuDTO;
import java.util.List;

public interface IMenuService {
    List<MenuDTO> getMenuByRolId(Long rolId);

    List<MenuDTO> getAllMenus();

    void updateRoleMenus(Long rolId, List<Long> menuIds);
}
