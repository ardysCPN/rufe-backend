package co.rufe.rufe.util;

import co.rufe.rufe.model.Organizacion;
import co.rufe.rufe.model.Permiso;
import co.rufe.rufe.model.MenuItem;
import co.rufe.rufe.model.MenuItemPermiso;
import co.rufe.rufe.model.Rol;
import co.rufe.rufe.model.RolPermiso;
import co.rufe.rufe.model.Usuario;
import co.rufe.rufe.model.UsuarioWithDetails;
import co.rufe.rufe.model.catalogo.EstadoBien;
import co.rufe.rufe.model.catalogo.FormaTenenciaBien;
import co.rufe.rufe.model.catalogo.Genero;
import co.rufe.rufe.model.catalogo.Parentesco;
import co.rufe.rufe.model.catalogo.PertenenciaEtnica;
import co.rufe.rufe.model.catalogo.TipoAlojamientoActual;
import co.rufe.rufe.model.catalogo.TipoBien;
import co.rufe.rufe.model.catalogo.TipoDocumento;
import co.rufe.rufe.model.catalogo.TipoUbicacionBien;
import co.rufe.rufe.model.catalogo.Departamento;
import co.rufe.rufe.model.catalogo.Municipio;
import co.rufe.rufe.model.catalogo.Evento;
import co.rufe.rufe.model.AuditLog;

import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;
import java.sql.Timestamp; // Importa Timestamp

public class CustomRowMappers {

        // Mapper para la entidad Organizacion
        public static final RowMapper<Organizacion> ORGANIZACION_ROW_MAPPER = (rs, rowNum) -> {
                Organizacion org = new Organizacion();
                org.setId(rs.getLong("id"));
                org.setNombreOrganizacion(rs.getString("nombre_organizacion"));
                org.setNit(rs.getString("nit"));
                org.setDireccion(rs.getString("direccion"));
                org.setTelefono(rs.getString("telefono"));
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
                item.setId(rs.getInt("id"));
                Integer parentId = rs.getInt("parent_id");
                if (!rs.wasNull()) { // Check if parent_id was actually NULL in DB
                        item.setParentId(parentId);
                } else {
                        item.setParentId(null);
                }
                item.setNombreItem(rs.getString("nombre_item"));
                item.setRuta(rs.getString("ruta"));
                item.setIcono(rs.getString("icono"));
                item.setOrden(rs.getInt("orden"));
                return item;
        };

        // Mapper para la entidad UsuarioWithDetails (usado por funciones de BD para
        // login)
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

        // public static final RowMapper<RolPermiso> ROL_PERMISO_ROW_MAPPER = (rs,
        // rowNum) -> {
        // RolPermiso rolPermiso = new RolPermiso();
        // rolPermiso.setRolId(rs.getLong("rol_id"));
        // rolPermiso.setMenuItemId(rs.getLong("menu_item_id"));
        // return rolPermiso;
        // };

        // Helper para convertir Timestamp a LocalDateTime de forma segura
        private static LocalDateTime toLocalDateTime(Timestamp timestamp) {
                return (timestamp != null) ? timestamp.toLocalDateTime() : null;
        }

        public static final RowMapper<Permiso> PERMISO_ROW_MAPPER = (rs, rowNum) -> Permiso.builder()
                        .id(rs.getInt("id"))
                        .nombrePermiso(rs.getString("nombre_permiso"))
                        .build();

        public static final RowMapper<RolPermiso> ROL_PERMISO_ROW_MAPPER = (rs, rowNum) -> RolPermiso.builder()
                        .rolId(rs.getLong("rol_id"))
                        .permisoId(rs.getInt("permiso_id"))
                        .build();

        // RowMapper para MenuItemPermiso
        public static final RowMapper<MenuItemPermiso> MENU_ITEM_PERMISO_ROW_MAPPER = (rs, rowNum) -> MenuItemPermiso
                        .builder()
                        .menuItemId(rs.getInt("menu_item_id"))
                        .permisoId(rs.getInt("permiso_id"))
                        .build();

