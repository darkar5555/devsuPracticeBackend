package com.devsu.bank.application.service;

import com.devsu.bank.application.port.out.AccountRepository;
import com.devsu.bank.application.port.out.CustomerRepository;
import com.devsu.bank.application.port.out.TransactionRepository;
import com.devsu.bank.application.report.AccountStatement;
import com.devsu.bank.application.report.AccountStatement.AccountSummary;
import com.devsu.bank.domain.exception.BusinessRuleException;
import com.devsu.bank.domain.exception.ResourceNotFoundException;
import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.Customer;
import com.devsu.bank.domain.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountStatementService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountStatement generate(Long customerId, LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new BusinessRuleException("The start date must not be after the end date");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + customerId + " not found"));
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX).truncatedTo(ChronoUnit.MICROS);
        List<AccountSummary> accounts = accountRepository.findByCustomerId(customerId).stream()
                .map(account -> summarize(account, start, end))
                .toList();
        return new AccountStatement(customer, from, to, accounts);
    }

    private AccountSummary summarize(Account account, LocalDateTime start, LocalDateTime end) {
        List<Transaction> transactions =
                transactionRepository.findByAccountIdAndDateBetween(account.getId(), start, end);
        BigDecimal credits = BigDecimal.ZERO.setScale(2);
        BigDecimal debits = BigDecimal.ZERO.setScale(2);
        for (Transaction transaction : transactions) {
            BigDecimal amount = transaction.getAmount();
            if (amount.signum() >= 0) {
                credits = credits.add(amount);
            } else {
                debits = debits.add(amount.negate());
            }
        }
        BigDecimal currentBalance = transactionRepository.findLastByAccountId(account.getId())
                .map(Transaction::getBalance)
                .orElse(account.getInitialBalance());
        return new AccountSummary(account, currentBalance, credits, debits, transactions);
    }
}
