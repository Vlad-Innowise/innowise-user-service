package by.innowise.internship.userService.core.config;

import by.innowise.internship.security.dto.Role;
import by.innowise.internship.security.filter.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter filter) throws Exception {

        http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                                       session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> requests
                    .requestMatchers("/api/v1/admin", "/api/v1/admin/**").hasRole(Role.ADMIN.name())
                    .anyRequest().authenticated()
            )

            .exceptionHandling(configurer ->
                                       configurer
                                               .authenticationEntryPoint((req, resp, authException) ->
                                                                                 resp.setStatus(
                                                                                         HttpServletResponse.SC_UNAUTHORIZED))
                                               .accessDeniedHandler((req, resp, authException) ->
                                                                            resp.setStatus(
                                                                                    HttpServletResponse.SC_FORBIDDEN)
                                               )
            )
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
