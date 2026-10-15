package pe.edu.utp.salviafinanzas.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;

    private String description;

    private BigDecimal monthlyBudget;

    private String icon;

    private String color;
}
