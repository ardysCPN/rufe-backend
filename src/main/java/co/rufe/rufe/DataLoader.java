package co.rufe.rufe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

/**
 * Gestiona la carga inicial de datos y el reset del sistema.
 * Utiliza 'db/setup.sql' como fuente única de verdad para la estructura y datos
 * maestros.
 */
@Configuration
@Slf4j
public class DataLoader {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Value("${app.dataloader.enabled:false}")
    private boolean enabled;

    @Value("${app.dataloader.force-reset:false}")
    private boolean forceReset;

    @Value("${app.dataloader.i-am-sure:false}")
    private boolean iAmSure;

    @Bean
    @Transactional
    public CommandLineRunner initDatabase() {
        return args -> {
            if (!enabled) {
                log.info("DataLoader: Ejecución omitida (APP_DATALOADER_ENABLED=false).");
                return;
            }

            log.info("MS-RUFE: Iniciando gestión de base de datos...");

            try {
                // 1. Verificar si la tabla fundamental 'departamento' existe
                boolean isInitialized = checkIfTableExists("departamento");

                // 2. Lógica de Reset Forzado
                if (forceReset) {
                    log.warn("#################################################################");
                    log.warn("MS-RUFE: ¡BLOQUE DE RESET FORZADO ACTIVADO! (force-reset=true)");

                    // --- SEGURIDAD: Validar si hay datos reales de transacciones ---
                    if (isInitialized && checkIfTableExists("registros_rufe")) {
                        Integer recordCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM registros_rufe",
                                Integer.class);
                        if (recordCount != null && recordCount > 0 && !iAmSure) {
                            log.error("ERROR DE SEGURIDAD: Se detectaron {} registros en RUFE.", recordCount);
                            log.error("Abortando reset forzado para evitar pérdida de datos del cliente.");
                            log.error("Si deseas borrar TODO, activa APP_DATALOADER_I_AM_SURE=true.");
                            log.error("#################################################################");
                            return;
                        }
                    }

                    log.info("MS-RUFE: Limpiando esquema público (DROP/CREATE SCHEMA)...");
                    // Nota: CASCADE borra tipos, funciones y tablas.
                    jdbcTemplate.execute("DROP SCHEMA public CASCADE; CREATE SCHEMA public;");
                    log.info("MS-RUFE: Esquema reseteado. Procediendo a recrear desde setup.sql...");
                    executeSetupSql();
                    return;
                }

                // 3. Inicialización Automática (solo si está vacío)
                if (!isInitialized) {
                    log.info("MS-RUFE: Base de datos virgen detectada. Ejecutando setup inicial...");
                    executeSetupSql();
                } else {
                    log.info("MS-RUFE: La base de datos ya está inicializada. No se realizaron cambios.");
                }

            } catch (Exception e) {
                log.error("MS-RUFE: El proceso de carga falló: {}", e.getMessage(), e);
            }
        };
    }

    private boolean checkIfTableExists(String tableName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?",
                    Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void executeSetupSql() {
        log.info("MS-RUFE: Ejecutando 'db/setup.sql'...");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/setup.sql"));
        populator.setSqlScriptEncoding("UTF-8");
        populator.setContinueOnError(false);
        populator.execute(dataSource);
        log.info("MS-RUFE: ¡Inicialización completada con éxito!");
    }
}
