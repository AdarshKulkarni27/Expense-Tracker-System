package expense_tracker_backend.controller;

import expense_tracker_backend.dto.DashboardSummaryDTO;
import expense_tracker_backend.dto.MonthlyExpenseDTO;
import expense_tracker_backend.model.Expense;
import expense_tracker_backend.repository.CategoryRepository;
import expense_tracker_backend.repository.ExpenseRepository;
import expense_tracker_backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import expense_tracker_backend.dto.CategoryExpenseDTO;
import expense_tracker_backend.dto.TopExpenseDTO;

import java.util.*;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
public class DashboardController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private DashboardService dashboardService;

    // ✅ Dashboard summary (basic)
    @GetMapping("/{userId}")
    public DashboardSummaryDTO getDashboard(
            @PathVariable Long userId) {

        return dashboardService.getDashboardSummary(userId);
    }

    // ✅ Monthly summary (CORRECT way - DB aggregation)
    @GetMapping("/monthly-summary/{userId}")
    public List<MonthlyExpenseDTO> getMonthlySummary(@PathVariable Long userId) {
        return dashboardService.getMonthlySummary(userId);
    }

    // ✅ Category summary (CORRECT way - DB aggregation)
    @GetMapping("/category-summary/{userId}")
    public List<CategoryExpenseDTO> getCategorySummary(@PathVariable Long userId) {
        return dashboardService.getCategorySummary(userId);
    }

    @GetMapping("/top-expenses/{userId}")
    public List<TopExpenseDTO> getTopExpenses(@PathVariable Long userId) {
        return dashboardService.getTopExpenses(userId);
    }
}