package co.rufe.rufe.dao;

import co.rufe.rufe.model.ActivoAgropecuario;
import co.rufe.rufe.model.BienAfectado;
import co.rufe.rufe.model.IntegranteHogar;
import co.rufe.rufe.model.RegistroRufe;
import java.util.List;
import java.util.Optional;

public interface IRegistroRufeDao {

    // Guardado transaccional de entidades
    RegistroRufe save(RegistroRufe registro);

    void saveIntegrante(IntegranteHogar integrante);

    void saveBienAfectado(BienAfectado bien);

    void saveActivoAgropecuario(ActivoAgropecuario activo);

    // Validaciones Antifraude y Offline
    boolean existsByClienteIdAndOrganizacionId(String clienteId, Long organizacionId);

    boolean existsByNumeroDocumentoAndEventoId(String numeroDocumento, Long eventoId);

    // Opcional: Buscar por ID para respuestas futuras
    Optional<RegistroRufe> findById(Long id);

    List<RegistroRufe> findAll();

    List<RegistroRufe> findAllByOrganizacionId(Long organizacionId);

    // Consulta desacoplada para reportes
    List<java.util.Map<String, Object>> obtenerDatosReporteExcel(Long organizacionId, boolean isAdmin);

    void update(RegistroRufe registro);

    void deleteById(Long id);

    int countIntegrantesByRegistroId(Long registroId);
}
