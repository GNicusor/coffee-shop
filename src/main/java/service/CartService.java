package service;

import domain.Cart;
import domain.CartItem;
import domain.Coffee;
import domain.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repository.CartItemRepository;
import repository.CartRepository;
import repository.CoffeeRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class CartService {

    private final CartRepository carts;
    private final CartItemRepository items;
    private final CoffeeRepository coffees;

    public Cart getActiveCart(User user) {
        return carts.findByUserAndStatus(user, Cart.Status.ACTIVE)
                .orElseGet(() -> carts.save(
                        Cart.builder().user(user).build()));
    }

    //THIS FUNCTION USED TO ADD ITEM TO THE CART (or delete items from the cart) {:P
    public Cart addItem(User user, Long coffeeId, int deltaQty) {
        Cart cart = getActiveCart(user);
        Coffee coffee = coffees.findById(coffeeId)
                .orElseThrow(() -> new EntityNotFoundException("Coffee"));

        CartItem line = items.findByCartAndCoffee(cart, coffee)
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .coffee(coffee)
                        .quantity(0)
                        .build());

        int newQty = line.getQuantity() + deltaQty;

        if (newQty > 0) {
            line.setQuantity(newQty);
        } else {
            cart.getItems().remove(line);
        }
        return cart;
    }

    public void clearCart(User user) {
        Cart cart = getActiveCart(user);
        cart.getItems().clear();
    }
}

