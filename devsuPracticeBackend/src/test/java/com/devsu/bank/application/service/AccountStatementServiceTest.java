package com.devsu.bank.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.devsu.bank.application.port.out.AccountRepository;
import com.devsu.bank.application.port.out.CustomerRepository;
import com.devsu.bank.application.port.out.TransactionRepository;
import com.devsu.bank.application.report.AccountStatement;
import com.devsu.bank.application.report.AccountStatement.AccountSummary;
import com.devsu.bank.domain.exception.BusinessRuleException;
import com.devsu.bank.domain.exception.ResourceNotFoundException;
import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.AccountType;
import com.devsu.bank.domain.model.Customer;
import com.devsu.bank.domain.model.Transaction;
import com.devsu.bank.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountStatementServiceTest {

    private static final LocalDate FROM = LocalDate.of(2022, 2, 1);
    private static final LocalDate TO = LocalDate.of(2022, 2, 28);

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private AccountStatementService service;
    private Customer customer;

    @BeforeEach
    void setUp() {
        service = new AccountStatementService(customerRepository, accountRepository, transactionRepository);
        customer = Customer.builder().id(2L).name("Marianela Montalvo").identification("1710000002").build();
    }

    @Test
    void summarizesEachAccountWithTotalsAndCurrentBalance() {
        Account checking = Account.builder().id(2L).accountNumber("225487").accountType(AccountType.CHECKING)
                .initialBalance(new BigDecimal("100.00")).status(true).build();
        Account savings = Account.builder().id(4L).accountNumber("496825").accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("540.00")).status(true).build();
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByCustomerId(2L)).thenReturn(List.of(checking, savings));
        Transaction deposit = transaction(2L, TransactionType.DEPOSIT, "600.00", "700.00");
        Transaction withdrawal = transaction(4L, TransactionType.WITHDRAWAL, "-540.00", "0.00");
        when(transactionRepository.findByAccountIdAndDateBetween(eq(2L), any(), any())).thenReturn(List.of(deposit));
        when(transactionRepository.findByAccountIdAndDateBetween(eq(4L), any(), any())).thenReturn(List.of(withdrawal));
        when(transactionRepository.findLastByAccountId(2L)).thenReturn(Optional.of(deposit));
        when(transactionRepository.findLastByAccountId(4L)).thenReturn(Optional.of(withdrawal));

        AccountStatement statement = service.generate(2L, FROM, TO);

        assertEquals(customer, statement.customer());
        assertEquals(2, statement.accounts().size());
        AccountSummary first = statement.accounts().get(0);
        assertMoney("600.00", first.totalCredits());
        assertMoney("0.00", first.totalDebits());
        assertMoney("700.00", first.currentBalance());
        AccountSummary second = statement.accounts().get(1);
        assertMoney("0.00", second.totalCredits());
        assertMoney("540.00", second.totalDebits());
        assertMoney("0.00", second.currentBalance());
    }

    @Test
    void accountWithoutTransactionsReportsInitialBalanceAndZeroTotals() {
        Account account = Account.builder().id(3L).accountNumber("495878").accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("0.00")).status(true).build();
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByCustomerId(2L)).thenReturn(List.of(account));
        when(transactionRepository.findByAccountIdAndDateBetween(eq(3L), any(), any())).thenReturn(List.of());
        when(transactionRepository.findLastByAccountId(3L)).thenReturn(Optional.empty());

        AccountSummary summary = service.generate(2L, FROM, TO).accounts().get(0);

        assertTrue(summary.transactions().isEmpty());
        assertMoney("0.00", summary.totalCredits());
        assertMoney("0.00", summary.totalDebits());
        assertMoney("0.00", summary.currentBalance());
    }

    @Test
    void rejectsReversedDateRange() {
        assertThrows(BusinessRuleException.class, () -> service.generate(2L, TO, FROM));
    }

    @Test
    void unknownCustomerIsNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.generate(99L, FROM, TO));
    }

    private static Transaction transaction(Long accountId, TransactionType type, String amount, String balance) {
        return Transaction.builder()
                .id(accountId * 10)
                .date(LocalDateTime.of(2022, 2, 10, 10, 0))
                .transactionType(type)
                .amount(new BigDecimal(amount))
                .balance(new BigDecimal(balance))
                .account(Account.builder().id(accountId).build())
                .build();
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual), "expected " + expected + " but was " + actual);
    }
}
