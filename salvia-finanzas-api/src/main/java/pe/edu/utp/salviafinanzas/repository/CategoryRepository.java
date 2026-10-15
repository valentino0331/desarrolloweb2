package pe.edu.utp.salviafinanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.salviafinanzas.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Categorías con JPQL
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    /**
     * Consulta JPQL personalizada: Búsqueda insensible a mayúsculas por nombre de categoría para un usuario
     */
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY c.name ASC")
    List<Category> searchCategoriesByName(@Param("userId") Long userId, @Param("keyword") String keyword);
}
