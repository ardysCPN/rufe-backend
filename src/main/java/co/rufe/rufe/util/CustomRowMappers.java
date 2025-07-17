package co.rufe.rufe.util;

import co.rufe.rufe.model.Organizacion;
import co.rufe.rufe.model.Permiso;
import co.rufe.rufe.model.MenuItem;
import co.rufe.rufe.model.MenuItemPermiso;
import co.rufe.rufe.model.Rol;
import co.rufe.rufe.model.RolPermiso;
import co.rufe.rufe.model.Usuario;
import co.rufe.rufe.model.UsuarioWithDetails;

import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;
import java.sql.Timestamp; // Importa Timestamp

public class CustomRowMappers {

    // Mapper para la entidad Organizacion
    public static final RowMapper<Organizacion> ORGANIZACION_ROW_MAPPER = (rs, rowNum) -> {
        Organizacion org = new Organizacion();
        org.setId(rs.getLong("id"));
        org.setNombreOrganizacion(rs.getString("nombre_organizacion"));
        org.setActiva(rs.getBoolean("activa"));
        org.setFechaCreacion(toLocalDateTime(rs.getTimestamp("fecha_creacion")));
        org.setFechaActualizacion(toLocalDateTime(rs.getTimestamp("fecha_actualizacion")));
        return org;
    };

    // Mapper para la entidad Rol
    public static final RowMapper<Rol> ROL_ROW_MAPPER = (rs, rowNum) -> {
        Rol rol = new Rol();
        rol.setId(rs.getLong("id"));
        rol.setOrganizacionId(rs.getLong("organizacion_id"));
        rol.setNombreRol(rs.getString("nombre_rol"));
        rol.setDescripcion(rs.getString("descripcion"));
        rol.setFechaCreacion(toLocalDateTime(rs.getTimestamp("fecha_creacion")));
        return rol;
    };

    // Mapper para la entidad Usuario
    public static final RowMapper<Usuario> USUARIO_ROW_MAPPER = (rs, rowNum) -> {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setOrganizacionId(rs.getLong("organizacion_id"));
        usuario.setRolId(rs.getLong("rol_id"));
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaCreacion(toLocalDateTime(rs.getTimestamp("fecha_creacion")));
        usuario.setFechaActualizacion(toLocalDateTime(rs.getTimestamp("fecha_actualizacion")));
        return usuario;
    };

    // Mapper para la entidad MenuItem
    public static final RowMapper<MenuItem> MENU_ITEM_ROW_MAPPER = (rs, rowNum) -> {
        MenuItem item = new MenuItem();
        item.setId(rs.getLong("id"));
        long parentId = rs.getLong("parent_id");
        if (!rs.wasNull()) { // Check if parent_id was actually NULL in DB
            item.setParentId(parentId);
        }else {
            item.setParentId(null);
        }
        item.setNombreItem(rs.getString("nombre_item"));
        item.setRuta(rs.getString("ruta"));
        item.setIcono(rs.getString("icono"));
        item.setOrden(rs.getInt("orden"));
        return item;
    };

    // Mapper para la entidad UsuarioWithDetails (usado por funciones de BD para login)
    public static final RowMapper<UsuarioWithDetails> USUARIO_WITH_DETAILS_ROW_MAPPER = (rs, rowNum) -> {
        UsuarioWithDetails usuario = new UsuarioWithDetails();
        usuario.setId(rs.getLong("id"));
        usuario.setOrganizacionId(rs.getLong("organizacion_id"));
        usuario.setOrganizacionNombre(rs.getString("nombre_organizacion"));
        usuario.setOrganizacionActiva(rs.getBoolean("organizacion_activa"));
        usuario.setRolId(rs.getLong("rol_id"));
        usuario.setRolNombre(rs.getString("nombre_rol"));
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaCreacion(toLocalDateTime(rs.getTimestamp("fecha_creacion")));
        usuario.setFechaActualizacion(toLocalDateTime(rs.getTimestamp("fecha_actualizacion")));
        return usuario;
    };

    // public static final RowMapper<RolPermiso> ROL_PERMISO_ROW_MAPPER = (rs, rowNum) -> {
    //     RolPermiso rolPermiso = new RolPermiso();
    //     rolPermiso.setRolId(rs.getLong("rol_id"));
    //     rolPermiso.setMenuItemId(rs.getLong("menu_item_id"));
    //     return rolPermiso;
    // };

    // Helper para convertir Timestamp a LocalDateTime de forma segura
    private static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return (timestamp != null) ? timestamp.toLocalDateTime() : null;
    }

    public static final RowMapper<Permiso> PERMISO_ROW_MAPPER = (rs, rowNum) -> Permiso.builder()
            .id(rs.getLong("id"))
            .nombrePermiso(rs.getString("nombre_permiso"))
            .build();

    public static final RowMapper<RolPermiso> ROL_PERMISO_ROW_MAPPER = (rs, rowNum) -> RolPermiso.builder()
            .rolId(rs.getLong("rol_id"))
            .permisoId(rs.getInt("permiso_id")) 
            .build();

    // RowMapper para MenuItemPermiso
    public static final RowMapper<MenuItemPermiso> MENU_ITEM_PERMISO_ROW_MAPPER = (rs, rowNum) -> MenuItemPermiso.builder()
            .menuItemId(rs.getInt("menu_item_id"))
            .permisoId(rs.getInt("permiso_id"))
            .build();
}
