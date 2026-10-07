package com.devsu.bank.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsu.bank.application.service.CustomerService;
import com.devsu.bank.domain.exception.ResourceNotFoundException;
import com.devsu.bank.domain.model.Customer;
import com.devsu.bank.domain.model.Gender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void patchSendsOnlyThePresentFields() throws Exception {
        Customer updated = Customer.builder()
                .id(1L)
                .name("Jose Lema")
                .gender(Gender.MALE)
                .age(35)
                .identification("1710000001")
                .address("Otavalo sn y principal")
                .phone("098254785")
                .status(false)
                .build();
        when(customerService.patch(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(patch("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jose Lema"))
                .andExpect(jsonPath("$.status").value(false));

        ArgumentCaptor<Customer> changes = ArgumentCaptor.forClass(Customer.class);
        verify(customerService).patch(eq(1L), changes.capture());
        assertEquals(Boolean.FALSE, changes.getValue().getStatus());
        assertNull(changes.getValue().getName());
    }

    @Test
    void unknownCustomerReturns404WithTheMessage() throws Exception {
        when(customerService.findById(99L)).thenThrow(new ResourceNotFoundException("Cliente 99 no encontrado"));

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Cliente 99 no encontrado"));
    }
}
