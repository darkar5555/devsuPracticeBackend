package com.devsu.bank.application.port.out;

import com.devsu.bank.domain.model.Account;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(Long id);

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumberForUpdate(String accountNumber);

    List<Account> findAll();

    List<Account> findByCustomerId(Long customerId);

    boolean existsByAccountNumber(String accountNumber);

    void deleteById(Long id);
}
