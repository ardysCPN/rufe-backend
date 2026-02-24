package co.rufe.rufe.dao;

import java.util.List;

public interface IMenuRolesDao {
    void assignMenuToRol(Long rolId, Long menuId);

    void revokeMenuFromRol(Long rolId, Long menuId);

    void deleteByRolId(Long rolId);

    List<Long> findMenuIdsByRolId(Long rolId);
}
