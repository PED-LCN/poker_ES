package engenharia.de.software.poker.api.dto.user;

import engenharia.de.software.poker.api.model.UserRole;

public record UserResponse(
        Long id,
        String userName,
        String email,
        UserRole role
){}
