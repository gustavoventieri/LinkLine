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
@SecurityScheme(
    name = "cookieAuth",              // nome do esquema de segurança
    type = SecuritySchemeType.APIKEY, // tipo apiKey
    in = SecuritySchemeIn.COOKIE,     // usar o cookie
    paramName = "token"          // nome do cookie que contem o JWT
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API HAE")
                .description("\n" + //
                        "Link Line is a secure and scalable real-time chat system built with TypeScript, Node.js, React, Docker, and Prisma. Designed for professional, educational, and social use, it offers customizable messaging with low latency, strong data security, and a focus on user experience.")
                .version("1.0")
                .contact(new Contact()
                    .name("Gustavo Ventieri")
                    .url("https://www.linkline.com")
                    .email("contato@linkline.com"))
                .termsOfService("Termos de uso: Link Line")
                .license(new License()
                    .name("Licença - Link Line")
                    .url("https://www.linkline.com"))
            );
    }
}