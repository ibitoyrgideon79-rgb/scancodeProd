package scancodes.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CorsConfigurationSource corsConfigurationSource;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        CorsConfigurationSource corsConfigurationSource) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.corsConfigurationSource = corsConfigurationSource;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  SecurityFilterChain appSecurity(HttpSecurity http) throws Exception {
    return http
      .cors(cors -> cors.configurationSource(corsConfigurationSource))
      .csrf(csrf -> csrf.disable())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        // Public auth endpoints
        .requestMatchers("/auth/**", "/api/auth/login").permitAll()
        // Public storefront endpoints
        .requestMatchers(HttpMethod.GET, "/api/business/storefronts/{slug}").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/storefronts/*/products").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/storefronts/*/config").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/orders/*").permitAll()
        // Public order creation (customers place orders without auth)
        .requestMatchers(HttpMethod.POST, "/api/storefronts/*/orders").permitAll()
        // Public slug-based storefront access
        .requestMatchers(HttpMethod.GET, "/{slug:[a-z0-9][a-z0-9-]{0,62}}").permitAll()
        // Static resources
        .requestMatchers("/", "/css/**", "/js/**", "/error", "/actuator/health").permitAll()
        // Admin endpoints
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        // Everything else requires authentication
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }
}
