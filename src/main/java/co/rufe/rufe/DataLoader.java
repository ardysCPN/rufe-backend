package co.rufe.rufe;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import co.rufe.rufe.dao.IMenuItemDao;
import co.rufe.rufe.dao.IMenuItemPermisoDao;
import co.rufe.rufe.dao.IOrganizacionDao;
import co.rufe.rufe.dao.IPermisoDao;
import co.rufe.rufe.dao.IRolDao;
import co.rufe.rufe.dao.IRolPermisoDao;
import co.rufe.rufe.dao.IUsuarioDao;
import co.rufe.rufe.model.MenuItem;
import co.rufe.rufe.model.Organizacion;
import co.rufe.rufe.model.Permiso;
import co.rufe.rufe.model.Rol;
import co.rufe.rufe.model.Usuario;
import co.rufe.rufe.util.TenantContext;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DataLoader {

    private final PasswordEncoder passwordEncoder;
    private final IOrganizacionDao organizacionDao;
    private final IRolDao rolDao;
    private final IUsuarioDao usuarioDao;
    private final IPermisoDao permisoDao;
    private final IRolPermisoDao rolPermisoDao;
    private final IMenuItemDao menuItemDao;
    private final IMenuItemPermisoDao menuItemPermisoDao;

    @Value("${app.dataloader.enabled:true}") // Valor por defecto: true (se ejecuta)
    private boolean dataLoaderEnabled;

    public DataLoader(PasswordEncoder passwordEncoder,
                      IOrganizacionDao organizacionDao,
                      IRolDao rolDao,
                      IUsuarioDao usuarioDao,
                      IPermisoDao permisoDao,
                      IRolPermisoDao rolPermisoDao,
                      IMenuItemDao menuItemDao,
                      IMenuItemPermisoDao menuItemPermisoDao) {
        this.passwordEncoder = passwordEncoder;
        this.organizacionDao = organizacionDao;
        this.rolDao = rolDao;
        this.usuarioDao = usuarioDao;
        this.permisoDao = permisoDao;
        this.rolPermisoDao = rolPermisoDao;
        this.menuItemDao = menuItemDao;
        this.menuItemPermisoDao = menuItemPermisoDao;
    }

    @Bean
    @Transactional
    public CommandLineRunner initDatabase() {
        return args -> {
            if (!dataLoaderEnabled) { // Controla la ejecución aquí
                log.info("DataLoader está deshabilitado por configuración. No se cargarán datos iniciales.");
                return; // Sale del método si está deshabilitado
            }
            
            log.info("Iniciando verificación y carga de datos iniciales...");

            // ==================== 1. Crear Organización Master (GlobalCorp) ====================
            // AHORA: Busca por nombre, NO por ID si el ID es SERIAL/auto-incrementado
            // Si el ID de Organizacion es BIGSERIAL/auto-incrementado, no puedes forzar el ID=1L
            // Si quieres que GlobalCorp SIEMPRE sea 1, entonces su 'id' en la DB no debe ser SERIAL,
            // o debes asegurarte de que sea la primera y única inserción de este tipo.
            // Para evitar errores como el de MenuItem, es mejor buscar por nombre.
            Organizacion masterOrg = organizacionDao.findByNombreOrganizacion("GlobalCorp")
                .orElseGet(() -> {
                    log.info("Creando Organización Master 'GlobalCorp'...");
                    Organizacion newOrg = Organizacion.builder()
                            // .id(masterOrgId) // <<--- ELIMINAR ESTA LÍNEA SI EL ID ES BIGSERIAL
                            .nombreOrganizacion("GlobalCorp")
                            .activa(true)
                            .fechaCreacion(LocalDateTime.now())
                            .build();
                    return organizacionDao.save(newOrg);
                });
            // Una vez que masterOrg ha sido obtenida o creada, podemos usar su ID real
            Long masterOrgId = masterOrg.getId();
            TenantContext.setCurrentOrganizationId(masterOrgId); // Establece el contexto para guardar
            log.info("Organización Master existente o creada: {} con ID: {}", masterOrg.getNombreOrganizacion(), masterOrg.getId());
            TenantContext.clear(); // Limpia el contexto después de la operación master

            // Resto del código... (igual que tu última versión, pero aplicando la misma lógica para otros `createOrGet` si sus IDs son auto-incrementados)
            // Asegúrate de que en createOrGetRol y createOrGetUsuario NO se fuerce el ID si son SERIAL/BIGSERIAL

            // ==================== 2. Crear Roles (en contexto de Organizacion Master) ====================
            TenantContext.setCurrentOrganizationId(masterOrgId);
            log.info("Creando roles en la Organización Master...");
            Rol rolAdminGlobal = createOrGetRol("ADMIN_GLOBAL", "Administrador global del sistema con acceso total a todas las organizaciones.", masterOrgId);
            Rol rolAdminOrganizacion = createOrGetRol("ADMIN_ORGANIZACION", "Administrador de una organización específica.", masterOrgId);
            Rol rolEncuestador = createOrGetRol("ENCUESTADOR_ORGANIZACION_UPDATED", "Encuestador actualizado de la organización.", masterOrgId);
            TenantContext.clear();
            log.info("Roles creados o existentes.");

            // ==================== 3. Crear Permisos (definiciones globales) ====================
            log.info("Creando permisos granulares...");
            Permiso orgCrear = createOrGetPermiso("organizaciones:crear", "Permite crear nuevas organizaciones", "Organizaciones");
            Permiso orgLeer = createOrGetPermiso("organizaciones:leer", "Permite ver la lista de organizaciones y sus detalles", "Organizaciones");
            Permiso orgActualizar = createOrGetPermiso("organizaciones:actualizar", "Permite modificar organizaciones existentes", "Organizaciones");
            Permiso orgEliminar = createOrGetPermiso("organizaciones:eliminar", "Permite eliminar organizaciones", "Organizaciones");

            Permiso rolCrear = createOrGetPermiso("roles:crear", "Permite crear nuevos roles dentro de una organización", "Roles");
            Permiso rolLeer = createOrGetPermiso("roles:leer", "Permite ver la lista de roles y sus detalles", "Roles");
            Permiso rolActualizar = createOrGetPermiso("roles:actualizar", "Permite modificar roles existentes", "Roles");
            Permiso rolEliminar = createOrGetPermiso("roles:eliminar", "Permite eliminar roles", "Roles");
            Permiso rolAsignarPermisos = createOrGetPermiso("roles:asignar_permisos", "Permite asignar y revocar permisos a un rol", "Roles");

            Permiso userCrear = createOrGetPermiso("usuarios:crear", "Permite crear nuevos usuarios en la organización", "Usuarios");
            Permiso userLeer = createOrGetPermiso("usuarios:leer", "Permite ver la lista de usuarios y sus detalles", "Usuarios");
            Permiso userActualizar = createOrGetPermiso("usuarios:actualizar", "Permite modificar usuarios existentes", "Usuarios");
            Permiso userEliminar = createOrGetPermiso("usuarios:eliminar", "Permite eliminar usuarios", "Usuarios");

            Permiso menuCrear = createOrGetPermiso("menu:crear", "Permite crear nuevos ítems de menú", "Menu");
            Permiso menuLeer = createOrGetPermiso("menu:leer", "Permite leer la estructura del menú", "Menu");
            Permiso menuActualizar = createOrGetPermiso("menu:actualizar", "Permite modificar ítems de menú existentes", "Menu");
            Permiso menuEliminar = createOrGetPermiso("menu:eliminar", "Permite eliminar ítems de menú", "Menu");
            Permiso menuAsignarPermisos = createOrGetPermiso("menu:asignar_permisos", "Permite asignar y revocar permisos a ítems de menú", "Menu");

            log.info("Permisos creados o existentes.");

            // ==================== 4. Asignar Permisos a Roles ====================
            TenantContext.setCurrentOrganizationId(masterOrgId); // Asignación de permisos para roles de la Org Master
            log.info("Asignando permisos a roles...");

            Set<Permiso> allAdminGlobalPerms = new HashSet<>(Arrays.asList(
                orgCrear, orgLeer, orgActualizar, orgEliminar,
                rolCrear, rolLeer, rolActualizar, rolEliminar, rolAsignarPermisos,
                userCrear, userLeer, userActualizar, userEliminar,
                menuCrear, menuLeer, menuActualizar, menuEliminar, menuAsignarPermisos
            ));
            for (Permiso p : allAdminGlobalPerms) {
                assignPermisoToRol(rolAdminGlobal.getId(), p.getId());
            }

            Set<Permiso> allAdminOrgPerms = new HashSet<>(Arrays.asList(
                userCrear, userLeer, userActualizar, userEliminar,
                rolCrear, rolLeer, rolActualizar, rolEliminar, rolAsignarPermisos,
                menuLeer
            ));
            for (Permiso p : allAdminOrgPerms) {
                assignPermisoToRol(rolAdminOrganizacion.getId(), p.getId());
            }

            Set<Permiso> allEncuestadorPerms = new HashSet<>(Arrays.asList(
                userLeer,
                menuLeer
            ));
            for (Permiso p : allEncuestadorPerms) {
                assignPermisoToRol(rolEncuestador.getId(), p.getId());
            }

            log.info("Permisos asignados a roles.");
            TenantContext.clear();

            // ==================== 5. Crear Usuarios de Prueba ====================
            TenantContext.setCurrentOrganizationId(masterOrgId);
            createOrGetUsuario("admin@global.com", "Admin", "Global", passwordEncoder.encode("SuperAdmin2024*"), true, rolAdminGlobal.getId(), masterOrgId);
            createOrGetUsuario("orgadmin@global.com", "Org", "Admin", passwordEncoder.encode("OrgAdmin2024*"), true, rolAdminOrganizacion.getId(), masterOrgId);
            createOrGetUsuario("encuestador@global.com", "Encuestador", "General", passwordEncoder.encode("Encuestador2024*"), true, rolEncuestador.getId(), masterOrgId);
            TenantContext.clear();
            log.info("Usuarios de prueba para Organizacion Master creados o existentes.");

            // ==================== 6. Crear Items de Menú ====================
            log.info("Creando ítems de menú...");
            MenuItem dashboardMenu = createOrGetMenuItem(null, "Dashboard", "/dashboard", "dashboard", 10);
            MenuItem rufeRecordsMenu = createOrGetMenuItem(null, "Registros RUFE", "/rufe/list", "description", 20);
                MenuItem createRufeRecord = createOrGetMenuItem(rufeRecordsMenu.getId(), "Crear Nuevo Registro", "/rufe/new", "add_circle", 21);
            MenuItem adminPanelMenu = createOrGetMenuItem(null, "Administración", "", "admin_panel_settings", 100);
                MenuItem userManagementMenu = createOrGetMenuItem(adminPanelMenu.getId(), "Gestión de Usuarios", "/admin/users", "group", 101);
                MenuItem roleManagementMenu = createOrGetMenuItem(adminPanelMenu.getId(), "Gestión de Roles", "/admin/roles", "manage_accounts", 102);
                MenuItem orgsManagementMenu = createOrGetMenuItem(adminPanelMenu.getId(), "Organizaciones", "/admin/organizations", "corporate_fare", 103);
                MenuItem permissionsManagementMenu = createOrGetMenuItem(adminPanelMenu.getId(), "Gestión de Permisos", "/admin/permissions", "security", 104);
                MenuItem menuItemsManagement = createOrGetMenuItem(adminPanelMenu.getId(), "Gestión de Menú", "/admin/menu-items", "menu_book", 105);

            log.info("Items de menú creados o existentes.");

            // ==================== 7. Asignar Permisos a Items de Menú ====================
            log.info("Asignando permisos a ítems de menú...");

            assignPermisoToMenuItem(dashboardMenu.getId(), userLeer.getId());
            assignPermisoToMenuItem(rufeRecordsMenu.getId(), userLeer.getId());
            assignPermisoToMenuItem(createRufeRecord.getId(), userCrear.getId());

            assignPermisoToMenuItem(adminPanelMenu.getId(), menuLeer.getId());
            assignPermisoToMenuItem(adminPanelMenu.getId(), orgLeer.getId());

            assignPermisoToMenuItem(userManagementMenu.getId(), userLeer.getId());
            assignPermisoToMenuItem(roleManagementMenu.getId(), rolLeer.getId());
            assignPermisoToMenuItem(orgsManagementMenu.getId(), orgLeer.getId());
            assignPermisoToMenuItem(permissionsManagementMenu.getId(), rolAsignarPermisos.getId());
            assignPermisoToMenuItem(menuItemsManagement.getId(), menuAsignarPermisos.getId());

            log.info("Permisos asignados a ítems de menú.");

            log.info("Carga de datos iniciales completada.");
        };
    }

    // ==================== Métodos Auxiliares ====================

    // Helper para crear u obtener Organizaciones
    // Si 'id' en Organizacion es SERIAL/BIGSERIAL, ELIMINA .id(id) del builder.
    // Si necesitas que GlobalCorp sea 1L, entonces 'id' no puede ser SERIAL, o debes asegurarte que la secuencia
    // de la DB se reinicie o al menos no esté en conflicto.
    private Organizacion createOrGetOrganizacion(Long id, String nombre, boolean activo) {
        // Mejor buscar por nombre si el ID es auto-generado y no se fuerza.
        return organizacionDao.findByNombreOrganizacion(nombre)
                .orElseGet(() -> {
                    log.info("Creando organización: {}", nombre);
                    Organizacion org = Organizacion.builder()
                            // ELIMINAR LA SIGUIENTE LÍNEA SI EL ID ES AUTO-INCREMENTADO (SERIAL/BIGSERIAL)
                            // .id(id)
                            .nombreOrganizacion(nombre)
                            .activa(activo)
                            .fechaCreacion(LocalDateTime.now())
                            .build();
                    return organizacionDao.save(org);
                });
    }

    // Helper para crear u obtener Roles
    // Rol ID es BIGSERIAL, por lo que NO se debe forzar el ID
    private Rol createOrGetRol(String nombre, String descripcion, Long organizacionId) {
        return rolDao.findByNombreRolAndOrganizacionId(nombre, organizacionId)
                .orElseGet(() -> {
                    log.info("Creando rol '{}' para org ID: {}", nombre, organizacionId);
                    Rol newRol = Rol.builder()
                            // NO .id() aquí si es auto-generado
                            .nombreRol(nombre)
                            .descripcion(descripcion)
                            .organizacionId(organizacionId)
                            .build();
                    return rolDao.save(newRol);
                });
    }

    // Helper para crear u obtener Usuarios
    // Usuario ID es BIGSERIAL, por lo que NO se debe forzar el ID
    private Usuario createOrGetUsuario(String email, String nombres, String apellidos, String passwordHash, Boolean activo, Long rolId, Long organizacionId) {
        return usuarioDao.findByEmailAndOrganizacionId(email, organizacionId)
                .orElseGet(() -> {
                    log.info("Creando usuario: {}", email);
                    Usuario newUser = Usuario.builder()
                            // NO .id() aquí si es auto-generado
                            .email(email)
                            .nombreCompleto(nombres + " " + apellidos)
                            .passwordHash(passwordHash)
                            .activo(activo)
                            .rolId(rolId)
                            .organizacionId(organizacionId)
                            .fechaCreacion(LocalDateTime.now())
                            .build();
                    return usuarioDao.save(newUser);
                });
    }

    // Helper: Para crear u obtener Permisos
    // Permiso ID es SERIAL, por lo que NO se debe forzar el ID
    private Permiso createOrGetPermiso(String nombrePermiso, String descripcion, String recurso) {
        return permisoDao.findByNombrePermiso(nombrePermiso)
                .orElseGet(() -> {
                    log.info("Creando permiso: {}", nombrePermiso);
                    Permiso newPermiso = Permiso.builder()
                            // NO .id() aquí si es auto-generado
                            .nombrePermiso(nombrePermiso)
                            .descripcion(descripcion)
                            .recurso(recurso)
                            .build();
                    return permisoDao.save(newPermiso);
                });
    }

    // Helper: Para asignar Permiso a Rol
    private void assignPermisoToRol(Long rolId, Integer permisoId) {
        if (!rolPermisoDao.existsPermission(rolId, permisoId)) {
            log.debug("Asignando permiso ID {} a rol ID {}", permisoId, rolId);
            rolPermisoDao.assignPermission(rolId, permisoId);
        } else {
            log.debug("Permiso ID {} ya asignado a rol ID {}", permisoId, rolId);
        }
    }

    // Helper: Para crear u obtener MenuItem
    // MenuItem ID es SERIAL, por lo que NO se debe forzar el ID
    private MenuItem createOrGetMenuItem(Integer parentId, String nombreItem, String ruta, String icono, Integer orden) {
        return menuItemDao.findByNombreItemAndParentId(nombreItem, parentId)
                .orElseGet(() -> {
                    log.info("Creando MenuItem: {}", nombreItem);
                    MenuItem newMenuItem = MenuItem.builder()
                            // NO .id() aquí si es auto-generado
                            .parentId(parentId)
                            .nombreItem(nombreItem)
                            .ruta(ruta)
                            .icono(icono)
                            .orden(orden)
                            .build();
                    return menuItemDao.save(newMenuItem);
                });
    }

    // Helper: Para asignar Permiso a MenuItem
    private void assignPermisoToMenuItem(Integer menuItemId, Integer permisoId) {
        if (!menuItemPermisoDao.existsMenuItemPermiso(menuItemId, permisoId)) {
            log.debug("Asignando permiso ID {} a MenuItem ID {}", permisoId, menuItemId);
            menuItemPermisoDao.assignMenuItemPermiso(menuItemId, permisoId);
        } else {
            log.debug("Permiso ID {} ya asignado a MenuItem ID {}", permisoId, menuItemId);
        }
    }
}