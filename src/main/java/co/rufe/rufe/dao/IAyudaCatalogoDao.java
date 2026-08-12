package co.rufe.rufe.dao;

import co.rufe.rufe.model.AyudaCatalogo;
import java.util.List;

public interface IAyudaCatalogoDao {
    List<AyudaCatalogo> findAllByOrganizacionId(Long organizacionId);
    AyudaCatalogo findById(Integer id);
    AyudaCatalogo save(AyudaCatalogo item);
}
