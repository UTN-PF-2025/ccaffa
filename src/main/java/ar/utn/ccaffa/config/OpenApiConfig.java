package ar.utn.ccaffa.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("CCAFFA - Control de Calidad API")
                        .description("API REST para el sistema de control de calidad de fabricación y gestión de producción. " +
                                "Este sistema permite gestionar órdenes de venta, órdenes de trabajo, rollos, máquinas, " +
                                "clientes, proveedores, certificados y control de calidad.\n\n" +
                                "## Autenticación\n" +
                                "La mayoría de los endpoints requieren autenticación mediante JWT. " +
                                "Primero realice login en `/api/auth/login` para obtener el token, " +
                                "luego incluya el token en el header `Authorization: Bearer {token}` en sus peticiones.\n\n" +
                                "## Documentación Completa\n" +
                                "Para ver ejemplos detallados de uso con curl, consulte el archivo API-DOCS.md")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo CCAFFA")
                                .email("contacto@ccaffa.utn.ar"))
                        .license(new License()
                                .name("UTN License")
                                .url("https://www.utn.edu.ar")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api.ccaffa.com")
                                .description("Servidor de Producción")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese el token JWT obtenido del endpoint /api/auth/login")));
    }
}
