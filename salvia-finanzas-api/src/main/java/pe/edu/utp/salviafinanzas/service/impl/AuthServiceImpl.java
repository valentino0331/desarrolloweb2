package pe.edu.utp.salviafinanzas.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.salviafinanzas.dto.request.LoginRequest;
import pe.edu.utp.salviafinanzas.dto.request.RegisterRequest;
import pe.edu.utp.salviafinanzas.dto.response.AuthResponse;
import pe.edu.utp.salviafinanzas.dto.response.UserResponse;
import pe.edu.utp.salviafinanzas.exception.BadRequestException;
import pe.edu.utp.salviafinanzas.exception.ResourceNotFoundException;
import pe.edu.utp.salviafinanzas.model.ERole;
import pe.edu.utp.salviafinanzas.model.Role;
import pe.edu.utp.salviafinanzas.model.User;
import pe.edu.utp.salviafinanzas.repository.RoleRepository;
import pe.edu.utp.salviafinanzas.repository.UserRepository;
import pe.edu.utp.salviafinanzas.security.jwt.JwtUtils;
import pe.edu.utp.salviafinanzas.security.services.UserDetailsImpl;
import pe.edu.utp.salviafinanzas.service.AuthService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación del Servicio de Autenticación y Registro con JWT
 * Cumple con la Consigna 4 de emisión y validación de credenciales.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .fullName(userDetails.getFullName())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("El nombre de usuario '" + registerRequest.getUsername() + "' ya está registrado");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("El correo electrónico '" + registerRequest.getEmail() + "' ya está registrado");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .points(10) // Puntos de bienvenida por registro
                .streak(1)
                .build();

        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol ROLE_USER no encontrado"));
            roles.add(userRole);
        } else {
            strRoles.forEach(roleName -> {
                switch (roleName.toUpperCase()) {
                    case "ADMIN":
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Rol ROLE_ADMIN no encontrado"));
                        roles.add(adminRole);
                        break;
                    case "GUEST":
                    case "ROLE_GUEST":
                        Role guestRole = roleRepository.findByName(ERole.ROLE_GUEST)
                                .orElseThrow(() -> new ResourceNotFoundException("Rol ROLE_GUEST no encontrado"));
                        roles.add(guestRole);
                        break;
                    default:
                        Role defaultRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new ResourceNotFoundException("Rol ROLE_USER no encontrado"));
                        roles.add(defaultRole);
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // Autenticar y generar token inmediatamente
        String jwt = jwtUtils.generateTokenFromUsername(savedUser.getUsername());

        List<String> roleList = savedUser.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .roles(roleList)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .points(user.getPoints())
                .streak(user.getStreak())
                .monthlyIncome(user.getMonthlyIncome())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
