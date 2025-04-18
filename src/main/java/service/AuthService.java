package service;

import domain.User;
import domain.VerificationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import repository.VerificationCodeRepository;
import shared.LoginRequest;
import shared.RegisterRequest;
import shared.VerifyCodeRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<String> register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);
        userRepository.save(user);

        // generate 6-digit code
        String code = String.format("%06d", new Random().nextInt(999999));

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setCode(code);
        verificationCode.setExpiration(LocalDateTime.now().plusMinutes(10));
        verificationCodeRepository.save(verificationCode);

        // send email
        mailService.sendVerificationEmail(user.getEmail(), code);

        return ResponseEntity.ok("Registration successful. Check your Gmail for the verification code.");
    }

    public ResponseEntity<String> verifyCode(VerifyCodeRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = optionalUser.get();

        Optional<VerificationCode> codeOpt = verificationCodeRepository.findByUser(user);
        if (codeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No verification code found.");
        }

        VerificationCode code = codeOpt.get();

        if (!code.getCode().equals(request.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect verification code.");
        }

        if (code.getExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification code expired.");
        }

        // Mark user as verified
        user.setVerified(true);
        userRepository.save(user);

        // Delete used code
        verificationCodeRepository.delete(code);

        return ResponseEntity.ok("Email verified successfully. You can now log in.");
    }

    public ResponseEntity<String> login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }

        User user = optionalUser.get();

        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please verify your email before logging in.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }

        return ResponseEntity.ok("Login successful!");
    }

}
