package repository;

import domain.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {

    Optional<Coffee> findByName(String name);
}

