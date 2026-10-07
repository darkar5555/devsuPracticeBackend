package com.devsu.bank.application.service;

import com.devsu.bank.application.port.out.AccountRepository;
import com.devsu.bank.application.port.out.TransactionRepository;
import com.devsu.bank.domain.exception.BusinessRuleException;
import com.devsu.bank.domain.exception.DailyLimitExceededException;
import com.devsu.bank.domain.exception.InsufficientBalanceException;
import com.devsu.bank.domain.exception.ResourceNotFoundException;
import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.DailyWithdrawalLimit;
import com.devsu.bank.domain.model.Transaction;
import com.devsu.bank.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final DailyWithdrawalLimit dailyWithdrawalLimit;

    public Transaction create(String accountNumber, TransactionType type, BigDecimal amount) {
        Account account = lockAccount(accountNumber);
        ensureActive(account);
        BigDecimal signedAmount = signedAmount(type, amount);
        BigDecimal balance = currentBalance(account).add(signedAmount);
        ensureNotNegative(balance);
        LocalDateTime now = LocalDateTime.now(clock);
        if (type == TransactionType.WITHDRAWAL) {
            ensureDailyLimit(account, amount, now.toLocalDate(), null);
        }
        Transaction transaction = Transaction.builder()
                .date(now)
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
        if (type == TransactionType.WITHDRAWAL) {
            ensureDailyLimit(account, amount, transaction.getDate().toLocalDate(), transaction.getId());
        }
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
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento " + id + " no encontrado"));
    }

    private Account lockAccount(String accountNumber) {
        return accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta " + accountNumber + " no encontrada"));
    }

    private BigDecimal currentBalance(Account account) {
        return transactionRepository.findLastByAccountId(account.getId())
                .map(Transaction::getBalance)
                .orElse(account.getInitialBalance());
    }

    // Limit for withdrawals
    private void ensureDailyLimit(Account account, BigDecimal amount, LocalDate day, Long excludedId) {
        BigDecimal withdrawnThatDay = transactionRepository
                .findByAccountIdAndDateBetween(account.getId(), day.atStartOfDay(), day.atTime(LocalTime.MAX))
                .stream()
                .filter(t -> t.getTransactionType() == TransactionType.WITHDRAWAL)
                .filter(t -> !t.getId().equals(excludedId))
                .map(Transaction::getAmount)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (dailyWithdrawalLimit.isExceededBy(withdrawnThatDay.add(amount))) {
            throw new DailyLimitExceededException();
        }
    }

    private void ensureLatest(Transaction transaction, Account account) {
        boolean latest = transactionRepository.findLastByAccountId(account.getId())
                .map(last -> last.getId().equals(transaction.getId()))
                .orElse(false);
        if (!latest) {
            throw new BusinessRuleException("Solo se puede modificar el último movimiento de la cuenta");
        }
    }

    private static BigDecimal signedAmount(TransactionType type, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessRuleException("El valor debe ser mayor que cero");
        }
        return type == TransactionType.WITHDRAWAL ? amount.negate() : amount;
    }

    private static void ensureActive(Account account) {
        if (!Boolean.TRUE.equals(account.getStatus())) {
            throw new BusinessRuleException("La cuenta " + account.getAccountNumber() + " está inactiva");
        }
    }

    private static void ensureNotNegative(BigDecimal balance) {
        if (balance.signum() < 0) {
            throw new InsufficientBalanceException();
        }
    }
}
