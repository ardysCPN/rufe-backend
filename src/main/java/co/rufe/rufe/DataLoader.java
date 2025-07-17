package co.rufe.rufe;

import co.rufe.rufe.dao.IMenuItemDao;
import co.rufe.rufe.dao.IOrganizacionDao;
import co.rufe.rufe.dao.IRolDao;
import co.rufe.rufe.dao.IRolPermisoDao; // ¡Importante!
import co.rufe.rufe.dao.IUsuarioDao;
import co.rufe.rufe.model.MenuItem;
import co.rufe.rufe.model.Organizacion;
import co.rufe.rufe.model.Rol;
import co.rufe.rufe.model.Usuario;
import co.rufe.rufe.util.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@Slf4j
public class DataLoader {

    private final PasswordEncoder passwordEncoder;
    private final IOrganizacionDao organizacionDao;
    private final IRolDao rolDao;
    private final IUsuarioDao usuarioDao;
    private final IMenuItemDao menuItemDao;
    private final IRolPermisoDao rolPermisoDao; // Inyectado

    public DataLoader(PasswordEncoder passwordEncoder,
                      IOrganizacionDao organizacionDao,
                      IRolDao rolDao,
                      IUsuarioDao usuarioDao,
                      IMenuItemDao menuItemDao,
                      IRolPermisoDao rolPermisoDao) { // Inyectar
        this.passwordEncoder = passwordEncoder;
        this.organizacionDao = organizacionDao;
        this.rolDao = rolDao;
        this.usuarioDao = usuarioDao;
        this.menuItemDao = menuItemDao;
        this.rolPermisoDao = rolPermisoDao;
    }

