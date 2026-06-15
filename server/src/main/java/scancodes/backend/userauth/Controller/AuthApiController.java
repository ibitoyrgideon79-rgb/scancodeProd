package scancodes.backend.userauth.Controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import scancodes.backend.config.JwtService;
import scancodes.backend.userauth.Repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthApiController(AuthenticationManager authenticationManager,
                             UserRepository userRepository,
                             JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        var emailOrUsername = body.getOrDefault("email", body.get("username"));
        var password = body.get("password");

        // Resolve username if email was provided (normalize email case)
        var lookup = emailOrUsername == null ? "" : emailOrUsername.toLowerCase(Locale.ROOT);
        var userOpt = userRepository.findByEmail(lookup);
        String username = userOpt.map(u -> u.getUsername()).orElse(emailOrUsername);

        // Authenticate — throws BadCredentialsException or DisabledException on failure
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        var user = userRepository.findByUsername(username).orElseThrow();

        // Generate JWT
        var roles = user.getRoles().stream().toList();
        var token = jwtService.generateToken(user.getUsername(), roles);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "roles", roles
                )
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(java.security.Principal principal) {
        var user = userRepository.findByUsername(principal.getName()).orElseThrow();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "roles", user.getRoles().stream().toList()
        ));
    }
}
