package co.rufe.rufe.dao;

import co.rufe.rufe.model.Menu;
import java.util.List;

public interface IMenuDao {
    List<Menu> findByRolId(Long rolId);

    List<Menu> findAll();
}
