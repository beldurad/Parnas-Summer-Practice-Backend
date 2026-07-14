package org.example.parnasservice.repository;

import java.util.Optional;
import org.example.parnasservice.entity.BlockchainTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockchainTransactionRepository extends JpaRepository<BlockchainTransaction, String> {
    Optional<BlockchainTransaction> findByHash(String hash);

    java.util.List<BlockchainTransaction> findTop10ByToOrderByCreatedAtDesc(String to);

    java.util.List<BlockchainTransaction> findTop10ByOrderByCreatedAtDesc();
}
