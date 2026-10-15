package pe.edu.utp.salviafinanzas.security.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.salviafinanzas.model.User;
import pe.edu.utp.salviafinanzas.repository.UserRepository;

/**
 * Servicio personalizado UserDetailsService
 * Recupera el usuario desde la base de datos para autenticación en Spring Security.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con identificador: " + usernameOrEmail));

        return UserDetailsImpl.build(user);
    }
}
