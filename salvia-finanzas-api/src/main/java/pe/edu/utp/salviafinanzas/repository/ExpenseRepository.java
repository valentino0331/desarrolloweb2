package pe.edu.utp.salviafinanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.salviafinanzas.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Gastos (JPA - Hibernate)
 * Implementa consultas personalizadas mediante JPQL alineadas a la Consigna 2.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    /**
     * JPQL 1: Filtrado de transacciones por categoría específica del usuario
     */
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND LOWER(e.categoryName) = LOWER(:category) ORDER BY e.expenseDate DESC")
    List<Expense> findExpensesByUserAndCategory(@Param("userId") Long userId, @Param("category") String category);

    /**
     * JPQL 2: Filtrado de transacciones dentro de un intervalo temporal (rango de fechas)
     */
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.expenseDate BETWEEN :startDate AND :endDate ORDER BY e.expenseDate DESC")
    List<Expense> findExpensesByDateRange(@Param("userId") Long userId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * JPQL 3: Cálculo agregado del monto total desembolsado por categoría
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND LOWER(e.categoryName) = LOWER(:category)")
    BigDecimal calculateTotalExpenseByUserAndCategory(@Param("userId") Long userId, @Param("category") String category);

    /**
     * JPQL 4: Filtrado de gastos de alto impacto económico (superiores o iguales a un umbral)
     */
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.amount >= :minAmount ORDER BY e.amount DESC")
    List<Expense> findHighValueExpenses(@Param("userId") Long userId, @Param("minAmount") BigDecimal minAmount);
}
