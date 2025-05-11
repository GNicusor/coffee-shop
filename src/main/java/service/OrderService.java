package service;

import com.stripe.exception.StripeException;
import domain.Cart;
import domain.OrderEntity;
import domain.OrderItem;
import domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.OrderEntityRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    @Autowired
    CartService cartService;

    @Autowired
    OrderEntityRepository orders;

    @Autowired
    StripeService stripe;

    public String startCheckout(User user) throws StripeException {

        Cart cart = cartService.getActiveCart(user);
        if (cart.getItems().isEmpty())
            throw new IllegalStateException("Cart is empty");

        /* ─── create Stripe CheckoutSession / PaymentIntent ───────────── */
        String stripeId = stripe.createCheckoutSession(cart);

        /* ─── snapshot cart → order rows ──────────────────────────────── */
        OrderEntity order = OrderEntity.builder()
                .user(user)
                .stripeId(stripeId)
                .status(OrderEntity.Status.PENDING)
                .build();

        cart.getItems().forEach(ci ->
                order.getItems().add(OrderItem.builder()
                        .order(order)
                        .coffee(ci.getCoffee())
                        .quantity(ci.getQuantity())
                        .price(ci.getCoffee().getPrice())
                        .build()));

        orders.save(order);
        return stripeId;      // frontend redirects to Stripe
    }

    public void handleStripeWebhook(String stripeId, boolean paid) {

        OrderEntity order = orders.findByStripeId(stripeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found for stripeId " + stripeId));

        order.setStatus(paid ? OrderEntity.Status.PAID : OrderEntity.Status.FAILED);
        if (paid) cartService.clearCart(order.getUser());
    }
}
