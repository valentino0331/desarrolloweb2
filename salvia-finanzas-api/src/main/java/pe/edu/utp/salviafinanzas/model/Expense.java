package pe.edu.utp.salviafinanzas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de Transacción de Gasto (Salvia Finanzas)
 * Representa los desembolsos financieros clasificados y vinculados a un usuario y categoría.
 */
@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 100)
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Size(max = 100)
    @Column(length = 100)
    private String subcategory;

    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @NotNull
    @Column(name = "expense_date", nullable = false)
    private LocalDateTime expenseDate;

    @Column(length = 50)
    @Builder.Default
    private String recurrence = "ONCE"; // ONCE, DAILY, WEEKLY, MONTHLY

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.expenseDate == null) {
            this.expenseDate = LocalDateTime.now();
        }
        if (this.recurrence == null) {
            this.recurrence = "ONCE";
        }
    }
}
