package scancodes.backend.userauth.Services;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import scancodes.backend.userauth.Repository.UserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public DatabaseUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
   
}

@Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
    if (!user.isEnabled()){
        throw new DisabledException("Account not verified. Please enter the OTP sent to your email.");
    }
    
    var authorities = user.getRoles().stream()
      .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
      .map(SimpleGrantedAuthority::new)
      .toList();

    return new org.springframework.security.core.userdetails.User(
      user.getUsername(),
      user.getPasswordHash(),
      authorities
    );
  }
}