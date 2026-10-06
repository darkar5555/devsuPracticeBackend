package com.devsu.bank.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsu.bank.application.service.TransactionService;
import com.devsu.bank.domain.exception.InsufficientBalanceException;
import com.devsu.bank.domain.model.Account;
import com.devsu.bank.domain.model.Transaction;
import com.devsu.bank.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void createReturns201WithTheStoredTransaction() throws Exception {
        Account account = Account.builder().id(1L).accountNumber("478758").build();
        Transaction saved = Transaction.builder()
                .id(10L)
                .date(LocalDateTime.of(2026, 10, 5, 15, 0))
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("-575.00"))
                .balance(new BigDecimal("1425.00"))
                .account(account)
                .build();
        when(transactionService.create(eq("478758"), eq(TransactionType.WITHDRAWAL), any())).thenReturn(saved);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountNumber": "478758", "transactionType": "WITHDRAWAL", "amount": 575.00}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.amount").value(-575.00))
                .andExpect(jsonPath("$.balance").value(1425.00))
                .andExpect(jsonPath("$.accountNumber").value("478758"));
    }

    @Test
    void insufficientBalanceReturns422WithTheRequiredMessage() throws Exception {
        when(transactionService.create(any(), any(), any())).thenThrow(new InsufficientBalanceException());

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountNumber": "225487", "transactionType": "WITHDRAWAL", "amount": 5000.00}
                                """))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Saldo no disponible"))
                .andExpect(jsonPath("$.instance").value("/movimientos"));
    }

    @Test
    void invalidBodyReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountNumber": "", "transactionType": "DEPOSIT", "amount": 0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.errors.accountNumber").exists())
                .andExpect(jsonPath("$.errors.amount").exists());
    }
}
