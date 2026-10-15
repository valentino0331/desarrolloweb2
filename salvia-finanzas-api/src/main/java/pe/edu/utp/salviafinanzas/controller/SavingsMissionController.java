package pe.edu.utp.salviafinanzas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.salviafinanzas.dto.request.MissionDepositRequest;
import pe.edu.utp.salviafinanzas.dto.request.SavingsMissionRequest;
import pe.edu.utp.salviafinanzas.dto.response.ApiResponse;
import pe.edu.utp.salviafinanzas.dto.response.SavingsMissionResponse;
import pe.edu.utp.salviafinanzas.service.SavingsMissionService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para la Gestión de Misiones de Ahorro y Gamificación Financiera
 * Endpoints protegidos mediante @PreAuthorize e integración de transacciones atómicas.
 */
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SavingsMissionController {

    private final SavingsMissionService missionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<SavingsMissionResponse> createMission(
            @Valid @RequestBody SavingsMissionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        SavingsMissionResponse response = missionService.createMission(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<SavingsMissionResponse>> getAllMissions(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SavingsMissionResponse> list = missionService.getAllMissions(userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<SavingsMissionResponse> getMissionById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        SavingsMissionResponse response = missionService.getMissionById(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<SavingsMissionResponse> updateMission(
            @PathVariable Long id,
            @Valid @RequestBody SavingsMissionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        SavingsMissionResponse response = missionService.updateMission(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteMission(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        missionService.deleteMission(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "Misión de ahorro eliminada correctamente con id: " + id));
    }

    /**
     * Operación Transaccional: Abono o depósito a la meta de ahorro
     */
    @PostMapping("/{id}/deposit")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<SavingsMissionResponse> depositToMission(
            @PathVariable Long id,
            @Valid @RequestBody MissionDepositRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        SavingsMissionResponse response = missionService.depositToMission(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // ==================== ENDPOINTS CON FILTROS JPQL ====================

    @GetMapping("/filter/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<SavingsMissionResponse>> getMissionsByStatus(
            @RequestParam Boolean completed,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SavingsMissionResponse> list = missionService.getMissionsByCompletionStatus(completed, userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/filter/near-deadline")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<SavingsMissionResponse>> getMissionsNearDeadline(
            @RequestParam(defaultValue = "30") int days,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SavingsMissionResponse> list = missionService.getMissionsNearDeadline(days, userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/filter/savings-above")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<SavingsMissionResponse>> getMissionsWithSavingsAbove(
            @RequestParam BigDecimal threshold,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SavingsMissionResponse> list = missionService.getMissionsWithSavingsAbove(threshold, userDetails.getUsername());
        return ResponseEntity.ok(list);
    }
}
