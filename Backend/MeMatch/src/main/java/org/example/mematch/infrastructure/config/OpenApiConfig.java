package org.example.mematch.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI meMatchOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development server");

        Contact contact = new Contact();
        contact.setEmail("support@mematch.com");
        contact.setName("MeMatch Support");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("MeMatch API")
                .version("1.0.0")
                .contact(contact)
                .description("API documentation for MeMatch - A meme-based matchmaking application. " +
                        "This API allows users to create profiles, post memes, match with other users, " +
                        "and interact through likes and comments. " +
                        "Use the /api/auth/register endpoint to create an account, then /api/auth/login to get a JWT token. " +
                        "Include the token in the Authorization header as 'Bearer {token}' for protected endpoints.")
                .license(license);

        // Add JWT security scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token");

        Components components = new Components()
                .addSecuritySchemes("bearerAuth", securityScheme);

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}

