package repository;

import domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, Integer> {

    Optional<OrderEntity> findByStripeId(String stripeId);

}
