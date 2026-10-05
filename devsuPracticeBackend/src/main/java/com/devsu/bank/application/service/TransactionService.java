package com.devsu.bank.application.service;

import com.devsu.bank.application.port.out.AccountRepository;
import com.devsu.bank.application.port.out.TransactionRepository;
import com.devsu.bank.domain.exception.BusinessRuleException;
import com.devsu.bank.domain.exception.InsufficientBalanceException;
import com.devsu.bank.domain.exception.ResourceNotFoundException;
import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.Transaction;
import com.devsu.bank.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final Clock clock;

    public Transaction create(String accountNumber, TransactionType type, BigDecimal amount) {
        Account account = lockAccount(accountNumber);
        ensureActive(account);
        BigDecimal signedAmount = signedAmount(type, amount);
        BigDecimal balance = currentBalance(account).add(signedAmount);
        ensureNotNegative(balance);
        Transaction transaction = Transaction.builder()
                .date(LocalDateTime.now(clock))
                .transactionType(type)
                .amount(signedAmount)
                .balance(balance)
                .account(account)
                .build();
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Transaction findById(Long id) {
        return getTransaction(id);
    }

    public Transaction update(Long id, TransactionType type, BigDecimal amount) {
        Transaction transaction = getTransaction(id);
        Account account = lockAccount(transaction.getAccount().getAccountNumber());
        ensureLatest(transaction, account);
        BigDecimal previousBalance = transaction.getBalance().subtract(transaction.getAmount());
        BigDecimal signedAmount = signedAmount(type, amount);
        BigDecimal balance = previousBalance.add(signedAmount);
        ensureNotNegative(balance);
        transaction.setTransactionType(type);
        transaction.setAmount(signedAmount);
        transaction.setBalance(balance);
        return transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        Transaction transaction = getTransaction(id);
        Account account = lockAccount(transaction.getAccount().getAccountNumber());
        ensureLatest(transaction, account);
        transactionRepository.deleteById(id);
    }

    private Transaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction " + id + " not found"));
    }

    private Account lockAccount(String accountNumber) {
        return accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account " + accountNumber + " not found"));
    }

    private BigDecimal currentBalance(Account account) {
        return transactionRepository.findLastByAccountId(account.getId())
                .map(Transaction::getBalance)
                .orElse(account.getInitialBalance());
    }

    private void ensureLatest(Transaction transaction, Account account) {
        boolean latest = transactionRepository.findLastByAccountId(account.getId())
                .map(last -> last.getId().equals(transaction.getId()))
                .orElse(false);
        if (!latest) {
            throw new BusinessRuleException("Only the latest transaction of an account can be changed");
        }
    }

    private static BigDecimal signedAmount(TransactionType type, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessRuleException("Amount must be greater than zero");
        }
        return type == TransactionType.WITHDRAWAL ? amount.negate() : amount;
    }

    private static void ensureActive(Account account) {
        if (!Boolean.TRUE.equals(account.getStatus())) {
            throw new BusinessRuleException("Account " + account.getAccountNumber() + " is inactive");
        }
    }

    private static void ensureNotNegative(BigDecimal balance) {
        if (balance.signum() < 0) {
            throw new InsufficientBalanceException();
        }
    }
}
