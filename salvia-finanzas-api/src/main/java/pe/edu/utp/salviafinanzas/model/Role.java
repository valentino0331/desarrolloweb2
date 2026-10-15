package pe.edu.utp.salviafinanzas.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad de Rol en persistencia JPA
 * Define los permisos asignables a cada usuario del sistema.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false, unique = true)
    private ERole name;

    public Role(ERole name) {
        this.name = name;
    }
}
