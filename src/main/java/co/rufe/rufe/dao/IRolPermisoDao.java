package co.rufe.rufe.dao;

import co.rufe.rufe.model.RolPermiso;

import java.util.List;

public interface IRolPermisoDao {
    void assignPermission(Long rolId, Integer permisoId); 
    void revokePermission(Long rolId, Integer permisoId); 
    List<RolPermiso> findByRolId(Long rolId);
    boolean existsPermission(Long rolId, Integer permisoId); 
    boolean deleteByPermisoId(Integer permisoId); 
    boolean deleteByRolId(Long rolId); 
}
