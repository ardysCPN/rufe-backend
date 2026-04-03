package co.rufe.rufe.service.impl;

import co.rufe.rufe.dao.IAyudaCatalogoDao;
import co.rufe.rufe.dao.IAyudasEntregadasDao;
import co.rufe.rufe.dao.IBodegaInventarioDao;
import co.rufe.rufe.model.AyudaCatalogo;
import co.rufe.rufe.model.AyudasEntregadas;
import co.rufe.rufe.model.BodegaInventario;
import co.rufe.rufe.service.ILogisticaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticaServiceImpl implements ILogisticaService {

    private final IAyudaCatalogoDao catalogoDao;
    private final IBodegaInventarioDao bodegaDao;
    private final IAyudasEntregadasDao entregasDao;

    @Override
    public List<AyudaCatalogo> getCatalogoAyudas() {
        return catalogoDao.findAll();
    }

    @Override
    @Transactional
    public BodegaInventario addStockBodega(Long organizacionId, Integer ayudaCatalogoId, BigDecimal cantidad) {
        Optional<BodegaInventario> actual = bodegaDao.findByOrganizacionAndAyuda(organizacionId, ayudaCatalogoId);
        
        if (actual.isPresent()) {
            BodegaInventario bodega = actual.get();
            BigDecimal nuevoStock = bodega.getCantidad().add(cantidad);
            if(nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("El inventario resultante no puede ser negativo.");
            }
            bodega.setCantidad(nuevoStock);
            return bodegaDao.save(bodega);
        } else {
            if(cantidad.compareTo(BigDecimal.ZERO) < 0) throw new IllegalStateException("No existe la bodega y se intentó entregar cantidad negativa.");
            
            BodegaInventario nuevo = BodegaInventario.builder()
                    .organizacionId(organizacionId)
                    .ayudaCatalogoId(ayudaCatalogoId)
                    .cantidad(cantidad)
                    .build();
            return bodegaDao.save(nuevo);
        }
    }

    @Override
    public List<BodegaInventario> getInventarioTotal(Long organizacionId) {
        return bodegaDao.findAllByOrganizacion(organizacionId);
    }

    @Override
    @Transactional
    public AyudasEntregadas despacharAyuda(Long organizacionId, Long registroRufeId, Integer ayudaCatalogoId, BigDecimal cantidad, String firmaDigital, String fotoUrl) {
        // Regla de Negocio: Restar de Bodega
        Optional<BodegaInventario> actual = bodegaDao.findByOrganizacionAndAyuda(organizacionId, ayudaCatalogoId);
        if (actual.isEmpty() || actual.get().getCantidad().compareTo(cantidad) < 0) {
            throw new IllegalStateException("Stock insuficiente en la bodega para despachar.");
        }
        
        bodegaDao.updateStock(organizacionId, ayudaCatalogoId, actual.get().getCantidad().subtract(cantidad));
        
        AyudasEntregadas entrega = AyudasEntregadas.builder()
                .organizacionId(organizacionId)
                .registroRufeId(registroRufeId)
                .ayudaCatalogoId(ayudaCatalogoId)
                .cantidad(cantidad)
                .firmaDigital(firmaDigital)
                .evidenciaFotoUrl(fotoUrl)
                .fechaEntrega(LocalDateTime.now())
                .build();
                
        return entregasDao.save(entrega);
    }

    @Override
    public List<AyudasEntregadas> getAyudasEntregadasPorOrganizacion(Long organizacionId) {
        return entregasDao.findByOrganizacionId(organizacionId);
    }

    @Override
    public List<AyudasEntregadas> getAyudasEntregadasPorCenso(Long registroRufeId) {
        return entregasDao.findByRegistroRufeId(registroRufeId);
    }
}
