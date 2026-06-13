package com.github.Victor5H.ExpenseTracker.service;

import com.github.Victor5H.ExpenseTracker.be.Expense;
import com.github.Victor5H.ExpenseTracker.be.PaymentType;
import com.github.Victor5H.ExpenseTracker.be.Tag;
import com.github.Victor5H.ExpenseTracker.dto.ExpenseRequest;
import com.github.Victor5H.ExpenseTracker.dto.ExpenseResponse;
import com.github.Victor5H.ExpenseTracker.repository.ExpenseRepository;
import com.github.Victor5H.ExpenseTracker.repository.PaymentTypeRepository;
import com.github.Victor5H.ExpenseTracker.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final TagRepository tagRepository;

    public ExpenseResponse createExpense(ExpenseRequest request) {
        PaymentType paymentType = null;
        Long paymentTypeId = request.getPaymentTypeId();
        if (paymentTypeId != null) {
            paymentType = paymentTypeRepository.findById(paymentTypeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentType not found"));
        }

        List<Tag> tags = new ArrayList<>();
        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                if (tagName == null || tagName.trim().isEmpty()) {
                    continue;
                }
                String trimmedName = tagName.trim();
                Tag tag = tagRepository.findByTag(trimmedName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().tag(trimmedName).build()));
                tags.add(tag);
            }
        }

        LocalDate expenseDate = request.getDate() != null ? request.getDate() : LocalDate.now();

        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .information(request.getInformation())
                .paymentType(paymentType)
                .tags(tags)
                .date(expenseDate)
                .build();

        Expense saved = expenseRepository.save(expense);
        return mapToResponse(saved);
    }

    public List<ExpenseResponse> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ExpenseResponse getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        return mapToResponse(expense);
    }

    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        PaymentType paymentType = null;
        if (request.getPaymentTypeId() != null) {
            paymentType = paymentTypeRepository.findById(request.getPaymentTypeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentType not found"));
        }

        List<Tag> tags = new ArrayList<>();
        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                if (tagName == null || tagName.trim().isEmpty()) {
                    continue;
                }
                String trimmedName = tagName.trim();
                Tag tag = tagRepository.findByTag(trimmedName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().tag(trimmedName).build()));
                tags.add(tag);
            }
        }

        expense.setAmount(request.getAmount());
        expense.setInformation(request.getInformation());
        expense.setPaymentType(paymentType);
        expense.setTags(tags);
        if (request.getDate() != null) {
            expense.setDate(request.getDate());
        }

        Expense updated = expenseRepository.save(expense);
        return mapToResponse(updated);
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");
        }
        expenseRepository.deleteById(id);
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        List<String> tagNames = expense.getTags() != null ?
                expense.getTags().stream().map(Tag::getTag).collect(Collectors.toList()) :
                new ArrayList<>();

        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .information(expense.getInformation())
                .paymentType(expense.getPaymentType())
                .tags(tagNames)
                .date(expense.getDate())
                .build();
    }
}
