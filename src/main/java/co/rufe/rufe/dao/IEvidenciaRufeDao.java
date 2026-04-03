package co.rufe.rufe.dao;

import co.rufe.rufe.model.EvidenciaRufe;
import java.util.List;

public interface IEvidenciaRufeDao {
    EvidenciaRufe save(EvidenciaRufe evidencia);
    List<EvidenciaRufe> findByRegistroRufeId(Long registroRufeId);
    void deleteById(Long id);
}
