package pe.edu.utp.salviafinanzas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de Misión de Ahorro (Salvia Finanzas)
 * Modela metas cuantificables de ahorro financiero con control de fechas límite y progreso.
 */
@Entity
@Table(name = "savings_missions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 150)
    @Column(nullable = false, length = 150)
    private String title;

    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @NotNull
    @DecimalMin(value = "1.00", message = "El monto meta debe ser al menos 1.00")
    @Column(name = "target_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @NotNull
    @Column(name = "current_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(length = 100)
    @Builder.Default
    private String icon = "savings_default";

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.currentAmount == null) {
            this.currentAmount = BigDecimal.ZERO;
        }
        if (this.isCompleted == null) {
            this.isCompleted = false;
        }
    }

    /**
     * Calcula el porcentaje de avance hacia la meta (0% a 100%)
     */
    public Double getProgressPercentage() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        double progress = currentAmount.doubleValue() / targetAmount.doubleValue() * 100.0;
        return Math.min(100.0, Math.round(progress * 100.0) / 100.0);
    }
}
