package com.github.Victor5H.ExpenseTracker.controller;

import com.github.Victor5H.ExpenseTracker.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/spend")
    public Map<String, Map<String, Double>> getSpendAnalytics(
            @RequestParam(name = "timeframe", defaultValue = "monthly") String timeframe,
            @RequestParam(name = "year", required = false) Integer year) {
        
        int targetYear = year != null ? year : LocalDate.now().getYear();
        return analyticsService.getSpendAnalytics(timeframe, targetYear);
    }
}