        public static final RowMapper<TipoUbicacionBien> TIPO_UBICACION_BIEN_ROW_MAPPER = (rs,
                        rowNum) -> TipoUbicacionBien.builder()
                                        .id(rs.getInt("id"))
                                        .nombre(rs.getString("nombre"))
                                        .build();

        public static final RowMapper<TipoAlojamientoActual> TIPO_ALOJAMIENTO_ACTUAL_ROW_MAPPER = (rs,
                        rowNum) -> TipoAlojamientoActual.builder()
                                        .id(rs.getInt("id"))
                                        .nombre(rs.getString("nombre"))
                                        .build();

        public static final RowMapper<FormaTenenciaBien> FORMA_TENENCIA_BIEN_ROW_MAPPER = (rs,
                        rowNum) -> FormaTenenciaBien.builder()
                                        .id(rs.getInt("id"))
                                        .nombre(rs.getString("nombre"))
                                        .build();

        public static final RowMapper<EstadoBien> ESTADO_BIEN_ROW_MAPPER = (rs, rowNum) -> EstadoBien.builder()
                        .id(rs.getInt("id"))
                        .nombre(rs.getString("nombre"))
                        .build();

        public static final RowMapper<TipoBien> TIPO_BIEN_ROW_MAPPER = (rs, rowNum) -> TipoBien.builder()
                        .id(rs.getInt("id"))
                        .nombre(rs.getString("nombre"))
                        .build();

        public static final RowMapper<TipoDocumento> TIPO_DOCUMENTO_ROW_MAPPER = (rs, rowNum) -> TipoDocumento.builder()
                        .id(rs.getInt("id"))
                        .nombre(rs.getString("nombre"))
                        .build();

        public static final RowMapper<Parentesco> PARENTESCO_ROW_MAPPER = (rs, rowNum) -> Parentesco.builder()
                        .id(rs.getInt("id"))
                        .nombre(rs.getString("nombre"))
                        .build();

        public static final RowMapper<Genero> GENERO_ROW_MAPPER = (rs, rowNum) -> Genero.builder()
                        .id(rs.getInt("id"))
                        .nombre(rs.getString("nombre"))
                        .build();

        public static final RowMapper<PertenenciaEtnica> PERTENENCIA_ETNICA_ROW_MAPPER = (rs,
                        rowNum) -> PertenenciaEtnica.builder()
                                        .id(rs.getInt("id"))
                                        .nombre(rs.getString("nombre"))
                                        .build();

        public static final RowMapper<Departamento> DEPARTAMENTO_ROW_MAPPER = (rs, rowNum) -> Departamento.builder()
                        .id(rs.getInt("id"))
                        .nombre(rs.getString("nombre"))
                        .build();

        public static final RowMapper<Municipio> MUNICIPIO_ROW_MAPPER = (rs, rowNum) -> Municipio.builder()
                        .id(rs.getInt("id"))
                        .nombre(rs.getString("nombre"))
                        .departamentoId(rs.getInt("departamento_id"))
                        .build();

        public static final RowMapper<Evento> EVENTO_ROW_MAPPER = (rs, rowNum) -> Evento.builder()
                        .id(rs.getInt("id"))
                        .nombre(rs.getString("nombre"))
                        .build();

        public static final RowMapper<AuditLog> AUDIT_LOG_ROW_MAPPER = (rs, rowNum) -> AuditLog.builder()
                        .id(rs.getLong("id"))
                        .organizacionId(rs.getLong("organizacion_id"))
                        .usuarioId(rs.getLong("usuario_id"))
                        .accion(rs.getString("accion"))
                        .recurso(rs.getString("recurso"))
                        .detalle(rs.getString("detalle"))
                        .ipAddress(rs.getString("ip_address"))
                        .fechaCreacion(toLocalDateTime(rs.getTimestamp("fecha_creacion")))
                        .build();
}
