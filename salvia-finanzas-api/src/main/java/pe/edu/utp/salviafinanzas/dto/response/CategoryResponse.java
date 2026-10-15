package pe.edu.utp.salviafinanzas.dto.response;

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
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal monthlyBudget;
    private String icon;
    private String color;
    private LocalDateTime createdAt;
}
