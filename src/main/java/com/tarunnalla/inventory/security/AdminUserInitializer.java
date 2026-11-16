package com.tarunnalla.inventory.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Creates the first ADMIN account from ADMIN_USERNAME / ADMIN_PASSWORD
 * so the API can be used right after startup. Public registration only
 * ever creates USER accounts.
 */
@Component
public class AdminUserInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminUserInitializer(AppUserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.admin.username:}") String username,
                                @Value("${app.admin.password:}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            log.info("ADMIN_USERNAME/ADMIN_PASSWORD not set; skipping admin account creation");
            return;
        }
        if (userRepository.existsByUsername(username)) {
            return;
        }
        userRepository.save(new AppUser(username, passwordEncoder.encode(password), Role.ADMIN));
        log.info("Created admin account '{}'", username);
    }
}
