package co.rufe.rufe.dao;

import co.rufe.rufe.model.BodegaInventario;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IBodegaInventarioDao {
    BodegaInventario save(BodegaInventario bodega);
    Optional<BodegaInventario> findById(Long id);
    Optional<BodegaInventario> findByOrganizacionAndAyuda(Long organizacionId, Integer ayudaCatalogoId);
    List<BodegaInventario> findAllByOrganizacion(Long organizacionId);
    void updateStock(Long organizacionId, Integer ayudaCatalogoId, BigDecimal newStock);
}
