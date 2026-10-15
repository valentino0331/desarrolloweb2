package pe.edu.utp.salviafinanzas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.salviafinanzas.dto.request.CategoryRequest;
import pe.edu.utp.salviafinanzas.dto.response.ApiResponse;
import pe.edu.utp.salviafinanzas.dto.response.CategoryResponse;
import pe.edu.utp.salviafinanzas.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CategoryResponse response = categoryService.createCategory(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GUEST')")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<CategoryResponse> list = categoryService.getAllCategories(userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GUEST')")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        CategoryResponse response = categoryService.getCategoryById(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CategoryResponse response = categoryService.updateCategory(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        categoryService.deleteCategory(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "Categoría eliminada con éxito con id: " + id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GUEST')")
    public ResponseEntity<List<CategoryResponse>> searchCategories(
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<CategoryResponse> list = categoryService.searchCategories(keyword, userDetails.getUsername());
        return ResponseEntity.ok(list);
    }
}
