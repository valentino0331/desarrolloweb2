package pe.edu.utp.salviafinanzas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.salviafinanzas.dto.request.LoginRequest;
import pe.edu.utp.salviafinanzas.dto.request.RegisterRequest;
import pe.edu.utp.salviafinanzas.dto.response.AuthResponse;
import pe.edu.utp.salviafinanzas.dto.response.UserResponse;
import pe.edu.utp.salviafinanzas.service.AuthService;

/**
 * Controlador REST de Autenticación y Registro con JWT
 * Endpoints públicos para login, register y perfil verificado mediante token.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
