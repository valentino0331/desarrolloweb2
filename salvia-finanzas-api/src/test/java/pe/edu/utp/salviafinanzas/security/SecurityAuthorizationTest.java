package pe.edu.utp.salviafinanzas.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pe.edu.utp.salviafinanzas.dto.request.LoginRequest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de Autorización y Control de Acceso Basado en Roles (RBAC - Consigna 3)
 * Valida la protección con @PreAuthorize: USER vs ADMIN vs GUEST.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private String adminToken;
    private String guestToken;

    @BeforeEach
    void setUpTokens() throws Exception {
        userToken = obtainToken("valentino", "User123!*");
        adminToken = obtainToken("admin", "Admin123!*");
        guestToken = obtainToken("invitado", "Guest123!*");
    }

    private String obtainToken(String username, String password) throws Exception {
        LoginRequest login = new LoginRequest(username, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    @DisplayName("CP-SEC-01: Usuario con ROLE_USER puede consultar sus gastos (/api/expenses)")
    void testUserCanAccessExpenses() throws Exception {
        mockMvc.perform(get("/api/expenses")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(Iterable.class)));
    }

    @Test
    @DisplayName("CP-SEC-02: Usuario con ROLE_USER recibe 403 FORBIDDEN al acceder a ruta ADMIN (/api/users)")
    void testUserCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Acceso denegado")))
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("CP-SEC-03: Administrador con ROLE_ADMIN puede listar usuarios (/api/users)")
    void testAdminCanAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    @DisplayName("CP-SEC-04: Usuario GUEST puede consultar categorías pero no puede crear gastos")
    void testGuestCannotCreateExpense() throws Exception {
        // GUEST sí puede ver categorías
        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isOk());

        // GUEST intenta crear un gasto (requiere ROLE_USER o ROLE_ADMIN) -> 403 Forbidden
        String expensePayload = """
                {
                    "amount": 50.00,
                    "categoryName": "Comida",
                    "description": "Intento de gasto invitado"
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expensePayload))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CP-SEC-05: Petición anónima a /api/expenses es rechazada con 401 UNAUTHORIZED")
    void testAnonymousAccessIsRejectedWith401() throws Exception {
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("No autorizado")));
    }
}
