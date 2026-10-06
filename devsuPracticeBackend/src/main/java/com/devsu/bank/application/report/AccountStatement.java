package com.devsu.bank.application.report;

import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.Customer;
import com.devsu.bank.domain.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AccountStatement(
        Customer customer,
        LocalDate from,
        LocalDate to,
        List<AccountSummary> accounts) {

    public record AccountSummary(
            Account account,
            BigDecimal currentBalance,
            BigDecimal totalCredits,
            BigDecimal totalDebits,
            List<Transaction> transactions) {
    }
}
