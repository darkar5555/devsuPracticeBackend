package com.devsu.bank.application.service;

import com.devsu.bank.application.port.out.AccountRepository;
import com.devsu.bank.application.port.out.CustomerRepository;
import com.devsu.bank.application.port.out.TransactionRepository;
import com.devsu.bank.domain.exception.BusinessRuleException;
import com.devsu.bank.domain.exception.DuplicateResourceException;
import com.devsu.bank.domain.exception.ResourceNotFoundException;
import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.Customer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public Account create(Account account, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente " + customerId + " no encontrado"));
        ensureAccountNumberIsFree(account.getAccountNumber());
        account.setCustomer(customer);
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return getAccount(id);
    }

    public Account update(Long id, Account changes) {
        Account account = getAccount(id);
        if (!account.getAccountNumber().equals(changes.getAccountNumber())) {
            ensureAccountNumberIsFree(changes.getAccountNumber());
        }
        ensureInitialBalanceCanChange(account, changes.getInitialBalance());
        account.setAccountNumber(changes.getAccountNumber());
        account.setAccountType(changes.getAccountType());
        account.setInitialBalance(changes.getInitialBalance());
        account.setStatus(changes.getStatus());
        return accountRepository.save(account);
    }

    public Account patch(Long id, Account changes) {
        Account account = getAccount(id);
        if (changes.getAccountNumber() != null
                && !changes.getAccountNumber().equals(account.getAccountNumber())) {
            ensureAccountNumberIsFree(changes.getAccountNumber());
        }
        if (changes.getInitialBalance() != null) {
            ensureInitialBalanceCanChange(account, changes.getInitialBalance());
        }
        Optional.ofNullable(changes.getAccountNumber()).ifPresent(account::setAccountNumber);
        Optional.ofNullable(changes.getAccountType()).ifPresent(account::setAccountType);
        Optional.ofNullable(changes.getInitialBalance()).ifPresent(account::setInitialBalance);
        Optional.ofNullable(changes.getStatus()).ifPresent(account::setStatus);
        return accountRepository.save(account);
    }

    public void delete(Long id) {
        getAccount(id);
        accountRepository.deleteById(id);
    }

    private Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta " + id + " no encontrada"));
    }

    private void ensureInitialBalanceCanChange(Account account, BigDecimal newInitialBalance) {
        if (account.getInitialBalance().compareTo(newInitialBalance) != 0
                && transactionRepository.findLastByAccountId(account.getId()).isPresent()) {
            throw new BusinessRuleException(
                    "El saldo inicial no se puede cambiar cuando la cuenta ya tiene movimientos");
        }
    }

    private void ensureAccountNumberIsFree(String accountNumber) {
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new DuplicateResourceException("Ya existe una cuenta con el número " + accountNumber);
        }
    }
}
