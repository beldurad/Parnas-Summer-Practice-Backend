package org.example.parnasservice.repository;

import java.util.Optional;
import java.util.UUID;
import org.example.parnasservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByWalletAddress(String walletAddress);
    Optional<User> findByUsername(String username);
    boolean existsByWalletAddress(String walletAddress);
    boolean existsByUsername(String username);
}
