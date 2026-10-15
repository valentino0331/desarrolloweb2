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
public class ExpenseResponse {

    private Long id;
    private BigDecimal amount;
    private String categoryName;
    private Long categoryId;
    private String subcategory;
    private String description;
    private LocalDateTime expenseDate;
    private String recurrence;
    private LocalDateTime createdAt;
}
