package engenharia.de.software.poker.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserChangePasswordRequest(

        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(min = 7)
        String password
) {}
