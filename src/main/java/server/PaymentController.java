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
        String sessionId = orderService.startCheckout(userId, amount);

        return Map.of("id", sessionId);
    }
}
