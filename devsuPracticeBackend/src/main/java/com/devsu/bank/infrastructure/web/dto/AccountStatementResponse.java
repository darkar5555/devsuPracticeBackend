package com.devsu.bank.infrastructure.web.dto;

import com.devsu.bank.application.report.AccountStatement;
import com.devsu.bank.domain.model.AccountType;
import com.devsu.bank.domain.model.Transaction;
import com.devsu.bank.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AccountStatementResponse(
        CustomerSummary customer,
        LocalDate from,
        LocalDate to,
        List<AccountDetail> accounts,
        String pdfBase64) {

    public record CustomerSummary(Long id, String name, String identification) {
    }

    public record AccountDetail(
            String accountNumber,
            AccountType accountType,
            BigDecimal initialBalance,
            Boolean status,
            BigDecimal currentBalance,
            BigDecimal totalCredits,
            BigDecimal totalDebits,
            List<Movement> transactions) {
    }

    public record Movement(LocalDateTime date, TransactionType transactionType, BigDecimal amount, BigDecimal balance) {

        static Movement from(Transaction transaction) {
            return new Movement(transaction.getDate(), transaction.getTransactionType(),
                    transaction.getAmount(), transaction.getBalance());
        }
    }

    public static AccountStatementResponse from(AccountStatement statement, String pdfBase64) {
        CustomerSummary customer = new CustomerSummary(
                statement.customer().getId(),
                statement.customer().getName(),
                statement.customer().getIdentification());
        List<AccountDetail> accounts = statement.accounts().stream()
                .map(summary -> new AccountDetail(
                        summary.account().getAccountNumber(),
                        summary.account().getAccountType(),
                        summary.account().getInitialBalance(),
                        summary.account().getStatus(),
                        summary.currentBalance(),
                        summary.totalCredits(),
                        summary.totalDebits(),
                        summary.transactions().stream().map(Movement::from).toList()))
                .toList();
        return new AccountStatementResponse(customer, statement.from(), statement.to(), accounts, pdfBase64);
    }
}
