package server;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.OrderEntityRepository;
import repository.UserRepository;
import service.CartService;
import service.OrderService;
import service.StripeService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private OrderEntityRepository orderRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository users;

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    @PostMapping("/create-session")
    public Map<String, String> createSession(@RequestBody Map<String, Object> payload) throws Exception {
        Stripe.apiKey = stripeApiKey;

        final OrderService orderService = new OrderService(cartService,orderRepo, stripeService, users);

        Object u = payload.get("userId");
        if (u == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing userId")).getBody();
        }

        long userId;
        try {
            userId = Long.parseLong(u.toString());
        } catch (NumberFormatException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid userId format")).getBody();
        }


        Long amount = ((Number) payload.get("amount")).longValue();
//        Long amount = ((Number)payload.get("amount")).longValue();
//
//        SessionCreateParams params =
//                SessionCreateParams.builder()
//                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
//                        .setMode(SessionCreateParams.Mode.PAYMENT)
//                        .setSuccessUrl("http://localhost:3000/checkout?success=true")
//                        .setCancelUrl("http://localhost:3000/checkout?canceled=true")
//                        .addLineItem(
//                                SessionCreateParams.LineItem.builder()
//                                        .setQuantity(1L)
//                                        .setPriceData(
//                                                SessionCreateParams.LineItem.PriceData.builder()
//                                                        .setCurrency("ron")
//                                                        .setUnitAmount(amount*100)
//                                                        .setProductData(
//                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                                                        .setName("Coffee Order")
//                                                                        .build()
//                                                        )
//                                                        .build()
//                                        )
//                                        .build()
//                        )
//                        .build();
//        Session session = Session.create(params);
//        Map<String,String> response = new HashMap<>();

        String sessionId = orderService.startCheckout(userId, amount);

        // 2) Return it to your React app
        return Map.of("id", sessionId);
    }
}
