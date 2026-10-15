package pe.edu.utp.salviafinanzas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionDepositRequest {

    @NotNull(message = "El monto de aporte no puede ser nulo")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0.00")
    private BigDecimal amount;
}
