package com.gustavoventieri.framework.config.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica o CORS a todos os endpoints

                .allowedOrigins( "http://localhost:5173", "https://8t0866rg-5173.brs.devtunnels.ms") 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") 
                .allowedHeaders("*") 
                .allowCredentials(true)
                .maxAge(3600); 
    }
}
 