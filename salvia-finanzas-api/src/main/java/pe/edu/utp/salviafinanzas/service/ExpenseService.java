package pe.edu.utp.salviafinanzas.service;

import pe.edu.utp.salviafinanzas.dto.request.ExpenseRequest;
import pe.edu.utp.salviafinanzas.dto.response.ExpenseResponse;
import pe.edu.utp.salviafinanzas.dto.response.ExpenseSummaryResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseRequest request, String username);
    List<ExpenseResponse> getAllExpenses(String username);
    ExpenseResponse getExpenseById(Long id, String username);
    ExpenseResponse updateExpense(Long id, ExpenseRequest request, String username);
    void deleteExpense(Long id, String username);

    // Consultas personalizadas JPQL
    List<ExpenseResponse> getExpensesByCategory(String category, String username);
    List<ExpenseResponse> getExpensesByDateRange(LocalDateTime startDate, LocalDateTime endDate, String username);
    ExpenseSummaryResponse getCategorySummary(String category, String username);
    List<ExpenseResponse> getHighValueExpenses(BigDecimal minAmount, String username);
}
