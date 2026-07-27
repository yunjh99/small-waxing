package com.example.waxing.user.repository;

import com.example.waxing.user.domain.User;
import com.example.waxing.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findFirstByRole(Role role);
}
