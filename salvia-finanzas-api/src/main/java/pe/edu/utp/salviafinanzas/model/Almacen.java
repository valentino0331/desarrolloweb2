package pe.edu.utp.salviafinanzas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Almacen de Acopio Temporal (EcoTechTrack)
 */
@Entity
@Table(name = "almacenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String sede;

    @Size(max = 200)
    @Column(length = 200)
    private String direccion;

    @NotNull
    @Column(name = "capacidad_kg", nullable = false)
    private Double capacidadKg;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String responsable;

    @OneToMany(mappedBy = "almacen")
    @JsonIgnore
    @Builder.Default
    private List<ResiduoElectronico> residuos = new ArrayList<>();
}
