package com.openhand.openhand.repositories;

import com.openhand.openhand.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByPhone(String Phone);
}
