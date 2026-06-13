package com.github.Victor5H.ExpenseTracker.controller;

import com.github.Victor5H.ExpenseTracker.be.PaymentType;
import com.github.Victor5H.ExpenseTracker.repository.PaymentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/payment-types")
public class PaymentTypeController {

    @Autowired
    private PaymentTypeRepository paymentTypeRepository;

    @PostMapping
    public ResponseEntity<PaymentType> createPaymentType(@RequestBody PaymentType paymentType) {
        if (paymentType.getType() == null || paymentType.getType().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PaymentType type cannot be empty");
        }
        String trimmed = paymentType.getType().trim();
        if (paymentTypeRepository.findByType(trimmed).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "PaymentType already exists");
        }
        paymentType.setType(trimmed);
        PaymentType saved = paymentTypeRepository.save(paymentType);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public List<PaymentType> getAllPaymentTypes() {
        return paymentTypeRepository.findAll();
    }
}
