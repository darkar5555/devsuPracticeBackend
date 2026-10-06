package com.devsu.bank.infrastructure.web;

import com.devsu.bank.application.report.AccountStatement;
import com.devsu.bank.application.service.AccountStatementService;
import com.devsu.bank.infrastructure.report.AccountStatementPdfRenderer;
import com.devsu.bank.infrastructure.web.dto.AccountStatementResponse;
import com.devsu.bank.infrastructure.web.dto.DateRangeParam;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportController {

    private final AccountStatementService statementService;
    private final AccountStatementPdfRenderer pdfRenderer;

    @GetMapping
    public AccountStatementResponse accountStatement(@RequestParam("fecha") String fecha,
                                                     @RequestParam("cliente") Long cliente) {
        DateRangeParam range = DateRangeParam.parse(fecha);
        AccountStatement statement = statementService.generate(cliente, range.from(), range.to());
        String pdfBase64 = Base64.getEncoder().encodeToString(pdfRenderer.render(statement));
        return AccountStatementResponse.from(statement, pdfBase64);
    }
}
