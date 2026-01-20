package co.rufe.rufe.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.rufe.rufe.dao.ICatalogoRufeDao;
import co.rufe.rufe.dto.catalogo.CatalogoItemResponse;
import co.rufe.rufe.dto.catalogo.CatalogoMunicipioResponse;
// Importa todos los modelos de catálogo
import co.rufe.rufe.model.catalogo.Departamento;
import co.rufe.rufe.model.catalogo.EstadoBien;
import co.rufe.rufe.model.catalogo.Evento;
import co.rufe.rufe.model.catalogo.FormaTenenciaBien;
import co.rufe.rufe.model.catalogo.Genero;
import co.rufe.rufe.model.catalogo.Municipio;
import co.rufe.rufe.model.catalogo.Parentesco;
import co.rufe.rufe.model.catalogo.PertenenciaEtnica;
import co.rufe.rufe.model.catalogo.TipoAlojamientoActual;
import co.rufe.rufe.model.catalogo.TipoBien;
import co.rufe.rufe.model.catalogo.TipoDocumento;
import co.rufe.rufe.model.catalogo.TipoUbicacionBien;
import co.rufe.rufe.service.ICatalogoRufeService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CatalogoRufeServiceImpl implements ICatalogoRufeService {

    private final ICatalogoRufeDao catalogoRufeDao;

    public CatalogoRufeServiceImpl(ICatalogoRufeDao catalogoRufeDao) {
        this.catalogoRufeDao = catalogoRufeDao;
    }

    // Método auxiliar para mapear Modelo a DTO
    private CatalogoItemResponse mapToResponse(Object model) {
        if (model == null) {
            return null;
        }
        if (model instanceof TipoUbicacionBien) {
            TipoUbicacionBien item = (TipoUbicacionBien) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        } else if (model instanceof TipoAlojamientoActual) {
            TipoAlojamientoActual item = (TipoAlojamientoActual) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        } else if (model instanceof FormaTenenciaBien) {
            FormaTenenciaBien item = (FormaTenenciaBien) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        } else if (model instanceof EstadoBien) {
            EstadoBien item = (EstadoBien) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        } else if (model instanceof TipoBien) {
            TipoBien item = (TipoBien) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        } else if (model instanceof TipoDocumento) {
            TipoDocumento item = (TipoDocumento) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        } else if (model instanceof Parentesco) {
            Parentesco item = (Parentesco) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        } else if (model instanceof Genero) {
            Genero item = (Genero) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        } else if (model instanceof PertenenciaEtnica) {
            PertenenciaEtnica item = (PertenenciaEtnica) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        }else if (model instanceof Departamento) {
            Departamento item = (Departamento) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();            
        }else if (model instanceof Municipio) {
            Municipio item = (Municipio) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        }else if (model instanceof Evento) {
            Evento item = (Evento) model;
            return CatalogoItemResponse.builder().id(item.getId()).nombre(item.getNombre()).build();
        } 
        log.warn("Tipo de modelo de catálogo no reconocido: {}", model.getClass().getName());
        return null;
    }

    // Implementaciones para TipoUbicacionBien
    @Override
    public List<CatalogoItemResponse> getAllTipoUbicacionBien() {
        return catalogoRufeDao.findAllTipoUbicacionBien().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CatalogoItemResponse> getTipoUbicacionBienById(Integer id) {
        return catalogoRufeDao.findTipoUbicacionBienById(id)
                .map(this::mapToResponse);
    }

    // Implementaciones para TipoAlojamientoActual
    @Override
    public List<CatalogoItemResponse> getAllTipoAlojamientoActual() {
        return catalogoRufeDao.findAllTipoAlojamientoActual().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CatalogoItemResponse> getTipoAlojamientoActualById(Integer id) {
        return catalogoRufeDao.findTipoAlojamientoActualById(id)
                .map(this::mapToResponse);
    }

    // Implementaciones para FormaTenenciaBien
    @Override
    public List<CatalogoItemResponse> getAllFormaTenenciaBien() {
        return catalogoRufeDao.findAllFormaTenenciaBien().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CatalogoItemResponse> getFormaTenenciaBienById(Integer id) {
        return catalogoRufeDao.findFormaTenenciaBienById(id)
                .map(this::mapToResponse);
    }

    // Implementaciones para EstadoBien
    @Override
    public List<CatalogoItemResponse> getAllEstadoBien() {
        return catalogoRufeDao.findAllEstadoBien().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CatalogoItemResponse> getEstadoBienById(Integer id) {
        return catalogoRufeDao.findEstadoBienById(id)
                .map(this::mapToResponse);
    }

    // Implementaciones para TipoBien
    @Override
    public List<CatalogoItemResponse> getAllTipoBien() {
        return catalogoRufeDao.findAllTipoBien().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CatalogoItemResponse> getTipoBienById(Integer id) {
        return catalogoRufeDao.findTipoBienById(id)
                .map(this::mapToResponse);
    }

    // Implementaciones para TipoDocumento
    @Override
    public List<CatalogoItemResponse> getAllTipoDocumento() {
        return catalogoRufeDao.findAllTipoDocumento().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CatalogoItemResponse> getTipoDocumentoById(Integer id) {
        return catalogoRufeDao.findTipoDocumentoById(id)
                .map(this::mapToResponse);
    }

    // Implementaciones para Parentesco
    @Override
    public List<CatalogoItemResponse> getAllParentesco() {
        return catalogoRufeDao.findAllParentesco().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CatalogoItemResponse> getParentescoById(Integer id) {
        return catalogoRufeDao.findParentescoById(id)
                .map(this::mapToResponse);
    }

    // Implementaciones para Genero
    @Override
    public List<CatalogoItemResponse> getAllGenero() {
        return catalogoRufeDao.findAllGenero().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CatalogoItemResponse> getGeneroById(Integer id) {
        return catalogoRufeDao.findGeneroById(id)
                .map(this::mapToResponse);
    }

    // Implementaciones para PertenenciaEtnica
    @Override
    public List<CatalogoItemResponse> getAllPertenenciaEtnica() {
        return catalogoRufeDao.findAllPertenenciaEtnica().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CatalogoItemResponse> getPertenenciaEtnicaById(Integer id) {
        return catalogoRufeDao.findPertenenciaEtnicaById(id)
                .map(this::mapToResponse);
    }


    private CatalogoMunicipioResponse mapToMunicipioResponse(Municipio model) {
        if (model == null) return null;
        return CatalogoMunicipioResponse.builder()
                .id(model.getId())
                .nombre(model.getNombre())
                .departamentoId(model.getDepartamentoId())
                .build();
    }

    @Override
    public List<CatalogoItemResponse> getAllDepartamentos() {
        return catalogoRufeDao.findAllDepartamentos().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CatalogoMunicipioResponse> getAllMunicipios() {
        return catalogoRufeDao.findAllMunicipios().stream()
                .map(this::mapToMunicipioResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CatalogoItemResponse> getAllEventos() {
        return catalogoRufeDao.findAllEventos().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}