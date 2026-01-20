package co.rufe.rufe.dao;

import co.rufe.rufe.model.Organizacion;
import co.rufe.rufe.model.Usuario;
import co.rufe.rufe.model.UsuarioWithDetails;

import java.util.List;
import java.util.Optional;
import java.util.Set; // Para los IDs de los items de menú

public interface IUsuarioDao {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByOrganizacionIdAndEmail(Long organizacionId, String email);
    Optional<Usuario> findByEmail(String email); // Para el login, sin el tenantId inicial
    List<Usuario> findByOrganizacionId(Long organizacionId);
    Usuario update(Usuario usuario);
    boolean deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByOrganizacionIdAndEmail(Long organizacionId, String email);

    // Métodos para obtener permisos (menu_item_ids) asociados a un usuario a través de su rol
    Set<Long> findMenuItemIdsByUserId(Long userId);

    // Función de BD para login:
    // Retorna Usuario con detalles de Rol y Organizacion para facilitar la construcción del JWT.
    // Esto es un ejemplo de cómo podemos llamar a una función de la BD.
    Optional<UsuarioWithDetails> findUserWithDetailsByEmailAndOrganizationName(String email, String organizacionNombre);
    Optional<Usuario> findByEmailAndOrganizacionId(String email, Long organizacionId);
}
