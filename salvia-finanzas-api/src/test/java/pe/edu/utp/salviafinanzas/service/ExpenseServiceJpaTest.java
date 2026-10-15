package pe.edu.utp.salviafinanzas.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.salviafinanzas.dto.request.ExpenseRequest;
import pe.edu.utp.salviafinanzas.dto.response.ExpenseResponse;
import pe.edu.utp.salviafinanzas.dto.response.ExpenseSummaryResponse;
import pe.edu.utp.salviafinanzas.dto.response.UserResponse;
import pe.edu.utp.salviafinanzas.model.Expense;
import pe.edu.utp.salviafinanzas.repository.ExpenseRepository;
import pe.edu.utp.salviafinanzas.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de Persistencia JPA y Consultas JPQL para Gastos (Consignas 1 y 2)
 */
@SpringBootTest
@Transactional
class ExpenseServiceJpaTest {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("CP-EXP-01: Creación de gasto persiste correctamente en base de datos H2")
    void testCreateExpensePersistence() {
        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("35.50"))
                .categoryName("Comida")
                .subcategory("Cena")
                .description("Cena de celebración académica")
                .expenseDate(LocalDateTime.now())
                .recurrence("ONCE")
                .build();

        ExpenseResponse created = expenseService.createExpense(request, "valentino");

        assertNotNull(created.getId());
        assertEquals(new BigDecimal("35.50"), created.getAmount());
        assertEquals("Comida", created.getCategoryName());

        // Verificar que existe en el repositorio JPA
        assertTrue(expenseRepository.findById(created.getId()).isPresent());
    }

    @Test
    @DisplayName("CP-EXP-02: Operación transaccional otorga puntos de gamificación al usuario")
    void testTransactionalGamificationPointsAwarded() {
        UserResponse userBefore = authService.getCurrentUser("valentino");
        int pointsBefore = userBefore.getPoints();

        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("22.00"))
                .categoryName("Transporte")
                .subcategory("Taxi")
                .description("Traslado nocturno seguro")
                .build();

        expenseService.createExpense(request, "valentino");

        UserResponse userAfter = authService.getCurrentUser("valentino");
        assertEquals(pointsBefore + 5, userAfter.getPoints(), "El registro de gasto debe sumar 5 XP de forma atómica");
    }

    @Test
    @DisplayName("CP-EXP-03: Consulta JPQL 1 filtra transacciones por categoría")
    void testJpqlFilterByCategory() {
        List<ExpenseResponse> foodExpenses = expenseService.getExpensesByCategory("Comida", "valentino");

        assertFalse(foodExpenses.isEmpty());
        for (ExpenseResponse exp : foodExpenses) {
            assertEquals("Comida", exp.getCategoryName());
        }
    }

    @Test
    @DisplayName("CP-EXP-04: Consulta JPQL 2 filtra transacciones por intervalo de fechas")
    void testJpqlFilterByDateRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        List<ExpenseResponse> expensesInRange = expenseService.getExpensesByDateRange(start, end, "valentino");

        assertFalse(expensesInRange.isEmpty());
        for (ExpenseResponse exp : expensesInRange) {
            assertTrue(!exp.getExpenseDate().isBefore(start) && !exp.getExpenseDate().isAfter(end));
        }
    }

    @Test
    @DisplayName("CP-EXP-05: Consulta JPQL 3 calcula el total acumulado desembolsado por categoría")
    void testJpqlCalculateTotalExpenseByCategory() {
        ExpenseSummaryResponse summary = expenseService.getCategorySummary("Universidad", "valentino");

        assertNotNull(summary);
        assertEquals("Universidad", summary.getCategory());
        assertTrue(summary.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(summary.getTransactionCount() >= 1);
    }

    @Test
    @DisplayName("CP-EXP-06: Consulta JPQL 4 recupera gastos de alto valor superiores a umbral")
    void testJpqlHighValueExpenses() {
        BigDecimal threshold = new BigDecimal("100.00");
        List<ExpenseResponse> highExpenses = expenseService.getHighValueExpenses(threshold, "valentino");

        assertFalse(highExpenses.isEmpty());
        for (ExpenseResponse exp : highExpenses) {
            assertTrue(exp.getAmount().compareTo(threshold) >= 0);
        }
    }

    @Test
    @DisplayName("CP-EXP-07: Eliminación de gasto persiste y remueve el registro")
    void testDeleteExpense() {
        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("15.00"))
                .categoryName("Ocio")
                .description("Café temporal para eliminar")
                .build();

        ExpenseResponse created = expenseService.createExpense(request, "valentino");
        Long id = created.getId();

        expenseService.deleteExpense(id, "valentino");
        assertFalse(expenseRepository.findById(id).isPresent());
    }
}
