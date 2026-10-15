package pe.edu.utp.salviafinanzas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.salviafinanzas.dto.request.ExpenseRequest;
import pe.edu.utp.salviafinanzas.dto.response.ApiResponse;
import pe.edu.utp.salviafinanzas.dto.response.ExpenseResponse;
import pe.edu.utp.salviafinanzas.dto.response.ExpenseSummaryResponse;
import pe.edu.utp.salviafinanzas.service.ExpenseService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la Gestión de Gastos y Transacciones Financieras
 * Protegido mediante Spring Security con anotaciones @PreAuthorize a nivel de endpoint.
 */
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseResponse response = expenseService.createExpense(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ExpenseResponse> list = expenseService.getAllExpenses(userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseResponse response = expenseService.getExpenseById(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseResponse response = expenseService.updateExpense(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteExpense(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        expenseService.deleteExpense(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "Gasto eliminado correctamente con id: " + id));
    }

    // ==================== ENDPOINTS CON FILTROS JPQL ====================

    @GetMapping("/filter/category")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByCategory(
            @RequestParam String category,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ExpenseResponse> list = expenseService.getExpensesByCategory(category, userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/filter/date-range")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ExpenseResponse> list = expenseService.getExpensesByDateRange(start, end, userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/filter/high-value")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ExpenseResponse>> getHighValueExpenses(
            @RequestParam BigDecimal minAmount,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ExpenseResponse> list = expenseService.getHighValueExpenses(minAmount, userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/summary/category")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ExpenseSummaryResponse> getCategorySummary(
            @RequestParam String category,
            @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseSummaryResponse summary = expenseService.getCategorySummary(category, userDetails.getUsername());
        return ResponseEntity.ok(summary);
    }
}
