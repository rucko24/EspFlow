package com.esp.espflow.security;

import com.esp.espflow.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.with(VaadinSecurityConfigurer.vaadin(), configurer -> configurer.loginView(LoginView.class))
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/images/*.png").permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/images/*.svg").permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/line-awesome/**/*.svg").permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/images/*.png").permitAll())
                .build();
    }
}
