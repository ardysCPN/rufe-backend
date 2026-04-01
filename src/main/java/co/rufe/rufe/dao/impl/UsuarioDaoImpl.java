package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IUsuarioDao;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.model.Usuario;
import co.rufe.rufe.model.UsuarioWithDetails; // Importa la nueva clase
import co.rufe.rufe.util.CustomRowMappers;
import co.rufe.rufe.util.TenantContext; // Para acceder al ID de la organización
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Repository
public class UsuarioDaoImpl implements IUsuarioDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public UsuarioDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO usuarios (organizacion_id, rol_id, nombre_completo, email, password_hash, activo, fecha_creacion, fecha_actualizacion) "
                +
                "VALUES (:organizacionId, :rolId, :nombreCompleto, :email, :passwordHash, :activo, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", usuario.getOrganizacionId()); // Viene del TenantContext o del servicio
        params.addValue("rolId", usuario.getRolId());
        params.addValue("nombreCompleto", usuario.getNombreCompleto());
        params.addValue("email", usuario.getEmail());
        params.addValue("passwordHash", usuario.getPasswordHash());
        params.addValue("activo", usuario.getActivo() != null ? usuario.getActivo() : true); // Default si es null

        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });

        long newId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        usuario.setId(newId);
        return usuario;
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT id, organizacion_id, rol_id, nombre_completo, email, password_hash, activo, fecha_creacion, fecha_actualizacion "
                +
                "FROM usuarios WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(
                    namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.USUARIO_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Usuario> findByOrganizacionIdAndEmail(Long organizacionId, String email) {
        String sql = "SELECT id, organizacion_id, rol_id, nombre_completo, email, password_hash, activo, fecha_creacion, fecha_actualizacion "
                +
                "FROM usuarios WHERE organizacion_id = :organizacionId AND email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", organizacionId);
        params.addValue("email", email);
        try {
            return Optional.ofNullable(
                    namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.USUARIO_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        // Este método NO usa TenantContext porque está diseñado para el login inicial
        // donde el TenantContext aún no está establecido.
        String sql = "SELECT id, organizacion_id, rol_id, nombre_completo, email, password_hash, activo, fecha_creacion, fecha_actualizacion "
                +
                "FROM usuarios WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);
        try {
            return Optional.ofNullable(
                    namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.USUARIO_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Usuario> findByOrganizacionId(Long organizacionId) {
        String sql = "SELECT id, organizacion_id, rol_id, nombre_completo, email, password_hash, activo, fecha_creacion, fecha_actualizacion "
                +
                "FROM usuarios WHERE organizacion_id = :organizacionId ORDER BY nombre_completo";
        MapSqlParameterSource params = new MapSqlParameterSource("organizacionId", organizacionId);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.USUARIO_ROW_MAPPER);
    }

    @Override
    public List<Usuario> findAll() {
        String sql = "SELECT id, organizacion_id, rol_id, nombre_completo, email, password_hash, activo, fecha_creacion, fecha_actualizacion "
                +
                "FROM usuarios ORDER BY nombre_completo";
        return namedParameterJdbcTemplate.query(sql, CustomRowMappers.USUARIO_ROW_MAPPER);
    }

    @Override
    public List<UsuarioWithDetails> findByOrganizacionIdWithDetails(Long organizacionId) {
        String sql = "SELECT u.id, u.organizacion_id, o.nombre_organizacion, o.activa as organizacion_activa, " +
                "u.rol_id, r.nombre_rol, u.nombre_completo, u.email, u.password_hash, u.activo, " +
                "u.fecha_creacion, u.fecha_actualizacion " +
                "FROM usuarios u " +
                "JOIN roles r ON u.rol_id = r.id " +
                "JOIN organizaciones o ON u.organizacion_id = o.id " +
                "WHERE u.organizacion_id = :organizacionId ORDER BY u.nombre_completo";
        MapSqlParameterSource params = new MapSqlParameterSource("organizacionId", organizacionId);
        return namedParameterJdbcTemplate.query(sql, params, CustomRowMappers.USUARIO_WITH_DETAILS_ROW_MAPPER);
    }

    @Override
    public List<UsuarioWithDetails> findAllWithDetails() {
        String sql = "SELECT u.id, u.organizacion_id, o.nombre_organizacion, o.activa as organizacion_activa, " +
                "u.rol_id, r.nombre_rol, u.nombre_completo, u.email, u.password_hash, u.activo, " +
                "u.fecha_creacion, u.fecha_actualizacion " +
                "FROM usuarios u " +
                "JOIN roles r ON u.rol_id = r.id " +
                "JOIN organizaciones o ON u.organizacion_id = o.id " +
                "ORDER BY u.nombre_completo";
        return namedParameterJdbcTemplate.query(sql, CustomRowMappers.USUARIO_WITH_DETAILS_ROW_MAPPER);
    }

    @Override
    public Usuario update(Usuario usuario) {
        String sql = "UPDATE usuarios SET rol_id = :rolId, nombre_completo = :nombreCompleto, " +
                "email = :email, password_hash = :passwordHash, activo = :activo, fecha_actualizacion = NOW() " +
                "WHERE id = :id AND organizacion_id = :organizacionId"; // Aseguramos que solo se actualice dentro de la
                                                                        // organización
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rolId", usuario.getRolId());
        params.addValue("nombreCompleto", usuario.getNombreCompleto());
        params.addValue("email", usuario.getEmail());
        params.addValue("passwordHash", usuario.getPasswordHash());
        params.addValue("activo", usuario.getActivo());
        params.addValue("id", usuario.getId());
        params.addValue("organizacionId", usuario.getOrganizacionId()); // El servicio debe setear esto o se toma del
                                                                        // TenantContext

        int rowsAffected = namedParameterJdbcTemplate.update(sql, params);
        if (rowsAffected == 0) {
            throw new ResourceNotFoundException(
                    "Usuario no encontrado con ID: " + usuario.getId() + " o no pertenece a la organización actual.");
        }
        return usuario;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = :id AND organizacion_id = :organizacionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("organizacionId", TenantContext.getCurrentOrganizationId());
        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE id = :id AND organizacion_id = :organizacionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("organizacionId", TenantContext.getCurrentOrganizationId());
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByOrganizacionIdAndEmail(Long organizacionId, String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE organizacion_id = :organizacionId AND email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", organizacionId);
        params.addValue("email", email);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public Set<Long> findMenuItemIdsByUserId(Long userId) {
        // Esta consulta obtiene los IDs de los items de menú a los que tiene acceso un
        // usuario
        // a través de su rol, asegurando que estén dentro de la misma organización.
        String sql = "SELECT rpm.menu_item_id FROM usuarios u " +
                "JOIN roles r ON u.rol_id = r.id AND u.organizacion_id = r.organizacion_id " +
                "JOIN rol_permisos rpm ON r.id = rpm.rol_id " +
                "WHERE u.id = :userId AND u.organizacion_id = :organizacionId"; // Asegura multi-tenancy
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("organizacionId", TenantContext.getCurrentOrganizationId());

        List<Long> itemIds = namedParameterJdbcTemplate.queryForList(sql, params, Long.class);
        return new HashSet<>(itemIds);
    }

    @Override
    public Optional<UsuarioWithDetails> findUserWithDetailsByEmailAndOrganizationName(String email,
            String organizacionNombre) {
        String sql = "SELECT * FROM get_user_details_for_login(:email, :organizacionNombre)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        params.addValue("organizacionNombre", organizacionNombre);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params,
                    CustomRowMappers.USUARIO_WITH_DETAILS_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UsuarioWithDetails> findUserWithDetailsByEmail(String email) {
        // Consulta SQL estándar con JOINs para obtener todos los detalles necesarios
        // para el token
        // asumiendo que el email es único o devolviendo el primero encontrado.
        String sql = "SELECT u.id, u.organizacion_id, o.nombre_organizacion, o.activa as organizacion_activa, " +
                "u.rol_id, r.nombre_rol, u.nombre_completo, u.email, u.password_hash, u.activo, " +
                "u.fecha_creacion, u.fecha_actualizacion " +
                "FROM usuarios u " +
                "JOIN roles r ON u.rol_id = r.id " +
                "JOIN organizaciones o ON u.organizacion_id = o.id " +
                "WHERE u.email = :email";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);

        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params,
                    CustomRowMappers.USUARIO_WITH_DETAILS_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Usuario> findByEmailAndOrganizacionId(String email, Long organizacionId) {
        String sql = "SELECT id, organizacion_id, rol_id, nombre_completo, email, password_hash, activo, fecha_creacion, fecha_actualizacion "
                +
                "FROM usuarios WHERE email = :email AND organizacion_id = :organizacionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        params.addValue("organizacionId", organizacionId);
        try {
            return Optional.ofNullable(
                    namedParameterJdbcTemplate.queryForObject(sql, params, CustomRowMappers.USUARIO_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
