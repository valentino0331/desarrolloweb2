package pe.edu.utp.salviafinanzas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {

    private Boolean success;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
