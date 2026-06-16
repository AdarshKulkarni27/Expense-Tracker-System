package expense_tracker_backend.service;

import expense_tracker_backend.dto.DashboardSummaryDTO;
import expense_tracker_backend.dto.MonthlyExpenseDTO;
import expense_tracker_backend.repository.ExpenseRepository;
import expense_tracker_backend.dto.CategoryExpenseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import expense_tracker_backend.dto.TopExpenseDTO;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<MonthlyExpenseDTO> getMonthlySummary(Long userId) {

        List<Object[]> results = expenseRepository.getMonthlyExpensesByUser(userId);

        return results.stream()
                .map(obj -> new MonthlyExpenseDTO(
                        (String) obj[0],
                        ((Number) obj[1]).doubleValue()
                ))
                .toList();
    }

    public List<CategoryExpenseDTO> getCategorySummary(Long userId) {

        List<Object[]> results = expenseRepository.getCategorySummary(userId);

        return results.stream()
                .map(obj -> new CategoryExpenseDTO(
                        (String) obj[0],
                        ((Number) obj[1]).doubleValue()
                ))
                .toList();
    }

    public List<TopExpenseDTO> getTopExpenses(Long userId) {

        return expenseRepository.getTopExpenses(userId)
                .stream()
                .map(obj -> new TopExpenseDTO(
                        (String) obj[2],            // title
                        (String) obj[3],            // category
                        obj[1].toString(),          // date
                        ((Number) obj[0]).doubleValue() // amount
                ))
                .toList();
    }

    public DashboardSummaryDTO getDashboardSummary(Long userId) {

        Double totalSpent =
                expenseRepository.getTotalSpent(userId);

        Double averageSpent =
                Math.round(
                        expenseRepository.getAverageSpent(userId) * 100.0
                ) / 100.0;

        Long transactionCount =
                expenseRepository.getTransactionCount(userId);

        List<Object[]> topCategoryData =
                expenseRepository.getTopCategory(userId);

        String topCategory = "N/A";
        Double topCategoryAmount = 0.0;

        if (!topCategoryData.isEmpty()) {

            topCategory =
                    topCategoryData.get(0)[0].toString();

            topCategoryAmount =
                    ((Number) topCategoryData.get(0)[1]).doubleValue();
        }

        return new DashboardSummaryDTO(
                totalSpent,
                averageSpent,
                topCategory,
                topCategoryAmount,
                transactionCount
        );
    }
}