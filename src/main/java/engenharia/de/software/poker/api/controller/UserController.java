package engenharia.de.software.poker.api.controller;

import engenharia.de.software.poker.api.dto.user.UserCreateRequest;
import engenharia.de.software.poker.api.dto.user.UserResponse;
import engenharia.de.software.poker.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/poker")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/cadastro")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse userResponse = userService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userResponse);
    }

}
