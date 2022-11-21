package com.capco.interview.features.config;

import com.capco.interview.features.api.model.ErrorBody;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

    private static final String CONTROLLERS_PACKAGE = "com.capco.interview.features.controllers";
    private static final String PASS_AS = "header";
    private static final String SECURITY_SCHEME_NAME = "JWT";
    private static final String AUTHORIZATION_SCOPE = "global";
    private static final String AUTHORIZATION_DESCRIPTION = "accessEverything";

    @Bean
    public Docket api(TypeResolver typeResolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .additionalModels(
                        typeResolver.resolve(ErrorBody.class)
                )
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(securityScheme()))
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage(CONTROLLERS_PACKAGE))
                .paths(PathSelectors.any())
                .build();
    }

    private SecurityScheme securityScheme() {
        return new ApiKey(SECURITY_SCHEME_NAME, HttpHeaders.AUTHORIZATION, PASS_AS);
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope(AUTHORIZATION_SCOPE, AUTHORIZATION_DESCRIPTION);
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference(SECURITY_SCHEME_NAME, authorizationScopes));
    }

}
