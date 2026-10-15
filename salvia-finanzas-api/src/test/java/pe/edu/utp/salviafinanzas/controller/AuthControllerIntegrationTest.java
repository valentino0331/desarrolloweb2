package pe.edu.utp.salviafinanzas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pe.edu.utp.salviafinanzas.dto.request.LoginRequest;
import pe.edu.utp.salviafinanzas.dto.request.RegisterRequest;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de Integración para Autenticación y Flujo JWT (Consigna 3 y Consigna 4)
 * Valida registro, login, emisión de tokens JWT, rechazo de credenciales inválidas y protección de rutas.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("CP-AUTH-01: Login exitoso con credenciales válidas genera Token JWT Bearer")
    void testSuccessfulLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("valentino", "User123!*");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.type", is("Bearer")))
                .andExpect(jsonPath("$.username", is("valentino")))
                .andExpect(jsonPath("$.roles", hasItem("ROLE_USER")));
    }

    @Test
    @DisplayName("CP-AUTH-02: Login fallido con contraseña errónea retorna 401 Unauthorized")
    void testFailedLoginWithBadPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("valentino", "PasswordInvalida_999");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("No autorizado")));
    }

    @Test
    @DisplayName("CP-AUTH-03: Registro de nuevo usuario exitoso retorna 201 Created y Token JWT")
    void testSuccessfulUserRegistration() throws Exception {
        String uniqueUser = "alumno_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest(
                uniqueUser,
                uniqueUser + "@utp.edu.pe",
                "ClaveSegura2026!",
                "Estudiante UTP de Prueba",
                Set.of("USER")
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username", is(uniqueUser)))
                .andExpect(jsonPath("$.email", is(uniqueUser + "@utp.edu.pe")));
    }

    @Test
    @DisplayName("CP-AUTH-04: Registro duplicado con mismo username retorna 400 Bad Request")
    void testDuplicateUsernameRegistration() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "valentino",
                "otro_correo@utp.edu.pe",
                "ClaveSegura2026!",
                "Valentino Duplicado",
                Set.of("USER")
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Petición inválida")));
    }

    @Test
    @DisplayName("CP-AUTH-05: Petición sin Token a endpoint protegido /api/auth/me retorna 401")
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("No autorizado")));
    }

    @Test
    @DisplayName("CP-AUTH-06: Petición con Token JWT válido a /api/auth/me retorna perfil 200 OK")
    void testProtectedEndpointWithValidToken() throws Exception {
        // 1. Iniciar sesión para obtener token
        LoginRequest loginRequest = new LoginRequest("valentino", "User123!*");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        // 2. Acceder al endpoint protegido usando la cabecera Authorization: Bearer <token>
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("valentino")))
                .andExpect(jsonPath("$.email", is("valentino@salviafinanzas.utp.pe")))
                .andExpect(jsonPath("$.fullName", is("Valentino Martin Marca Quedena")));
    }
}
