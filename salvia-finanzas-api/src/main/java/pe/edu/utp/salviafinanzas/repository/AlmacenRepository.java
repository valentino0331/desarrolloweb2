package pe.edu.utp.salviafinanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.salviafinanzas.model.Almacen;

import java.util.Optional;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    Optional<Almacen> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
