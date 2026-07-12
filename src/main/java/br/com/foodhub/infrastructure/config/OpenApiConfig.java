package br.com.foodhub.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foodhub() {
        return new OpenAPI()
                .info(new Info()
                        .title("FoodHub API")
                        .version("v1.0.0")
                        .description("REST API for restaurant, menu and user management.")
                        .license(new License().name("FoodHub License").url("https://github.com/JuniorGDev")));
    }
}
