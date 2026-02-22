package co.rufe.rufe.service;

import java.util.List;
import java.util.Optional;

import co.rufe.rufe.dto.catalogo.CatalogoItemResponse;
import co.rufe.rufe.dto.catalogo.CatalogoMunicipioResponse;

public interface ICatalogoRufeService {

    // TipoUbicacionBien
    List<CatalogoItemResponse> getAllTipoUbicacionBien();

    Optional<CatalogoItemResponse> getTipoUbicacionBienById(Integer id);

    // TipoAlojamientoActual
    List<CatalogoItemResponse> getAllTipoAlojamientoActual();

    Optional<CatalogoItemResponse> getTipoAlojamientoActualById(Integer id);

    // FormaTenenciaBien
    List<CatalogoItemResponse> getAllFormaTenenciaBien();

    Optional<CatalogoItemResponse> getFormaTenenciaBienById(Integer id);

    // EstadoBien
    List<CatalogoItemResponse> getAllEstadoBien();

    Optional<CatalogoItemResponse> getEstadoBienById(Integer id);

    // TipoBien
    List<CatalogoItemResponse> getAllTipoBien();

    Optional<CatalogoItemResponse> getTipoBienById(Integer id);

    // TipoDocumento
    List<CatalogoItemResponse> getAllTipoDocumento();

    Optional<CatalogoItemResponse> getTipoDocumentoById(Integer id);

    // Parentesco
    List<CatalogoItemResponse> getAllParentesco();

    Optional<CatalogoItemResponse> getParentescoById(Integer id);

    // Genero
    List<CatalogoItemResponse> getAllGenero();

    Optional<CatalogoItemResponse> getGeneroById(Integer id);

    // PertenenciaEtnica
    List<CatalogoItemResponse> getAllPertenenciaEtnica();

    Optional<CatalogoItemResponse> getPertenenciaEtnicaById(Integer id);

    // Departamentos
    List<CatalogoItemResponse> getAllDepartamentos();

    // Municipios
    List<CatalogoMunicipioResponse> getAllMunicipios();

    // Eventos
    List<CatalogoItemResponse> getAllEventos();

    // Generic CRUD for catalogs
    void createItem(String catalogo, String nombre);

    void updateItem(String catalogo, Integer id, String nombre);

    void deleteItem(String catalogo, Integer id);
}
