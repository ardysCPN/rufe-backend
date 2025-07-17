package co.rufe.rufe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Nombre de tu esquema de seguridad JWT

        return new OpenAPI()
                .info(new Info()
                        .title("API Rufe Multi-Tenant SaaS")
                        .version("1.0")
                        .description("Documentación de la API REST para el sistema de gestión Multi-Tenant (SaaS) Rufe. " +
                                     "Incluye gestión de organizaciones, roles, usuarios y permisos de menú."))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP) // Tipo HTTP (para Bearer)
                                        .scheme("bearer") // Esquema Bearer
                                        .bearerFormat("JWT") // Formato del token
                                        .description("Introduce el token JWT de autenticación. Ej: 'Bearer eyJhbGci...'")));
    }
}
