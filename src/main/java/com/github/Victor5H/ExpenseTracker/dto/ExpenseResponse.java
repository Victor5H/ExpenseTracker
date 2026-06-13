package com.github.Victor5H.ExpenseTracker.dto;

import com.github.Victor5H.ExpenseTracker.be.PaymentType;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {
    private Long id;
    private Double amount;
    private String information;
    private PaymentType paymentType;
    private List<String> tags;
    private LocalDate date;
}
