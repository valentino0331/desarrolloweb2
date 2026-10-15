package pe.edu.utp.salviafinanzas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Principal: Residuo Electrónico (EcoTechTrack)
 * Conforme al D.S. N° 009-2019-MINAM
 */
@Entity
@Table(name = "residuos_electronicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResiduoElectronico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código de identificación es obligatorio")
    @Size(max = 50)
    @Column(name = "codigo_identificacion", nullable = false, unique = true, length = 50)
    private String codigoIdentificacion;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaResiduo categoria;

    @Size(max = 50)
    @Column(length = 50)
    private String marca;

    @Size(max = 50)
    @Column(length = 50)
    private String modelo;

    @Size(max = 50)
    @Column(name = "numero_serie", length = 50)
    private String numeroSerie;

    @NotNull
    @Positive(message = "El peso en kg debe ser positivo")
    @Column(name = "peso_kg", nullable = false)
    private Double pesoKg;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoResiduo estado = EstadoResiduo.DECLARADO_BAJA;

    @Size(max = 255)
    @Column(length = 255)
    private String observaciones;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relación Many-to-One con Almacén de Acopio Temporal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "residuos"})
    private Almacen almacen;

    // Relación Many-to-One con Empresa Gestora RAEE (EO-RS)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_gestora_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "residuos"})
    private EmpresaGestora empresaGestora;

    // Relación One-to-Many con el Historial Inmutable de Auditoría
    @OneToMany(mappedBy = "residuo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaMovimiento DESC")
    @Builder.Default
    private List<HistorialMovimiento> historialMovimientos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoResiduo.DECLARADO_BAJA;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
