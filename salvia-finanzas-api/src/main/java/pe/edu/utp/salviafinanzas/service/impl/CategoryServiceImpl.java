package pe.edu.utp.salviafinanzas.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.salviafinanzas.dto.request.CategoryRequest;
import pe.edu.utp.salviafinanzas.dto.response.CategoryResponse;
import pe.edu.utp.salviafinanzas.exception.ResourceNotFoundException;
import pe.edu.utp.salviafinanzas.model.Category;
import pe.edu.utp.salviafinanzas.model.User;
import pe.edu.utp.salviafinanzas.repository.CategoryRepository;
import pe.edu.utp.salviafinanzas.repository.UserRepository;
import pe.edu.utp.salviafinanzas.service.CategoryService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryResponse createCategory(CategoryRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .monthlyBudget(request.getMonthlyBudget() != null ? request.getMonthlyBudget() : BigDecimal.ZERO)
                .icon(request.getIcon() != null ? request.getIcon() : "category_default")
                .color(request.getColor() != null ? request.getColor() : "#4CAF50")
                .user(user)
                .build();

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return categoryRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));

        return mapToResponse(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryResponse updateCategory(Long id, CategoryRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getMonthlyBudget() != null) {
            category.setMonthlyBudget(request.getMonthlyBudget());
        }
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }

        Category updated = categoryRepository.save(category);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategories(String keyword, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return categoryRepository.searchCategoriesByName(user.getId(), keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .monthlyBudget(category.getMonthlyBudget())
                .icon(category.getIcon())
                .color(category.getColor())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
