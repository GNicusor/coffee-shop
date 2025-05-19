package server;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    // DTO for incoming items
    public static class CartItem {
        public Long   coffeeId;
        public String name;
        public Long   unitPrice;
        public Long   quantity;
    }

    public static class CreateSessionRequest {
        public List<CartItem> items;
    }

    @PostMapping("/create-session")
    public Map<String, String> createSession(@RequestBody CreateSessionRequest req) throws Exception {
        Stripe.apiKey = stripeApiKey;

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/checkout?success=true")
                .setCancelUrl("http://localhost:3000/checkout?canceled=true");

        for (CartItem item : req.items) {
            builder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(item.quantity)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("ron")
                                            .setUnitAmount(item.unitPrice * 100)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(item.name)
                                                            .putMetadata("coffeeId", item.coffeeId.toString())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }

        Session session = Session.create(builder.build());

        Map<String, String> response = new HashMap<>();
        response.put("id", session.getId());
        return response;
    }
}
