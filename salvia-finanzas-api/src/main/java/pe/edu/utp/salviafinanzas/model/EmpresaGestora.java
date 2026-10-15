package pe.edu.utp.salviafinanzas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Empresa Gestora (EO-RS autorizada por MINAM)
 */
@Entity
@Table(name = "empresas_gestoras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaGestora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 11, max = 11)
    @Pattern(regexp = "\\d{11}", message = "El RUC debe tener 11 dígitos numéricos")
    @Column(nullable = false, unique = true, length = 11)
    private String ruc;

    @NotBlank
    @Size(max = 150)
    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @NotBlank
    @Size(max = 50)
    @Column(name = "registro_autorizacion", nullable = false, length = 50)
    private String registroAutorizacion;

    @Size(max = 100)
    @Column(length = 100)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    private String telefono;

    @Size(max = 200)
    @Column(length = 200)
    private String direccion;

    @OneToMany(mappedBy = "empresaGestora")
    @JsonIgnore
    @Builder.Default
    private List<ResiduoElectronico> residuos = new ArrayList<>();
}
