package com.secretlab.kvstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /*
    * For API Documentation
    * */
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Versioned Key-Value Store API")
                        .version("1.0.0")
                        .description("Secretlab tech exercise - version controlled KV store"));
    }
}
