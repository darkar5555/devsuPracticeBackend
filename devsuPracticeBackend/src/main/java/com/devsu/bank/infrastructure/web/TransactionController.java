package com.devsu.bank.infrastructure.web;

import com.devsu.bank.application.service.TransactionService;
import com.devsu.bank.infrastructure.web.dto.TransactionRequest;
import com.devsu.bank.infrastructure.web.dto.TransactionResponse;
import com.devsu.bank.infrastructure.web.dto.TransactionUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionResponse> findAll() {
        return transactionService.findAll().stream().map(TransactionResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TransactionResponse findById(@PathVariable("id") Long id) {
        return TransactionResponse.from(transactionService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody TransactionRequest request) {
        return TransactionResponse.from(transactionService.create(
                request.accountNumber(), request.transactionType(), request.amount()));
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable("id") Long id,
                                      @Valid @RequestBody TransactionUpdateRequest request) {
        return TransactionResponse.from(transactionService.update(
                id, request.transactionType(), request.amount()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        transactionService.delete(id);
    }
}
