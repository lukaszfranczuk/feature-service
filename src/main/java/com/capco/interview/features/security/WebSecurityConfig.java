package com.capco.interview.features.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String ROOT_ENDPOINT = "/**";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String H2_CONSOLE_ENDPOINT = "/h2-console/**";
    private static final String[] SWAGGER_UI_ENDPOINTS = {
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/definitions/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**",
            "/configuration/security",
            "/configuration/ui"};

    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;
    @Value("${springfox.documentation.swagger-ui.enabled:false}")
    private boolean swaggerUiEnabled;

    private final JwtService jwtService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final DatabaseUserDetailsService userDetailsService;
    private final AuthenticationEntryPoint delegatedAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, ROOT_ENDPOINT).permitAll()
                .antMatchers(HttpMethod.POST, LOGIN_ENDPOINT).permitAll();
        if (swaggerUiEnabled) {
            http.authorizeRequests()
                    .antMatchers(SWAGGER_UI_ENDPOINTS).permitAll();
        }
        if (h2ConsoleEnabled) {
            http.authorizeRequests()
                    .antMatchers(H2_CONSOLE_ENDPOINT).permitAll();
            http.headers().frameOptions().disable();
        }

        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new UserAndPasswordLoginFilter(LOGIN_ENDPOINT, authenticationConfiguration.getAuthenticationManager(), jwtService),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtService),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(delegatedAuthenticationEntryPoint);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authenticationProvider;
    }

}
