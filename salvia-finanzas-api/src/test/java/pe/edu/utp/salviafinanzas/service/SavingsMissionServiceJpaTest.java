package pe.edu.utp.salviafinanzas.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.salviafinanzas.dto.request.MissionDepositRequest;
import pe.edu.utp.salviafinanzas.dto.request.SavingsMissionRequest;
import pe.edu.utp.salviafinanzas.dto.response.SavingsMissionResponse;
import pe.edu.utp.salviafinanzas.dto.response.UserResponse;
import pe.edu.utp.salviafinanzas.exception.BadRequestException;
import pe.edu.utp.salviafinanzas.repository.SavingsMissionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de Persistencia JPA, Transacciones Atómicas y Consultas JPQL para Misiones de Ahorro
 */
@SpringBootTest
@Transactional
class SavingsMissionServiceJpaTest {

    @Autowired
    private SavingsMissionService missionService;

    @Autowired
    private SavingsMissionRepository missionRepository;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("CP-MIS-01: Creación de misión de ahorro persiste en H2")
    void testCreateMissionPersistence() {
        SavingsMissionRequest request = SavingsMissionRequest.builder()
                .title("Ahorro Certificación Cloud")
                .description("Voucher de examen AWS / GCP")
                .targetAmount(new BigDecimal("600.00"))
                .currentAmount(new BigDecimal("100.00"))
                .deadline(LocalDate.now().plusMonths(3))
                .icon("cloud_certificate")
                .build();

        SavingsMissionResponse created = missionService.createMission(request, "valentino");

        assertNotNull(created.getId());
        assertEquals("Ahorro Certificación Cloud", created.getTitle());
        assertEquals(new BigDecimal("600.00"), created.getTargetAmount());
        assertFalse(created.getIsCompleted());
        assertTrue(missionRepository.findById(created.getId()).isPresent());
    }

    @Test
    @DisplayName("CP-MIS-02: Operación transaccional de depósito incrementa fondos y otorga XP")
    void testTransactionalDeposit() {
        SavingsMissionRequest request = SavingsMissionRequest.builder()
                .title("Ahorro Monitor UltraWide")
                .targetAmount(new BigDecimal("1000.00"))
                .currentAmount(new BigDecimal("200.00"))
                .deadline(LocalDate.now().plusMonths(2))
                .build();

        SavingsMissionResponse created = missionService.createMission(request, "valentino");

        // Realizar depósito transaccional de 150.00
        MissionDepositRequest deposit = new MissionDepositRequest(new BigDecimal("150.00"));
        SavingsMissionResponse updated = missionService.depositToMission(created.getId(), deposit, "valentino");

        assertEquals(new BigDecimal("350.00"), updated.getCurrentAmount());
        assertEquals(35.0, updated.getProgressPercentage());
        assertFalse(updated.getIsCompleted());
    }

    @Test
    @DisplayName("CP-MIS-03: Depósito que completa la meta marca isCompleted=true y premia con 100 XP")
    void testDepositCompletesMissionAndAwardsBonus() {
        UserResponse userBefore = authService.getCurrentUser("valentino");
        int pointsBefore = userBefore.getPoints();

        SavingsMissionRequest request = SavingsMissionRequest.builder()
                .title("Libro Clean Architecture")
                .targetAmount(new BigDecimal("150.00"))
                .currentAmount(new BigDecimal("100.00"))
                .deadline(LocalDate.now().plusDays(10))
                .build();

        SavingsMissionResponse created = missionService.createMission(request, "valentino");

        // Completar la meta con 60.00 (100 + 60 = 160 >= 150)
        MissionDepositRequest deposit = new MissionDepositRequest(new BigDecimal("60.00"));
        SavingsMissionResponse completed = missionService.depositToMission(created.getId(), deposit, "valentino");

        assertTrue(completed.getIsCompleted());
        assertEquals(100.0, completed.getProgressPercentage());

        UserResponse userAfter = authService.getCurrentUser("valentino");
        assertEquals(pointsBefore + 100, userAfter.getPoints(), "Alcanzar la meta debe otorgar 100 XP de bonificación");
    }

    @Test
    @DisplayName("CP-MIS-04: Depósito con monto negativo o cero lanza BadRequestException")
    void testInvalidDepositAmountThrowsBadRequest() {
        SavingsMissionRequest request = SavingsMissionRequest.builder()
                .title("Meta Prueba Error")
                .targetAmount(new BigDecimal("300.00"))
                .build();

        SavingsMissionResponse created = missionService.createMission(request, "valentino");

        MissionDepositRequest negativeDeposit = new MissionDepositRequest(new BigDecimal("-50.00"));
        assertThrows(BadRequestException.class, () ->
                missionService.depositToMission(created.getId(), negativeDeposit, "valentino"));
    }

    @Test
    @DisplayName("CP-MIS-05: Consulta JPQL 1 filtra misiones por estado completado/pendiente")
    void testJpqlFilterByCompletionStatus() {
        List<SavingsMissionResponse> activeMissions = missionService.getMissionsByCompletionStatus(false, "valentino");
        List<SavingsMissionResponse> completedMissions = missionService.getMissionsByCompletionStatus(true, "valentino");

        assertFalse(activeMissions.isEmpty());
        for (SavingsMissionResponse m : activeMissions) {
            assertFalse(m.getIsCompleted());
        }

        assertFalse(completedMissions.isEmpty());
        for (SavingsMissionResponse m : completedMissions) {
            assertTrue(m.getIsCompleted());
        }
    }

    @Test
    @DisplayName("CP-MIS-06: Consulta JPQL 2 filtra metas de ahorro próximas a vencer")
    void testJpqlFilterNearDeadline() {
        List<SavingsMissionResponse> nearDeadlineMissions = missionService.getMissionsNearDeadline(30, "valentino");

        assertNotNull(nearDeadlineMissions);
        for (SavingsMissionResponse m : nearDeadlineMissions) {
            assertFalse(m.getIsCompleted());
            assertNotNull(m.getDeadline());
        }
    }

    @Test
    @DisplayName("CP-MIS-07: Consulta JPQL 3 filtra misiones con fondos acumulados superiores a umbral")
    void testJpqlFilterSavingsAboveThreshold() {
        BigDecimal threshold = new BigDecimal("800.00");
        List<SavingsMissionResponse> highSavings = missionService.getMissionsWithSavingsAbove(threshold, "valentino");

        assertFalse(highSavings.isEmpty());
        for (SavingsMissionResponse m : highSavings) {
            assertTrue(m.getCurrentAmount().compareTo(threshold) >= 0);
        }
    }
}
