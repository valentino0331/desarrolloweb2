package pe.edu.utp.salviafinanzas.service;

import pe.edu.utp.salviafinanzas.dto.request.MissionDepositRequest;
import pe.edu.utp.salviafinanzas.dto.request.SavingsMissionRequest;
import pe.edu.utp.salviafinanzas.dto.response.SavingsMissionResponse;

import java.math.BigDecimal;
import java.util.List;

public interface SavingsMissionService {
    SavingsMissionResponse createMission(SavingsMissionRequest request, String username);
    List<SavingsMissionResponse> getAllMissions(String username);
    SavingsMissionResponse getMissionById(Long id, String username);
    SavingsMissionResponse updateMission(Long id, SavingsMissionRequest request, String username);
    void deleteMission(Long id, String username);

    // Operación Transaccional con validación de negocio y actualización de gamificación
    SavingsMissionResponse depositToMission(Long id, MissionDepositRequest request, String username);

    // Consultas personalizadas JPQL
    List<SavingsMissionResponse> getMissionsByCompletionStatus(Boolean isCompleted, String username);
    List<SavingsMissionResponse> getMissionsNearDeadline(int daysAhead, String username);
    List<SavingsMissionResponse> getMissionsWithSavingsAbove(BigDecimal threshold, String username);
}
