package com.devsu.bank.infrastructure.persistence;

import com.devsu.bank.application.port.out.AccountRepository;
import com.devsu.bank.domain.model.Account;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository jpaRepository;

    @Override
    public Account save(Account account) {
        return jpaRepository.save(account);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public Optional<Account> findByAccountNumberForUpdate(String accountNumber) {
        return jpaRepository.findByAccountNumberForUpdate(accountNumber);
    }

    @Override
    public List<Account> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Account> findByCustomerId(Long customerId) {
        return jpaRepository.findByCustomerId(customerId);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
