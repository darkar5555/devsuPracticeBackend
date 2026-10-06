package com.devsu.bank.infrastructure.web;

import com.devsu.bank.application.service.AccountService;
import com.devsu.bank.infrastructure.web.dto.AccountRequest;
import com.devsu.bank.infrastructure.web.dto.AccountResponse;
import com.devsu.bank.infrastructure.web.dto.AccountUpdateRequest;
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
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountService.findAll().stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable("id") Long id) {
        return AccountResponse.from(accountService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        return AccountResponse.from(accountService.create(request.toEntity(), request.customerId()));
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable("id") Long id, @Valid @RequestBody AccountUpdateRequest request) {
        return AccountResponse.from(accountService.update(id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        accountService.delete(id);
    }
}
