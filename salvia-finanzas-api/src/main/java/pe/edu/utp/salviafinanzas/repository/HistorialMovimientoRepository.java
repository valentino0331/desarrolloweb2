package pe.edu.utp.salviafinanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.salviafinanzas.model.HistorialMovimiento;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialMovimientoRepository extends JpaRepository<HistorialMovimiento, Long> {

    List<HistorialMovimiento> findByResiduoIdOrderByFechaMovimientoDesc(Long residuoId);

    /**
     * JPQL: Auditoría de Movimientos Recientes para Fiscalización MINAM
     */
    @Query("SELECT h FROM HistorialMovimiento h WHERE h.residuo.id = :residuoId AND h.fechaMovimiento >= :fechaDesde ORDER BY h.fechaMovimiento DESC")
    List<HistorialMovimiento> findAuditoriaReciente(
            @Param("residuoId") Long residuoId,
            @Param("fechaDesde") LocalDateTime fechaDesde
    );
}
