package com.tarunnalla.inventory.auth;

import com.tarunnalla.inventory.common.ConflictException;
import com.tarunnalla.inventory.security.AppUser;
import com.tarunnalla.inventory.security.AppUserRepository;
import com.tarunnalla.inventory.security.JwtService;
import com.tarunnalla.inventory.security.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AppUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /** Public sign-up always creates a USER; admins are provisioned separately. */
    public void register(AuthRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username is already taken");
        }
        userRepository.save(new AppUser(request.username(), passwordEncoder.encode(request.password()), Role.USER));
    }

    /**
     * @throws org.springframework.security.core.AuthenticationException on bad credentials
     */
    public TokenResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        Role role = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .map(Role::valueOf)
                .findFirst()
                .orElse(Role.USER);

        String token = jwtService.generateToken(authentication.getName(), role);
        return TokenResponse.bearer(token, jwtService.getExpiration().toSeconds());
    }
}
