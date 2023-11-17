package de.streamfusion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Security {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/").permitAll();
            auth.requestMatchers("/register").permitAll();
            auth.requestMatchers("/login").permitAll();
            auth.requestMatchers("/video").permitAll();
            auth.requestMatchers("/cdn/v").permitAll();
            auth.requestMatchers("/cdn/v/thumbnail").permitAll();
            auth.requestMatchers("/error").permitAll();

            auth.requestMatchers("/account").authenticated();
        })
            .formLogin(formLogin -> formLogin.loginPage("/login"))
            .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
            .build();
    }
}
