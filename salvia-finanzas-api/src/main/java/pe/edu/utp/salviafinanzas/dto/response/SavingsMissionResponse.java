package pe.edu.utp.salviafinanzas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsMissionResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Double progressPercentage;
    private LocalDate deadline;
    private Boolean isCompleted;
    private String icon;
    private LocalDateTime createdAt;
}
