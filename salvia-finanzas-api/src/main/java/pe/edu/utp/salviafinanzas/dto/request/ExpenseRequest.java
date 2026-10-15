package pe.edu.utp.salviafinanzas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {

    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0.00")
    private BigDecimal amount;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoryName;

    private String subcategory;

    private String description;

    private LocalDateTime expenseDate;

    private String recurrence;

    private Long categoryId;
}
