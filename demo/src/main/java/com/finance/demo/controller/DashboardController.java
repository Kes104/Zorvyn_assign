package com.finance.demo.controller;

import com.finance.demo.dto.DashboardSummaryResponse;
import com.finance.demo.dto.TrendPoint;
import com.finance.demo.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

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
    public DashboardSummaryResponse summary() {
        return dashboardService.getSummary(null, null, null);
    }

    @GetMapping("/trends")
    public List<TrendPoint> trends() {
        return dashboardService.getMonthlyTrends(6, null);
    }
}