    @Bean
    @Transactional
    public CommandLineRunner initDatabase() {
        return args -> {
            log.info("Iniciando verificación y carga de datos iniciales...");

            String defaultOrgName = "GlobalCorp";
            Organizacion globalOrg = organizacionDao.findByNombreOrganizacion(defaultOrgName)
                    .orElseGet(() -> {
                        Organizacion newOrg = new Organizacion();
                        newOrg.setNombreOrganizacion(defaultOrgName);
                        newOrg.setActiva(true);
                        log.info("Organización inicial '{}' creada.", defaultOrgName);
                        return organizacionDao.save(newOrg);
                    });

            TenantContext.setCurrentOrganizationId(globalOrg.getId());

            // --- Creación de MenuItems (Permisos) si no existen ---
            // Define aquí todos los permisos que tu aplicación necesitará.
            // El 'nombre_item' se usará como la autoridad en @PreAuthorize.
            MenuItem orgCreate = createOrGetMenuItem("ORGANIZACION_CREATE", "/api/organizaciones/create");
            MenuItem orgRead = createOrGetMenuItem("ORGANIZACION_READ", "/api/organizaciones/read");
            MenuItem orgUpdate = createOrGetMenuItem("ORGANIZACION_UPDATE", "/api/organizaciones/update");
            MenuItem orgDelete = createOrGetMenuItem("ORGANIZACION_DELETE", "/api/organizaciones/delete");

            MenuItem rolCreate = createOrGetMenuItem("ROL_CREATE", "/api/roles/create");
            MenuItem rolRead = createOrGetMenuItem("ROL_READ", "/api/roles/read");
            MenuItem rolList = createOrGetMenuItem("ROL_LIST", "/api/roles/list");
            MenuItem rolUpdate = createOrGetMenuItem("ROL_UPDATE", "/api/roles/update");
            MenuItem rolDelete = createOrGetMenuItem("ROL_DELETE", "/api/roles/delete");

            MenuItem userCreate = createOrGetMenuItem("USUARIO_CREATE", "/api/usuarios/create");
            MenuItem userRead = createOrGetMenuItem("USUARIO_READ", "/api/usuarios/read");
            MenuItem userList = createOrGetMenuItem("USUARIO_LIST", "/api/usuarios/list");
            MenuItem userUpdate = createOrGetMenuItem("USUARIO_UPDATE", "/api/usuarios/update");
            MenuItem userDelete = createOrGetMenuItem("USUARIO_DELETE", "/api/usuarios/delete");

            MenuItem menuItemCreate = createOrGetMenuItem("MENU_ITEM_CREATE", "/api/menu-items/create");
            MenuItem menuItemRead = createOrGetMenuItem("MENU_ITEM_READ", "/api/menu-items/read");
            MenuItem menuItemList = createOrGetMenuItem("MENU_ITEM_LIST", "/api/menu-items/list");
            MenuItem menuItemUpdate = createOrGetMenuItem("MENU_ITEM_UPDATE", "/api/menu-items/update");
            MenuItem menuItemDelete = createOrGetMenuItem("MENU_ITEM_DELETE", "/api/menu-items/delete");
            MenuItem menuItemAssign = createOrGetMenuItem("MENU_ITEM_ASSIGN", "/api/menu-items/assign");
            MenuItem menuItemRevoke = createOrGetMenuItem("MENU_ITEM_REVOKE", "/api/menu-items/revoke");
            MenuItem menuItemListByRole = createOrGetMenuItem("MENU_ITEM_LIST_BY_ROLE", "/api/menu-items/by-role");


            // --- Rol ADMIN_GLOBAL ---
            String adminGlobalRoleName = "ADMIN_GLOBAL";
            Rol adminGlobalRol = rolDao.findByOrganizacionIdAndNombreRol(globalOrg.getId(), adminGlobalRoleName)
                    .orElseGet(() -> {
                        Rol newRol = new Rol();
                        newRol.setNombreRol(adminGlobalRoleName);
                        newRol.setDescripcion("Administrador global del sistema con todos los permisos.");
                        newRol.setActivo(true);
                        newRol.setOrganizacionId(globalOrg.getId());
                        Rol savedRol = rolDao.save(newRol);
                        log.info("Rol inicial '{}' creado.", adminGlobalRoleName);
                        return savedRol;
                    });

            // Asignar TODOS los permisos al ADMIN_GLOBAL si no están ya asignados
            Set<MenuItem> allPermissions = new HashSet<>(Arrays.asList(
                orgCreate, orgRead, orgUpdate, orgDelete,
                rolCreate, rolRead, rolList, rolUpdate, rolDelete,
                userCreate, userRead, userList, userUpdate, userDelete,
                menuItemCreate, menuItemRead, menuItemList, menuItemUpdate, menuItemDelete, menuItemAssign, menuItemRevoke, menuItemListByRole
            ));

            for (MenuItem permission : allPermissions) {
                if (!rolPermisoDao.existsPermission(adminGlobalRol.getId(), permission.getId())) {
                    rolPermisoDao.assignPermission(adminGlobalRol.getId(), permission.getId());
                    log.debug("Permiso '{}' asignado a rol '{}'.", permission.getNombreItem(), adminGlobalRol.getNombreRol());
                }
            }


            // --- Usuario admin@global.com ---
            String adminEmail = "admin@global.com";
            usuarioDao.findByOrganizacionIdAndEmail(globalOrg.getId(), adminEmail)
                    .orElseGet(() -> {
                        Usuario adminUser = new Usuario();
                        adminUser.setNombreCompleto("Admin Global Rufe");
                        adminUser.setEmail(adminEmail);
                        adminUser.setPasswordHash(passwordEncoder.encode("adminpass"));
                        adminUser.setActivo(true);
                        adminUser.setOrganizacionId(globalOrg.getId());
                        adminUser.setRolId(adminGlobalRol.getId());
                        log.info("Usuario inicial '{}' creado para la organización '{}'.", adminEmail, defaultOrgName);
                        return usuarioDao.save(adminUser);
                    });

            // --- Crear un Rol de ejemplo para ORGANIZACION_ADMIN si no existe ---
            String orgAdminRoleName = "ADMIN_ORGANIZACION";
            Rol orgAdminRol = rolDao.findByOrganizacionIdAndNombreRol(globalOrg.getId(), orgAdminRoleName)
                    .orElseGet(() -> {
                        Rol newRol = new Rol();
                        newRol.setNombreRol(orgAdminRoleName);
                        newRol.setDescripcion("Administrador de la organización específica.");
                        newRol.setActivo(true);
                        newRol.setOrganizacionId(globalOrg.getId());
                        Rol savedRol = rolDao.save(newRol);
                        log.info("Rol '{}' de organización creado.", orgAdminRoleName);
                        return savedRol;
                    });

            // Asignar permisos comunes para un ADMIN_ORGANIZACION
            Set<MenuItem> orgAdminPermissions = new HashSet<>(Arrays.asList(
                orgRead, orgUpdate,
                rolCreate, rolRead, rolList, rolUpdate, rolDelete,
                userCreate, userRead, userList, userUpdate, userDelete,
                menuItemRead, menuItemList, menuItemAssign, menuItemRevoke, menuItemListByRole
            ));

            for (MenuItem permission : orgAdminPermissions) {
                if (!rolPermisoDao.existsPermission(orgAdminRol.getId(), permission.getId())) {
                    rolPermisoDao.assignPermission(orgAdminRol.getId(), permission.getId());
                    log.debug("Permiso '{}' asignado a rol '{}'.", permission.getNombreItem(), orgAdminRol.getNombreRol());
                }
            }


            TenantContext.clear();
            log.info("Carga de datos iniciales completada.");
        };
    }

    private MenuItem createOrGetMenuItem(String name, String route) {
        // Usa existsByNombreItem y findByNombreItem para evitar duplicados.
        return menuItemDao.existsByNombreItem(name)
                ? menuItemDao.findByNombreItem(name).orElseThrow(() -> new RuntimeException("MenuItem not found after checking existence! This should not happen."))
                : menuItemDao.save(new MenuItem(null, null, name, route, null, null)); // ID y ParentId nulos al crear
    }
}