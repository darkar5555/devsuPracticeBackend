package com.devsu.bank.infrastructure.persistence;

import com.devsu.bank.application.port.out.TransactionRepository;
import com.devsu.bank.domain.model.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    @Override
    public Transaction save(Transaction transaction) {
        return jpaRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Transaction> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Transaction> findLastByAccountId(Long accountId) {
        return jpaRepository.findFirstByAccountIdOrderByIdDesc(accountId);
    }

    @Override
    public List<Transaction> findByAccountIdAndDateBetween(Long accountId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.findByAccountIdAndDateBetweenOrderByDateDescIdDesc(accountId, from, to);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
