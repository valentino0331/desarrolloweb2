package pe.edu.utp.salviafinanzas.service;

import pe.edu.utp.salviafinanzas.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    List<UserResponse> getLeaderboard(Integer minPoints);
    void deleteUser(Long id);
}
