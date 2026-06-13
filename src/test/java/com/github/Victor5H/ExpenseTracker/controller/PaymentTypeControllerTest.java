package com.github.Victor5H.ExpenseTracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.Victor5H.ExpenseTracker.be.PaymentType;
import com.github.Victor5H.ExpenseTracker.repository.ExpenseRepository;
import com.github.Victor5H.ExpenseTracker.repository.PaymentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentTypeRepository paymentTypeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PaymentType existingPaymentType;

    @BeforeEach
    public void setUp() {
        System.out.println("--- DEBUG START ---");
        System.out.println("BEFORE DELETE EXPENSES: " + expenseRepository.count());
        System.out.println("BEFORE DELETE PAYMENT TYPES: " + paymentTypeRepository.count());
        expenseRepository.deleteAll();
        paymentTypeRepository.deleteAll();
        System.out.println("AFTER DELETE EXPENSES: " + expenseRepository.count());
        System.out.println("AFTER DELETE PAYMENT TYPES: " + paymentTypeRepository.count());
        existingPaymentType = PaymentType.builder()
                .type("Credit Card")
                .build();
        existingPaymentType = paymentTypeRepository.save(existingPaymentType);
        paymentTypeRepository.flush();
        System.out.println("SAVED AND FLUSHED");
        System.out.println("--- DEBUG END ---");
    }

    @Test
    public void testCreatePaymentType_Success() throws Exception {
        PaymentType newPaymentType = PaymentType.builder()
                .type("Debit Card")
                .build();

        mockMvc.perform(post("/api/payment-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPaymentType)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.type", is("Debit Card")));
    }

    @Test
    public void testCreatePaymentType_ValidationError() throws Exception {
        PaymentType emptyPaymentType = PaymentType.builder()
                .type("")
                .build();

        mockMvc.perform(post("/api/payment-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyPaymentType)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreatePaymentType_Conflict() throws Exception {
        PaymentType duplicatePaymentType = PaymentType.builder()
                .type("Credit Card")
                .build();

        mockMvc.perform(post("/api/payment-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatePaymentType)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetAllPaymentTypes() throws Exception {
        mockMvc.perform(get("/api/payment-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is("Credit Card")));
    }

    @Test
    public void testGetPaymentTypeById_Success() throws Exception {
        mockMvc.perform(get("/api/payment-types/" + existingPaymentType.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(existingPaymentType.getId().intValue())))
                .andExpect(jsonPath("$.type", is("Credit Card")));
    }

    @Test
    public void testGetPaymentTypeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/payment-types/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdatePaymentType_Success() throws Exception {
        PaymentType updateDetails = PaymentType.builder()
                .type("Updated Card")
                .build();

        mockMvc.perform(put("/api/payment-types/" + existingPaymentType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(existingPaymentType.getId().intValue())))
                .andExpect(jsonPath("$.type", is("Updated Card")));
    }

    @Test
    public void testUpdatePaymentType_ValidationError() throws Exception {
        PaymentType updateDetails = PaymentType.builder()
                .type("   ")
                .build();

        mockMvc.perform(put("/api/payment-types/" + existingPaymentType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePaymentType_Conflict() throws Exception {
        PaymentType anotherType = PaymentType.builder()
                .type("Cash")
                .build();
        paymentTypeRepository.save(anotherType);

        PaymentType updateDetails = PaymentType.builder()
                .type("Cash")
                .build();

        mockMvc.perform(put("/api/payment-types/" + existingPaymentType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdatePaymentType_NotFound() throws Exception {
        PaymentType updateDetails = PaymentType.builder()
                .type("Cash")
                .build();

        mockMvc.perform(put("/api/payment-types/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePaymentType_Success() throws Exception {
        mockMvc.perform(delete("/api/payment-types/" + existingPaymentType.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeletePaymentType_NotFound() throws Exception {
        mockMvc.perform(delete("/api/payment-types/99999"))
                .andExpect(status().isNotFound());
    }
}
