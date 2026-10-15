package pe.edu.utp.salviafinanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.salviafinanzas.model.EmpresaGestora;

import java.util.Optional;

@Repository
public interface EmpresaGestoraRepository extends JpaRepository<EmpresaGestora, Long> {
    Optional<EmpresaGestora> findByRuc(String ruc);
    boolean existsByRuc(String ruc);
}
