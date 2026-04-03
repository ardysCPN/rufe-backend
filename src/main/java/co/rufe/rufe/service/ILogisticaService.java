package co.rufe.rufe.service;

import co.rufe.rufe.model.AyudaCatalogo;
import co.rufe.rufe.model.AyudasEntregadas;
import co.rufe.rufe.model.BodegaInventario;

import java.math.BigDecimal;
import java.util.List;

public interface ILogisticaService {

    // Catalogo
    List<AyudaCatalogo> getCatalogoAyudas();
    AyudaCatalogo addCatalogoAyuda(AyudaCatalogo item);

    // Bodega
    BodegaInventario addStockBodega(Long organizacionId, Integer ayudaCatalogoId, BigDecimal cantidad);
    List<BodegaInventario> getInventarioTotal(Long organizacionId);

    // Entregas de ayudas a censados
    AyudasEntregadas despacharAyuda(Long organizacionId, Long registroRufeId, Integer ayudaCatalogoId, BigDecimal cantidad, String firmaDigital, String fotoUrl);
    List<AyudasEntregadas> getAyudasEntregadasPorOrganizacion(Long organizacionId);
    List<AyudasEntregadas> getAyudasEntregadasPorCenso(Long registroRufeId);
}
