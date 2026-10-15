package pe.edu.utp.salviafinanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.salviafinanzas.model.CategoriaResiduo;
import pe.edu.utp.salviafinanzas.model.EstadoResiduo;
import pe.edu.utp.salviafinanzas.model.ResiduoElectronico;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResiduoRepository extends JpaRepository<ResiduoElectronico, Long> {

    Optional<ResiduoElectronico> findByCodigoIdentificacion(String codigoIdentificacion);
    boolean existsByCodigoIdentificacion(String codigoIdentificacion);

    List<ResiduoElectronico> findByEstado(EstadoResiduo estado);

    /**
     * JPQL 1: Filtrado de Residuos por Categoría y Estado
     */
    @Query("SELECT r FROM ResiduoElectronico r WHERE r.categoria = :categoria AND r.estado = :estado ORDER BY r.fechaRegistro DESC")
    List<ResiduoElectronico> findByCategoriaYEstado(
            @Param("categoria") CategoriaResiduo categoria,
            @Param("estado") EstadoResiduo estado
    );

    /**
     * JPQL 2: Residuos de Gran Volumen o Peso Crítico
     */
    @Query("SELECT r FROM ResiduoElectronico r WHERE r.pesoKg >= :minPeso ORDER BY r.pesoKg DESC")
    List<ResiduoElectronico> findResiduosGranVolumen(@Param("minPeso") Double minPeso);

    /**
     * JPQL 3: Filtrado de Residuos por Código de Almacén
     */
    @Query("SELECT r FROM ResiduoElectronico r JOIN r.almacen a WHERE a.codigo = :codigoAlmacen")
    List<ResiduoElectronico> findByAlmacenCodigo(@Param("codigoAlmacen") String codigoAlmacen);
}
