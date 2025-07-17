// C:\microservicio-rufe\rufe\src\main\java\co\rufe\rufe\dao\IPermisoDao.java
package co.rufe.rufe.dao;

import co.rufe.rufe.model.Permiso;
import java.util.List;
import java.util.Optional;

public interface IPermisoDao {
    Optional<Permiso> findById(Long id);
    Optional<Permiso> findByNombrePermiso(String nombrePermiso);
    List<Permiso> findAll();
    List<Permiso> findByRolId(Long rolId); // Método clave para la autenticación
    Permiso save(Permiso permiso);
    void delete(Long id);
}
