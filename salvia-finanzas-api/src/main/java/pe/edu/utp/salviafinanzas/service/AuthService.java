package pe.edu.utp.salviafinanzas.service;

import pe.edu.utp.salviafinanzas.dto.request.LoginRequest;
import pe.edu.utp.salviafinanzas.dto.request.RegisterRequest;
import pe.edu.utp.salviafinanzas.dto.response.AuthResponse;
import pe.edu.utp.salviafinanzas.dto.response.UserResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);
    UserResponse getCurrentUser(String username);
}
