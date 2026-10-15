package pe.edu.utp.salviafinanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.salviafinanzas.model.SavingsMission;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Misiones de Ahorro
 * Consultas personalizadas JPQL alineadas a la Consigna 2.
 */
@Repository
public interface SavingsMissionRepository extends JpaRepository<SavingsMission, Long> {

    List<SavingsMission> findByUserId(Long userId);

    Optional<SavingsMission> findByIdAndUserId(Long id, Long userId);

    /**
     * JPQL 1: Filtrado de misiones según su estado de completitud (Activas vs Completadas)
     */
    @Query("SELECT m FROM SavingsMission m WHERE m.user.id = :userId AND m.isCompleted = :isCompleted ORDER BY m.deadline ASC")
    List<SavingsMission> findByUserIdAndIsCompleted(@Param("userId") Long userId, @Param("isCompleted") Boolean isCompleted);

    /**
     * JPQL 2: Filtrado de metas de ahorro próximas a vencer (entre hoy y una fecha límite)
     */
    @Query("SELECT m FROM SavingsMission m WHERE m.user.id = :userId AND m.isCompleted = false AND m.deadline BETWEEN :today AND :deadlineLimit ORDER BY m.deadline ASC")
    List<SavingsMission> findMissionsNearDeadline(@Param("userId") Long userId,
                                                 @Param("today") LocalDate today,
                                                 @Param("deadlineLimit") LocalDate deadlineLimit);

    /**
     * JPQL 3: Misiones de ahorro con avance superior al umbral
     */
    @Query("SELECT m FROM SavingsMission m WHERE m.user.id = :userId AND m.currentAmount >= :thresholdAmount ORDER BY m.currentAmount DESC")
    List<SavingsMission> findMissionsWithSavingsAbove(@Param("userId") Long userId,
                                                     @Param("thresholdAmount") java.math.BigDecimal thresholdAmount);
}
