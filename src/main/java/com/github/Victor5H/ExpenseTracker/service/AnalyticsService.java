package com.github.Victor5H.ExpenseTracker.service;

import com.github.Victor5H.ExpenseTracker.be.Expense;
import com.github.Victor5H.ExpenseTracker.be.Tag;
import com.github.Victor5H.ExpenseTracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Map<String, Map<String, Double>> getSpendAnalytics(String timeframe, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Expense> expenses = expenseRepository.findByDateRange(startDate, endDate);

        Map<String, Map<String, Double>> analytics = new HashMap<>();

        for (Expense expense : expenses) {
            String period = getPeriodKey(timeframe, expense.getDate());
            Double amount = expense.getAmount() != null ? expense.getAmount() : 0.0;

            List<Tag> tags = expense.getTags();
            if (tags == null || tags.isEmpty()) {
                addToAnalytics(analytics, "Untagged", period, amount);
            } else {
                for (Tag tag : tags) {
                    addToAnalytics(analytics, tag.getTag(), period, amount);
                }
            }
        }

        return analytics;
    }

    private String getPeriodKey(String timeframe, LocalDate date) {
        if ("quarterly".equalsIgnoreCase(timeframe)) {
            int quarter = (date.getMonthValue() - 1) / 3 + 1;
            return date.getYear() + "-Q" + quarter;
        } else if ("yearly".equalsIgnoreCase(timeframe)) {
            return String.valueOf(date.getYear());
        } else {
            return String.format("%d-%02d", date.getYear(), date.getMonthValue());
        }
    }

    private void addToAnalytics(Map<String, Map<String, Double>> analytics, String tag, String period, Double amount) {
        analytics.putIfAbsent(tag, new HashMap<>());
        Map<String, Double> periodMap = analytics.get(tag);
        periodMap.put(period, periodMap.getOrDefault(period, 0.0) + amount);
    }
}
