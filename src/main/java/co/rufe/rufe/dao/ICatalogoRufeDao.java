package co.rufe.rufe.dao;

import co.rufe.rufe.model.catalogo.*; // Importa todos los modelos de catálogo
import java.util.List;
import java.util.Optional;

public interface ICatalogoRufeDao {

    // TipoUbicacionBien
    List<TipoUbicacionBien> findAllTipoUbicacionBien();

    Optional<TipoUbicacionBien> findTipoUbicacionBienById(Integer id);

    // TipoAlojamientoActual
    List<TipoAlojamientoActual> findAllTipoAlojamientoActual();

    Optional<TipoAlojamientoActual> findTipoAlojamientoActualById(Integer id);

    // FormaTenenciaBien
    List<FormaTenenciaBien> findAllFormaTenenciaBien();

    Optional<FormaTenenciaBien> findFormaTenenciaBienById(Integer id);

    // EstadoBien
    List<EstadoBien> findAllEstadoBien();

    Optional<EstadoBien> findEstadoBienById(Integer id);

    // TipoBien
    List<TipoBien> findAllTipoBien();

    Optional<TipoBien> findTipoBienById(Integer id);

    // TipoDocumento
    List<TipoDocumento> findAllTipoDocumento();

    Optional<TipoDocumento> findTipoDocumentoById(Integer id);

    // Parentesco
    List<Parentesco> findAllParentesco();

    Optional<Parentesco> findParentescoById(Integer id);

    // Genero
    List<Genero> findAllGenero();

    Optional<Genero> findGeneroById(Integer id);

    // PertenenciaEtnica
    List<PertenenciaEtnica> findAllPertenenciaEtnica();

    Optional<PertenenciaEtnica> findPertenenciaEtnicaById(Integer id);

    List<Departamento> findAllDepartamentos();

    List<Municipio> findAllMunicipios();

    List<Evento> findAllEventos();

    // Generic CRUD for catalogs
    void create(String tableName, String nombre);

    void update(String tableName, Integer id, String nombre);

    void delete(String tableName, Integer id);
}
