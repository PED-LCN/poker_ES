package engenharia.de.software.poker.api.service;

import engenharia.de.software.poker.api.dto.user.UserCreateRequest;
import engenharia.de.software.poker.api.dto.user.UserResponse;
import engenharia.de.software.poker.api.model.entity.User;
import engenharia.de.software.poker.api.model.enums.UserRole;
import engenharia.de.software.poker.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse create(UserCreateRequest  userCreateRequest) {
        if(userRepository.findByUserName(userCreateRequest.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já cadastrado");
        }
        if(userRepository.findByEmail(userCreateRequest.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        User user = User.builder()
                .userName(userCreateRequest.username())
                .password(passwordEncoder.encode(userCreateRequest.password()))
                .email(userCreateRequest.email())
                .role(UserRole.PLAYER)
                .build();
        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getUserName(),
                saved.getEmail(),
                saved.getRole()
        );
    }
    
}
