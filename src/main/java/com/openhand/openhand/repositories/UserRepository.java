package com.openhand.openhand.repositories;

import com.openhand.openhand.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    User findByPhone(String Phone);
}
