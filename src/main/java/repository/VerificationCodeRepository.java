package repository;

import domain.User;
import domain.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {

    Optional<VerificationCode> findByUser(User user);
}
