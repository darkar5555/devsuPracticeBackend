package com.devsu.bank.infrastructure.persistence;

import com.devsu.bank.domain.model.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findFirstByAccountIdOrderByIdDesc(Long accountId);

    List<Transaction> findByAccountIdAndDateBetweenOrderByDateDescIdDesc(
            Long accountId, LocalDateTime from, LocalDateTime to);
}
