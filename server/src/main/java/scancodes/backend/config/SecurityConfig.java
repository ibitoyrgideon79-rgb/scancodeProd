package scancodes.backend.config;

import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  private static final Set<String> RESERVED_PUBLIC_PATHS = Set.of(
    "/", "/admin", "/api", "/auth", "/css", "/dashboard", "/error", "/js", "/settings"
  );

  @Bean
  SecurityFilterChain appSecurity(HttpSecurity http) throws Exception {
    RequestMatcher publicStorefrontSlug = request -> {
      var path = request.getRequestURI();

      return HttpMethod.GET.matches(request.getMethod())
        && path.matches("/[a-z0-9][a-z0-9-]{0,62}")
        && !RESERVED_PUBLIC_PATHS.contains(path);
    };

    return http
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(publicStorefrontSlug).permitAll()
        .requestMatchers("/", "/auth/**", "/css/**", "/js/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/business/storefronts/**").permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
      )
      .formLogin(form -> form
        .loginPage("/auth/login")        
        .defaultSuccessUrl("/dashboard", true)
      )
      .logout(logout -> logout
        .logoutUrl("/auth/logout")
        .logoutSuccessUrl("/")
      )
      .build();
  }
}
