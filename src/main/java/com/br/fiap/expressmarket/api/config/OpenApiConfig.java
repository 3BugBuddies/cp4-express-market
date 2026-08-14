package com.br.fiap.expressmarket.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI expressMarketOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Express Market API")
                .description("CRUD do estoque de um mercado express, com respostas no padrão HATEOAS "
                        + "(nível 3 de maturidade de Richardson) sobre a tabela TDS_TB_mercado.")
                .version("v1"));
    }
}
