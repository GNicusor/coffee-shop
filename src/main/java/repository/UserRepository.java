package repository;

import domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    UserRepository findByUsername(String username);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);
}
