package co.rufe.rufe.dao;

import co.rufe.rufe.model.Rol;

import java.util.List;
import java.util.Optional;

public interface IRolDao {
    Rol save(Rol rol);
    Optional<Rol> findById(Long id);
    Optional<Rol> findByOrganizacionIdAndNombreRol(Long organizacionId, String nombreRol);
    List<Rol> findByOrganizacionId(Long organizacionId);
    Rol update(Rol rol);
    boolean deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByOrganizacionIdAndNombreRol(Long organizacionId, String nombreRol);
    Optional<Rol> findByNombreRolAndOrganizacionId(String nombre, Long organizacionId);
}
