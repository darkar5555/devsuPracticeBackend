package com.devsu.bank.infrastructure.web.dto;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record DateRangeParam(LocalDate from, LocalDate to) {

    private static final String EXPECTED_FORMAT =
            "Parameter 'fecha' must be two ISO dates separated by a comma, for example 2022-02-01,2022-02-28";

    public static DateRangeParam parse(String value) {
        String[] parts = value == null ? new String[0] : value.split(",");
        if (parts.length != 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, EXPECTED_FORMAT);
        }
        try {
            return new DateRangeParam(LocalDate.parse(parts[0].trim()), LocalDate.parse(parts[1].trim()));
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, EXPECTED_FORMAT);
        }
    }
}
