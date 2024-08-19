package com.hcmute.utezbe.config;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;


@Configuration
public class SwaggerConfig  {
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
    }
    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt"))
                .components(new Components().addSecuritySchemes("bearer-jwt", securityScheme()))
                .info(new Info().title("UTEZBE API").version("1.0.0"));

    }

}
