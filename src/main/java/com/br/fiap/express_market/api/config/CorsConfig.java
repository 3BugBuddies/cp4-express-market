package com.br.fiap.express_market.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Libera o consumo da API a partir de front-ends hospedados em outra origem.
 * As origens permitidas ficam na propriedade cors.allowed-origins, então dá para
 * abrir em desenvolvimento e restringir no deploy sem recompilar.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                // sem isso o browser não enxerga o Location devolvido pelo POST
                .exposedHeaders("Location")
                .maxAge(3600);
    }
}
