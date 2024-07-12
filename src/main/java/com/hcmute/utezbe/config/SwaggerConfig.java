package com.hcmute.utezbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .globalRequestParameters(customRequestHeader())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfoMetaData());
    }

    private List<RequestParameter> customRequestHeader() {
        RequestParameter requestParameter = new RequestParameterBuilder()
                .name("Authorization")
                .description("Bearer Token")
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .required(false)
//                .example(new Example("Bearer "))
                .build();
        return List.of(requestParameter);
    }

    private ApiInfo apiInfoMetaData() {
        return new ApiInfoBuilder().title("UTEz")
                .description("API Endpoint Decoration")
                .contact(new Contact("UTEz Dev-Team", "", ""))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0.0")
                .build();
    }

}
