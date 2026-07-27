package com.example.waxing.global.config;

import com.example.waxing.user.domain.User;
import com.example.waxing.user.domain.Role;
import com.example.waxing.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitConfig {

    @Bean
    public CommandLineRunner createAdminAccount(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username:}") String adminUsername,
            @Value("${app.admin.password:}") String adminPassword
    ) {
        return args -> {
            if (adminUsername.isBlank() || adminPassword.isBlank()) {
                return;
            }

            String encodedPassword = passwordEncoder.encode(adminPassword);

            userRepository.findFirstByRole(Role.ADMIN).ifPresentOrElse(
                    admin -> {
                        admin.changeAdminCredentials(adminUsername, encodedPassword);
                        userRepository.save(admin);
                    },
                    () -> userRepository.save(
                            User.createAdmin(adminUsername, encodedPassword)
                    )
            );
        };
    }
}
