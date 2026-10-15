package pe.edu.utp.salviafinanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.salviafinanzas.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Usuarios con consultas estándar y JPQL
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    /**
     * Consulta JPQL personalizada: Obtiene usuarios con puntuación gamificada superior a un umbral
     */
    @Query("SELECT u FROM User u WHERE u.points >= :minPoints ORDER BY u.points DESC")
    List<User> findLeaderboardUsers(@Param("minPoints") Integer minPoints);
}
