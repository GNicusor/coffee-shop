// server/AuthController.java
package server;

import domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AuthService;
import shared.LoginRequest;
import shared.RegisterRequest;
import shared.VerifyCodeRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verify(@RequestBody VerifyCodeRequest request) {
        return authService.verifyCode(request);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRequest.LoginResponse> login(@RequestBody LoginRequest request) {
        User user = authService.authenticate(request);
        return ResponseEntity.ok(
                new LoginRequest.LoginResponse(user.getId(), "Login successful!")
        );
    }
}
