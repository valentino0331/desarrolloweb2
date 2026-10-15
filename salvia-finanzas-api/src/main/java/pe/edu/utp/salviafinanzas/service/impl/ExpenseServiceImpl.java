package pe.edu.utp.salviafinanzas.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.salviafinanzas.dto.request.ExpenseRequest;
import pe.edu.utp.salviafinanzas.dto.response.ExpenseResponse;
import pe.edu.utp.salviafinanzas.dto.response.ExpenseSummaryResponse;
import pe.edu.utp.salviafinanzas.exception.ResourceNotFoundException;
import pe.edu.utp.salviafinanzas.model.Category;
import pe.edu.utp.salviafinanzas.model.Expense;
import pe.edu.utp.salviafinanzas.model.User;
import pe.edu.utp.salviafinanzas.repository.CategoryRepository;
import pe.edu.utp.salviafinanzas.repository.ExpenseRepository;
import pe.edu.utp.salviafinanzas.repository.UserRepository;
import pe.edu.utp.salviafinanzas.service.ExpenseService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la Lógica de Negocio de Gastos
 * Incorpora métodos CRUD, operaciones transaccionales (@Transactional) y consultas JPQL.
 */
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpenseResponse createExpense(ExpenseRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                    .orElse(null);
        }

        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .categoryName(request.getCategoryName())
                .subcategory(request.getSubcategory())
                .description(request.getDescription())
                .expenseDate(request.getExpenseDate() != null ? request.getExpenseDate() : LocalDateTime.now())
                .recurrence(request.getRecurrence() != null ? request.getRecurrence() : "ONCE")
                .category(category)
                .user(user)
                .build();

        // Operación transaccional complementaria: Bonificación de puntos de gamificación (5 XP por registro)
        user.setPoints(user.getPoints() + 5);
        userRepository.save(user);

        Expense saved = expenseRepository.save(expense);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAllExpenses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return expenseRepository.findByUserIdOrderByExpenseDateDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Expense expense = expenseRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Gasto", "id", id));

        return mapToResponse(expense);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Expense expense = expenseRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Gasto", "id", id));

        expense.setAmount(request.getAmount());
        expense.setCategoryName(request.getCategoryName());
        expense.setSubcategory(request.getSubcategory());
        expense.setDescription(request.getDescription());
        if (request.getExpenseDate() != null) {
            expense.setExpenseDate(request.getExpenseDate());
        }
        if (request.getRecurrence() != null) {
            expense.setRecurrence(request.getRecurrence());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                    .orElse(null);
            expense.setCategory(category);
        }

        Expense updated = expenseRepository.save(expense);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpense(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Expense expense = expenseRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Gasto", "id", id));

        expenseRepository.delete(expense);
    }

    // ==================== MÉTODOS CON CONSULTAS JPQL ====================

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByCategory(String category, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return expenseRepository.findExpensesByUserAndCategory(user.getId(), category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByDateRange(LocalDateTime startDate, LocalDateTime endDate, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return expenseRepository.findExpensesByDateRange(user.getId(), startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseSummaryResponse getCategorySummary(String category, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        BigDecimal total = expenseRepository.calculateTotalExpenseByUserAndCategory(user.getId(), category);
        List<Expense> expenses = expenseRepository.findExpensesByUserAndCategory(user.getId(), category);

        return ExpenseSummaryResponse.builder()
                .category(category)
                .totalAmount(total != null ? total : BigDecimal.ZERO)
                .transactionCount((long) expenses.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getHighValueExpenses(BigDecimal minAmount, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return expenseRepository.findHighValueExpenses(user.getId(), minAmount)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .categoryName(expense.getCategoryName())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .subcategory(expense.getSubcategory())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .recurrence(expense.getRecurrence())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}
