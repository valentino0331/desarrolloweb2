package pe.edu.utp.salviafinanzas.service;

import pe.edu.utp.salviafinanzas.dto.request.CategoryRequest;
import pe.edu.utp.salviafinanzas.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request, String username);
    List<CategoryResponse> getAllCategories(String username);
    CategoryResponse getCategoryById(Long id, String username);
    CategoryResponse updateCategory(Long id, CategoryRequest request, String username);
    void deleteCategory(Long id, String username);
    List<CategoryResponse> searchCategories(String keyword, String username);
}
