package scancodes.backend.userauth.Services;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scancodes.backend.userauth.Entity.AppUserEntity;
import scancodes.backend.userauth.Repository.UserRepository;



@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public AppUserEntity register(String username, String email, String rawPassword) {
    var normalizedEmail = email.toLowerCase(Locale.ROOT);

    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("Username already taken");
    }
    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new IllegalArgumentException("Email already used");
    }

    var user = new AppUserEntity();
    user.setUsername(username);
    user.setEmail(normalizedEmail);
    user.setPasswordHash(passwordEncoder.encode(rawPassword));
    user.setEnabled(false);
    user.getRoles().add("USER");

    return userRepository.save(user);
  }
}