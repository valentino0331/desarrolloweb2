package pe.edu.utp.salviafinanzas.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.salviafinanzas.dto.response.UserResponse;
import pe.edu.utp.salviafinanzas.exception.ResourceNotFoundException;
import pe.edu.utp.salviafinanzas.model.User;
import pe.edu.utp.salviafinanzas.repository.UserRepository;
import pe.edu.utp.salviafinanzas.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getLeaderboard(Integer minPoints) {
        int threshold = minPoints != null ? minPoints : 0;
        return userRepository.findLeaderboardUsers(threshold).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {
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
