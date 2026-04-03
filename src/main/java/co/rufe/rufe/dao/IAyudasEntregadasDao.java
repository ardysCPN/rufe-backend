package co.rufe.rufe.dao;

import co.rufe.rufe.model.AyudasEntregadas;
import java.util.List;

public interface IAyudasEntregadasDao {
    AyudasEntregadas save(AyudasEntregadas ayuda);
    List<AyudasEntregadas> findByOrganizacionId(Long organizacionId);
    List<AyudasEntregadas> findByRegistroRufeId(Long registroRufeId);
}
