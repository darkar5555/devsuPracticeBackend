package com.devsu.bank.infrastructure.persistence;

import com.devsu.bank.domain.model.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {

    @Override
    @EntityGraph(attributePaths = "account")
    List<Transaction> findAll();

    @Override
    @EntityGraph(attributePaths = "account")
    Optional<Transaction> findById(Long id);

    Optional<Transaction> findFirstByAccountIdOrderByIdDesc(Long accountId);

    List<Transaction> findByAccountIdAndDateBetweenOrderByDateDescIdDesc(
            Long accountId, LocalDateTime from, LocalDateTime to);
}
