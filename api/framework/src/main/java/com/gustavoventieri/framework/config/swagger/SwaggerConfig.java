package com.gustavoventieri.framework.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(name = "cookieAuth", // Nome do esquema de segurança
        type = SecuritySchemeType.APIKEY, // Tipo apiKey
        in = SecuritySchemeIn.COOKIE, // Usar cookie
        paramName = "token" // Nome do cookie que contém o JWT
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LinkLine API")
                        .version("1.0")
                        .description(
                                """
                                        LinkLine is a secure and scalable real-time chat system built with Java, SpringBoot, React, Docker, and Hibernate.

                                        Designed for professional, educational, and social use, it offers customizable messaging with low latency, strong data security, and a focus on user experience.""")
                        .contact(new Contact()
                                .name("Gustavo Ventieri")
                                .url("https://www.linkline.com")
                                .url("https://github.com/gustavoventieri"))
                        .termsOfService("Terms of Service: LinkLine")
                        .license(new License()
                                .name("LinkLine License")
                                .url("https://www.linkline.com")));
    }
}
