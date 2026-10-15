package pe.edu.utp.salviafinanzas.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.salviafinanzas.dto.request.MissionDepositRequest;
import pe.edu.utp.salviafinanzas.dto.request.SavingsMissionRequest;
import pe.edu.utp.salviafinanzas.dto.response.SavingsMissionResponse;
import pe.edu.utp.salviafinanzas.exception.BadRequestException;
import pe.edu.utp.salviafinanzas.exception.ResourceNotFoundException;
import pe.edu.utp.salviafinanzas.model.SavingsMission;
import pe.edu.utp.salviafinanzas.model.User;
import pe.edu.utp.salviafinanzas.repository.SavingsMissionRepository;
import pe.edu.utp.salviafinanzas.repository.UserRepository;
import pe.edu.utp.salviafinanzas.service.SavingsMissionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la Lógica de Negocio de Misiones de Ahorro
 * Gestiona el progreso de ahorro con operaciones atómicas y transaccionales (@Transactional).
 */
@Service
@RequiredArgsConstructor
public class SavingsMissionServiceImpl implements SavingsMissionService {

    private final SavingsMissionRepository missionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SavingsMissionResponse createMission(SavingsMissionRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        BigDecimal initialAmount = request.getCurrentAmount() != null ? request.getCurrentAmount() : BigDecimal.ZERO;
        boolean completed = initialAmount.compareTo(request.getTargetAmount()) >= 0;

        SavingsMission mission = SavingsMission.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .targetAmount(request.getTargetAmount())
                .currentAmount(initialAmount)
                .deadline(request.getDeadline())
                .icon(request.getIcon() != null ? request.getIcon() : "savings_default")
                .isCompleted(completed)
                .user(user)
                .build();

        SavingsMission saved = missionRepository.save(mission);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingsMissionResponse> getAllMissions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return missionRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SavingsMissionResponse getMissionById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        SavingsMission mission = missionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Misión de ahorro", "id", id));

        return mapToResponse(mission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SavingsMissionResponse updateMission(Long id, SavingsMissionRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        SavingsMission mission = missionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Misión de ahorro", "id", id));

        mission.setTitle(request.getTitle());
        mission.setDescription(request.getDescription());
        mission.setTargetAmount(request.getTargetAmount());
        if (request.getDeadline() != null) {
            mission.setDeadline(request.getDeadline());
        }
        if (request.getIcon() != null) {
            mission.setIcon(request.getIcon());
        }
        if (request.getCurrentAmount() != null) {
            mission.setCurrentAmount(request.getCurrentAmount());
            mission.setIsCompleted(mission.getCurrentAmount().compareTo(mission.getTargetAmount()) >= 0);
        }

        SavingsMission updated = missionRepository.save(mission);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMission(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        SavingsMission mission = missionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Misión de ahorro", "id", id));

        missionRepository.delete(mission);
    }

    /**
     * OPERACIÓN TRANSACCIONAL AVANZADA:
     * Deposita fondos a una meta de ahorro, valida invariantes y recompensa con XP al completar la meta.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SavingsMissionResponse depositToMission(Long id, MissionDepositRequest request, String username) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El monto a depositar debe ser estrictamente mayor a 0.00");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        SavingsMission mission = missionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Misión de ahorro", "id", id));

        if (Boolean.TRUE.equals(mission.getIsCompleted())) {
            throw new BadRequestException("La misión ya se encuentra 100% completada");
        }

        BigDecimal newAmount = mission.getCurrentAmount().add(request.getAmount());
        mission.setCurrentAmount(newAmount);

        // Si se alcanza o supera el objetivo de la misión, se marca como completada y premia al usuario
        if (newAmount.compareTo(mission.getTargetAmount()) >= 0) {
            mission.setIsCompleted(true);
            user.setPoints(user.getPoints() + 100); // 100 XP por culminación de meta
            userRepository.save(user);
        } else {
            user.setPoints(user.getPoints() + 10); // 10 XP por aporte financiero
            userRepository.save(user);
        }

        SavingsMission updated = missionRepository.save(mission);
        return mapToResponse(updated);
    }

    // ==================== MÉTODOS CON CONSULTAS JPQL ====================

    @Override
    @Transactional(readOnly = true)
    public List<SavingsMissionResponse> getMissionsByCompletionStatus(Boolean isCompleted, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return missionRepository.findByUserIdAndIsCompleted(user.getId(), isCompleted)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingsMissionResponse> getMissionsNearDeadline(int daysAhead, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        LocalDate today = LocalDate.now();
        LocalDate deadlineLimit = today.plusDays(daysAhead);

        return missionRepository.findMissionsNearDeadline(user.getId(), today, deadlineLimit)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingsMissionResponse> getMissionsWithSavingsAbove(BigDecimal threshold, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return missionRepository.findMissionsWithSavingsAbove(user.getId(), threshold)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SavingsMissionResponse mapToResponse(SavingsMission mission) {
        return SavingsMissionResponse.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .targetAmount(mission.getTargetAmount())
                .currentAmount(mission.getCurrentAmount())
                .progressPercentage(mission.getProgressPercentage())
                .deadline(mission.getDeadline())
                .isCompleted(mission.getIsCompleted())
                .icon(mission.getIcon())
                .createdAt(mission.getCreatedAt())
                .build();
    }
}
