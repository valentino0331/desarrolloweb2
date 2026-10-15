package pe.edu.utp.salviafinanzas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.salviafinanzas.dto.response.ApiResponse;
import pe.edu.utp.salviafinanzas.dto.response.UserResponse;
import pe.edu.utp.salviafinanzas.service.UserService;

import java.util.List;

/**
 * Controlador de Usuarios y Administración de Cuentas
 * Demuestra la seguridad granular con restricción exclusiva para ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    /**
     * Endpoint protegido exclusivo para administradores
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Obtener detalle de usuario por ID (exclusivo ADMIN)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Eliminar usuario por ID (exclusivo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "Usuario eliminado correctamente con id: " + id));
    }

    /**
     * Ranking / Tabla de clasificación por gamificación (Accesible para USER y ADMIN)
     */
    @GetMapping("/leaderboard")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GUEST')")
    public ResponseEntity<List<UserResponse>> getLeaderboard(
            @RequestParam(defaultValue = "0") Integer minPoints) {
        List<UserResponse> leaderboard = userService.getLeaderboard(minPoints);
        return ResponseEntity.ok(leaderboard);
    }
}
