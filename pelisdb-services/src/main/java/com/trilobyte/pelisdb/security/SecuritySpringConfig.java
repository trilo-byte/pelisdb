package com.trilobyte.pelisdb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecuritySpringConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    MovieUserDetailsService userDetailsSrv;

    @Override
    public void configure(final AuthenticationManagerBuilder authenticationManagerBuilder)
            throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsSrv)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                // exception management
                .exceptionHandling()
                .and()
                .authorizeRequests()
                // Request: swagger public
                .antMatchers(
                        HttpMethod.GET,
                        "/*-rest.yml",
                        "/v3/api-docs",
                        "/swagger-resources/**",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "favicon.ico")
                .permitAll()
                // Request: actuator public
                .antMatchers(HttpMethod.GET, "/actuator", "/actuator/**")
                .permitAll()
                // Request: default authenticated
                .anyRequest()
                .authenticated()
                .and()
                // Basic Auth
                .httpBasic()
                .realmName("HeroApp")
                .and()
                // session management
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // headers
                .headers()
                .cacheControl()
                .and()
                .frameOptions()
                .and()
                .contentTypeOptions()
                .and()
                .xssProtection();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
