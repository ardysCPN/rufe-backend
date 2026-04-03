package co.rufe.rufe.dao;

import co.rufe.rufe.model.AyudaCatalogo;
import java.util.List;

public interface IAyudaCatalogoDao {
    List<AyudaCatalogo> findAll();
    AyudaCatalogo findById(Integer id);
}
