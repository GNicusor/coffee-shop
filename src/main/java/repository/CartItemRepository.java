package repository;

import domain.Cart;
import domain.CartItem;
import domain.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndCoffee(Cart cart, Coffee coffee);
}
