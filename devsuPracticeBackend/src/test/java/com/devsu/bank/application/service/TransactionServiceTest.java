package com.devsu.bank.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devsu.bank.application.port.out.AccountRepository;
import com.devsu.bank.application.port.out.TransactionRepository;
import com.devsu.bank.domain.exception.BusinessRuleException;
import com.devsu.bank.domain.exception.InsufficientBalanceException;
import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.AccountType;
import com.devsu.bank.domain.model.Transaction;
import com.devsu.bank.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-05T15:00:00Z"), ZoneOffset.UTC);

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService service;
    private Account account;

    @BeforeEach
    void setUp() {
        service = new TransactionService(accountRepository, transactionRepository, FIXED_CLOCK);
        account = Account.builder()
                .id(1L)
                .accountNumber("478758")
                .accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("2000.00"))
                .status(true)
                .build();
        when(accountRepository.findByAccountNumberForUpdate("478758")).thenReturn(Optional.of(account));
    }

    @Test
    void depositAddsToTheCurrentBalance() {
        lastBalanceIs("1425.00");
        saveReturnsArgument();

        Transaction result = service.create("478758", TransactionType.DEPOSIT, new BigDecimal("600.00"));

        assertMoney("600.00", result.getAmount());
        assertMoney("2025.00", result.getBalance());
        assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
        assertEquals(LocalDateTime.of(2026, 10, 5, 15, 0), result.getDate());
        assertEquals(account, result.getAccount());
    }

    @Test
    void withdrawalIsStoredWithNegativeAmount() {
        lastBalanceIs("1425.00");
        saveReturnsArgument();

        Transaction result = service.create("478758", TransactionType.WITHDRAWAL, new BigDecimal("575.00"));

        assertMoney("-575.00", result.getAmount());
        assertMoney("850.00", result.getBalance());
    }

    @Test
    void withdrawalAboveBalanceIsRejected() {
        lastBalanceIs("100.00");

        InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class,
                () -> service.create("478758", TransactionType.WITHDRAWAL, new BigDecimal("150.00")));

        assertEquals("Saldo no disponible", ex.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void usesInitialBalanceWhenAccountHasNoTransactions() {
        when(transactionRepository.findLastByAccountId(1L)).thenReturn(Optional.empty());
        saveReturnsArgument();

        Transaction result = service.create("478758", TransactionType.DEPOSIT, new BigDecimal("150.00"));

        assertMoney("2150.00", result.getBalance());
    }

    @Test
    void inactiveAccountCannotTransact() {
        account.setStatus(false);

        assertThrows(BusinessRuleException.class,
                () -> service.create("478758", TransactionType.DEPOSIT, new BigDecimal("10.00")));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void amountMustBePositive() {
        assertThrows(BusinessRuleException.class,
                () -> service.create("478758", TransactionType.DEPOSIT, BigDecimal.ZERO));
        assertThrows(BusinessRuleException.class,
                () -> service.create("478758", TransactionType.WITHDRAWAL, new BigDecimal("-5.00")));
    }

    private void lastBalanceIs(String balance) {
        Transaction last = Transaction.builder().id(9L).balance(new BigDecimal(balance)).account(account).build();
        when(transactionRepository.findLastByAccountId(1L)).thenReturn(Optional.of(last));
    }

    private void saveReturnsArgument() {
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual), "expected " + expected + " but was " + actual);
    }
}
