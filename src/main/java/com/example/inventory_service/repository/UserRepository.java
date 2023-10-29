package com.example.inventory_service.repository;

import com.example.inventory_service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}