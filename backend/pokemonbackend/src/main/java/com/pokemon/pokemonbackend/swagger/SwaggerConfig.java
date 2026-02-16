package com.pokemon.pokemonbackend.swagger;

//imports correspondientes

import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Configuración Swagger para la generación de documentación de la API REST
 * <p>
 * HTML: http://localhost:8080/swagger-ui/
 * JSON: http://localhost:8080/v2/api-docs
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PokeBuilder API")
                        .version("1.0")
                        .description("Documentación de la API de PokeBuilder"));
    }
}
