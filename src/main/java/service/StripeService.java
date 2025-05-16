package service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import domain.Cart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class StripeService {

    @Value("${stripe.apiKey}")
    private String secretKey;

    public String createCheckoutSession(Cart cart) throws StripeException {
        Stripe.apiKey = secretKey;

        List<SessionCreateParams.LineItem> lines = cart.getItems().stream()
                .map(ci -> {
                    BigDecimal unit = BigDecimal.valueOf(ci.getCoffee().getPrice());
                    BigDecimal cents = unit.multiply(BigDecimal.valueOf(100));
                    return SessionCreateParams.LineItem.builder()
                            .setQuantity((long) ci.getQuantity())
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("ron")
                                    .setUnitAmountDecimal(cents)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(ci.getCoffee().getName())
                                            .build())
                                    .build())
                            .build();
                })
                .toList();

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lines)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/checkout/success")
                .setCancelUrl("http://localhost:3000/checkout/canceled")
                .build();

        Session session = Session.create(params);
        return session.getId();
    }
}