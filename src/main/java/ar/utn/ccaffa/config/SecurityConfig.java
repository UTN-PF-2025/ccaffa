package ar.utn.ccaffa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.http.HttpMethod;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                .requestMatchers("/api/auth/login", "/api/ws/**").permitAll() 
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/maquinas/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/rollos/tipoMateriales").hasAuthority("vendedor")
                .requestMatchers("/api/rollos/**").hasAnyAuthority("produccion", "deposito")
                .requestMatchers("/api/rollos_productos/**").hasAnyAuthority("produccion", "deposito")
                .requestMatchers("/api/clientes/**").hasAnyAuthority("produccion", "deposito", "vendedor")
                .requestMatchers("/api/ordenes-venta/**").hasAnyAuthority("vendedor", "produccion")
                .requestMatchers(HttpMethod.GET,"/api/ordenes-trabajo/{id}").hasAnyAuthority("supervisor", "operario")
                .requestMatchers(HttpMethod.GET,"/api/ordenes-trabajo").hasAnyAuthority("supervisor", "operario","produccion")
                .requestMatchers(HttpMethod.GET,"/api/ordenes-trabajo/programaciones-maquinas/**").hasAuthority("operario")
                .requestMatchers("/api/ordenes-trabajo/**").hasAuthority("produccion")
                .requestMatchers("/api/planner/**").hasAuthority("produccion")
                .requestMatchers("/api/controles-calidad/**").hasAuthority("operario")
                .requestMatchers("/api/controles-calidad/**").hasAuthority("validate")
                .requestMatchers("/api/defectos/{camaraId}/{imageId}/aceptar").hasAuthority("operario")
                .requestMatchers("/api/defectos/{camaraId}/{imageId}/rechazar").hasAuthority("operario")
                .requestMatchers("/api/images/**").hasAuthority("operario")
                .requestMatchers("/api/images/{id}/{filename}").hasAuthority("operario")
                .requestMatchers("/api/camaras/{id}/upload").hasAuthority("operario")
                .requestMatchers("/api/camaras/**").hasAuthority("operario")
                .requestMatchers("/api/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()

            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8080", "http://localhost:5174", "http://18.189.28.38", "https://ccaffa.art"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Camera-Id"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}