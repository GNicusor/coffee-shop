package server;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    @PostMapping("/create-session")
    public Map<String, String> createSession(@RequestBody Map<String, Object> payload) throws Exception {
        Stripe.apiKey = stripeApiKey;

        Long amount = ((Number)payload.get("amount")).longValue();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:3000/checkout?success=true")
                        .setCancelUrl("http://localhost:3000/checkout?canceled=true")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("ron")
                                                        .setUnitAmount(amount)
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

        Session session = Session.create(params);
        Map<String,String> response = new HashMap<>();
        response.put("id", session.getId());
        return response;
    }
}
