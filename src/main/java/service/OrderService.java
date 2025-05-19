package service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import domain.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.OrderEntityRepository;
import repository.UserRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final CartService cartService;
    private final OrderEntityRepository orders;
    private final StripeService stripe;
    private final UserRepository users;

    @Transactional
    public String startCheckout(Long userId, long amount) throws StripeException {
        // 1) Load the user + cart
        User user = users.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId));
        Cart cart = cartService.getActiveCart(user);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setStatus(OrderEntity.Status.PENDING);
        order.setItems(new ArrayList<>());  // ensure not null

        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setCoffee(ci.getCoffee());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getCoffee().getPrice());
            order.getItems().add(oi);
        }

        // 3) Persist to get a generated order.getId()
        order = orders.save(order);

        // 4) Create Stripe CheckoutSession with metadata.orderId
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/checkout?success=true")
                .setCancelUrl("http://localhost:3000/checkout?canceled=true")
                .putMetadata("orderId", order.getId().toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("ron")
                                                .setUnitAmount(amount * 100)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Coffee Order")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Stripe.apiKey = "sk_test_51RNBGm4Tagxb8m7TfuClmLCVHVozSA7IfHf9NiTkwNwUumUrTnwLoJ27Ofqfxal2M8iWOYtHOYs2VqjWJpt2Ritr00MLFoJtC6";
        Session session = Session.create(params);

        // 5) Persist the stripeId on the same order
        order.setStripeId(session.getId());
        orders.save(order);

        return session.getId();
    }

    public void handleStripeWebhook(String stripeId, boolean paid) {

        OrderEntity order = orders.findByStripeId(stripeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found for stripeId " + stripeId));

        order.setStatus(paid ? OrderEntity.Status.PAID : OrderEntity.Status.FAILED);
        if (paid) cartService.clearCart(order.getUser());
    }
}
