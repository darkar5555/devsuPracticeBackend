package com.devsu.bank.application.port.out;

import com.devsu.bank.domain.model.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(Long id);

    List<Transaction> findAll();

    Optional<Transaction> findLastByAccountId(Long accountId);

    List<Transaction> findByAccountIdAndDateBetween(Long accountId, LocalDateTime from, LocalDateTime to);

    void deleteById(Long id);
}
