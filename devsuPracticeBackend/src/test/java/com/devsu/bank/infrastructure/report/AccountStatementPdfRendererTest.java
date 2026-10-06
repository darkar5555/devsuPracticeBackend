package com.devsu.bank.infrastructure.report;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.devsu.bank.application.report.AccountStatement;
import com.devsu.bank.application.report.AccountStatement.AccountSummary;
import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.AccountType;
import com.devsu.bank.domain.model.Customer;
import com.devsu.bank.domain.model.Transaction;
import com.devsu.bank.domain.model.TransactionType;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccountStatementPdfRendererTest {

    @Test
    void rendersAValidPdfDocument() {
        Customer customer = Customer.builder().id(2L).name("Marianela Montalvo").identification("1710000002").build();
        Account account = Account.builder().id(2L).accountNumber("225487").accountType(AccountType.CHECKING)
                .initialBalance(new BigDecimal("100.00")).status(true).build();
        Transaction deposit = Transaction.builder().id(2L).date(LocalDateTime.of(2022, 2, 10, 11, 0))
                .transactionType(TransactionType.DEPOSIT).amount(new BigDecimal("600.00"))
                .balance(new BigDecimal("700.00")).account(account).build();
        AccountStatement statement = new AccountStatement(customer,
                LocalDate.of(2022, 2, 1), LocalDate.of(2022, 2, 28),
                List.of(new AccountSummary(account, new BigDecimal("700.00"), new BigDecimal("600.00"),
                        BigDecimal.ZERO, List.of(deposit))));

        byte[] pdf = new AccountStatementPdfRenderer().render(statement);

        String header = new String(pdf, 0, 5, StandardCharsets.US_ASCII);
        assertTrue(header.startsWith("%PDF-"), "not a PDF: " + header);
        assertTrue(pdf.length > 500, "suspiciously small PDF: " + pdf.length + " bytes");
    }
}
