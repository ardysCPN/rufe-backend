package co.rufe.rufe.dao;

import co.rufe.rufe.model.Organizacion;

import java.util.List;
import java.util.Optional;

public interface IOrganizacionDao {
    Organizacion save(Organizacion organizacion);
    Optional<Organizacion> findById(Long id);
    Optional<Organizacion> findByNombreOrganizacion(String nombreOrganizacion);
    List<Organizacion> findAll();
    Organizacion update(Organizacion organizacion);
    boolean deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByNombreOrganizacion(String nombreOrganizacion);
}
