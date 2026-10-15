package pe.edu.utp.salviafinanzas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de Categoría Financiera (Salvia Finanzas)
 * Permite organizar gastos y establecer presupuestos mensuales máximos.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @Column(name = "monthly_budget", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal monthlyBudget = BigDecimal.ZERO;

    @Column(length = 50)
    @Builder.Default
    private String icon = "category_default";

    @Column(length = 20)
    @Builder.Default
    private String color = "#4CAF50";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<Expense> expenses = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.monthlyBudget == null) this.monthlyBudget = BigDecimal.ZERO;
    }
}
