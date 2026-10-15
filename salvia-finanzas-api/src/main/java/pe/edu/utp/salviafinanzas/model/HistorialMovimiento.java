package pe.edu.utp.salviafinanzas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad de Trazabilidad y Auditoría Inmutable (EcoTechTrack)
 */
@Entity
@Table(name = "historial_movimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 30)
    private EstadoResiduo estadoAnterior;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 30)
    private EstadoResiduo estadoNuevo;

    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String responsable;

    @Size(max = 255)
    @Column(length = 255)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "residuo_id", nullable = false)
    @JsonIgnore
    private ResiduoElectronico residuo;

    @PrePersist
    protected void onCreate() {
        if (this.fechaMovimiento == null) {
            this.fechaMovimiento = LocalDateTime.now();
        }
    }
}
