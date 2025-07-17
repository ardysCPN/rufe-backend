package co.rufe.rufe.dao;

import co.rufe.rufe.model.RolPermiso;

import java.util.List;

public interface IRolPermisoDao {
    void assignPermission(Long rolId, Integer permisoId); // <-- Cambiado a Integer permisoId
    void revokePermission(Long rolId, Integer permisoId); // <-- Cambiado a Integer permisoId
    List<RolPermiso> findByRolId(Long rolId);
    boolean existsPermission(Long rolId, Integer permisoId); // <-- Cambiado a Integer permisoId
    boolean deleteByPermisoId(Integer permisoId); // <-- Nuevo método para limpiar si se elimina un permiso
    boolean deleteByRolId(Long rolId); // <-- Nuevo método para limpiar si se elimina un rol
}
