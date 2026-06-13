package com.github.Victor5H.ExpenseTracker.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {
    private Double amount;
    private String information;
    private Long paymentTypeId;
    private List<String> tags;
    private LocalDate date;
}
