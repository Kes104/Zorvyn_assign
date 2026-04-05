package com.finance.demo.controller;

import com.finance.demo.dto.DashboardSummaryResponse;
import com.finance.demo.dto.TrendPoint;
import com.finance.demo.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Integer createdBy
    ) {
        return dashboardService.getSummary(start, end, createdBy);
    }

    @GetMapping("/trends")
    public List<TrendPoint> trends(
            @RequestParam(defaultValue = "6") int months,
            @RequestParam(required = false) Integer createdBy
    ) {
        return dashboardService.getMonthlyTrends(months, createdBy);
    }
}
