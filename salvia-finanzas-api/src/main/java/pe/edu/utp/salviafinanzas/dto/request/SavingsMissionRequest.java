package pe.edu.utp.salviafinanzas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsMissionRequest {

    @NotBlank(message = "El título de la misión es obligatorio")
    private String title;

    private String description;

    @NotNull(message = "El monto objetivo es obligatorio")
    @DecimalMin(value = "1.00", message = "La meta debe ser de al menos 1.00")
    private BigDecimal targetAmount;

    private BigDecimal currentAmount;

    private LocalDate deadline;

    private String icon;
}
