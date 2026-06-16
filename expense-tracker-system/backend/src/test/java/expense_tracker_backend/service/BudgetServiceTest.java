package expense_tracker_backend.service;

import expense_tracker_backend.model.Budget;
import expense_tracker_backend.model.Category;
import expense_tracker_backend.model.Expense;
import expense_tracker_backend.repository.BudgetRepository;
import expense_tracker_backend.repository.CategoryRepository;
import expense_tracker_backend.repository.ExpenseRepository;
import expense_tracker_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @InjectMocks
    private BudgetService budgetService;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void getOverBudgetAlertMessageReturnsNullWhenNothingIsExceeded() {
        Budget budget = new Budget();
        budget.setId(1L);
        budget.setUserId(4L);
        budget.setCategoryId(10L);
        budget.setMonth("2026-05");
        budget.setMonthlyLimit(3000.0);

        Expense expense = new Expense();
        expense.setUserId(4L);
        expense.setAmount(2500.0);
        expense.setDate(LocalDate.of(2026, 5, 10));
        expense.setCategory(new Category(10L, "Food"));

        when(budgetRepository.findByUserIdAndMonth(4L, "2026-05"))
                .thenReturn(List.of(budget));
        when(expenseRepository.findByUserId(4L)).thenReturn(List.of(expense));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(new Category(10L, "Food")));

        String result = budgetService.getOverBudgetAlertMessage(4L, "2026-05");

        assertNull(result);
    }

    @Test
    void getOverBudgetAlertMessageBuildsReadableAlertForExceededBudgets() {
        Budget foodBudget = new Budget();
        foodBudget.setId(1L);
        foodBudget.setUserId(4L);
        foodBudget.setCategoryId(10L);
        foodBudget.setMonth("2026-05");
        foodBudget.setMonthlyLimit(3000.0);

        Budget shoppingBudget = new Budget();
        shoppingBudget.setId(2L);
        shoppingBudget.setUserId(4L);
        shoppingBudget.setCategoryId(11L);
        shoppingBudget.setMonth("2026-05");
        shoppingBudget.setMonthlyLimit(2500.0);

        Expense foodExpense = new Expense();
        foodExpense.setUserId(4L);
        foodExpense.setAmount(3700.0);
        foodExpense.setDate(LocalDate.of(2026, 5, 10));
        foodExpense.setCategory(new Category(10L, "Food"));

        Expense shoppingExpense = new Expense();
        shoppingExpense.setUserId(4L);
        shoppingExpense.setAmount(3100.5);
        shoppingExpense.setDate(LocalDate.of(2026, 5, 12));
        shoppingExpense.setCategory(new Category(11L, "Shopping"));

        when(budgetRepository.findByUserIdAndMonth(4L, "2026-05"))
                .thenReturn(List.of(foodBudget, shoppingBudget));
        when(expenseRepository.findByUserId(4L))
                .thenReturn(List.of(foodExpense, shoppingExpense));
        when(categoryRepository.findById(10L))
                .thenReturn(Optional.of(new Category(10L, "Food")));
        when(categoryRepository.findById(11L))
                .thenReturn(Optional.of(new Category(11L, "Shopping")));

        String result = budgetService.getOverBudgetAlertMessage(4L, "2026-05");

        assertEquals(
                "Budget alert: You have exceeded your budget for Food by \u20B9700.00, Shopping by \u20B9600.50.",
                result
        );
    }
}
