// C:\microservicio-rufe\rufe\src\main\java\co\rufe\rufe\dao\IPermisoDao.java
package co.rufe.rufe.dao;

import co.rufe.rufe.model.Permiso;
import java.util.List;
import java.util.Optional;

public interface IPermisoDao {

    Permiso save(Permiso permiso);
    Optional<Permiso> findById(Integer id);
    List<Permiso> findAll();
    Optional<Permiso> findByNombrePermiso(String nombrePermiso); // Para buscar por el nombre único del permiso
    boolean existsByNombrePermiso(String nombrePermiso);
    boolean existsById(Integer id);
    Permiso update(Permiso permiso);
    boolean deleteById(Integer id);
    List<Permiso> findAllById(List<Integer> ids); // Para obtener múltiples permisos por sus IDs
    List<Permiso> findByRolId(Long id);
}
