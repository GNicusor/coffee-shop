package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import domain.OrderEntity;
import domain.User;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.CoffeeRepository;
import repository.OrderEntityRepository;
import repository.UserRepository;
import service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

/*
const { sessionId } = await api.post("/api/checkout?userId=1");
const stripe = await loadStripe(PUBLISHABLE_KEY);
stripe.redirectToCheckout({ sessionId });
* */
@RestController
@RequestMapping("/api/checkout")
@CrossOrigin
@RequiredArgsConstructor
public class CheckoutController {

    private final OrderEntityRepository orderRepository;
    private final OrderService  orders;
    private final UserRepository users;
    @Value("${stripe.webhook.secret}")
    String webhookSecret;

    /** POST /api/checkout?userId=1  body empty */
//    @PostMapping
//    public Map<String, String> start(@RequestParam Long userId) throws StripeException {
//        User user = users.findById(userId)              // lookup
//                .orElseThrow(() ->
//                        new EntityNotFoundException("User " + userId));
//        String sessionId = orders.startCheckout(userId,  );
//        return Map.of("sessionId", sessionId);   // FE redirects to Stripe
//    }

    @PostMapping(path = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> stripeWebhook(HttpServletRequest request,
                                                @RequestHeader("Stripe-Signature") String sig) throws IOException, StripeException {
        String payload = request.getReader().lines().collect(Collectors.joining("\n"));

        Event event;
        try {
            event = Webhook.constructEvent(payload, sig, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            // Use Jackson to parse the raw JSON payload
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);

            // Navigate to data.object.id
            String sessionId = root
                    .path("data")
                    .path("object")
                    .path("id")
                    .asText(null);

            if (sessionId == null) {
                // Malformed; bail out
                return ResponseEntity.badRequest().body("Missing session ID");
            }

            // Now retrieve the fully populated Session from Stripe
            Stripe.apiKey = "sk_test_51RNBGm4Tagxb8m7TfuClmLCVHVozSA7IfHf9NiTkwNwUumUrTnwLoJ27Ofqfxal2M8iWOYtHOYs2VqjWJpt2Ritr00MLFoJtC6";
            Session session = Session.retrieve(sessionId);

            // Pull out your orderId from metadata
            String orderIdStr = session.getMetadata().get("orderId");
            Long orderId = Long.valueOf(orderIdStr);

            // Load and finalize the order
            OrderEntity order = orderRepository.findById(Math.toIntExact(orderId))
                    .orElseThrow(() -> new EntityNotFoundException("Order " + orderId));
            order.setStatus(OrderEntity.Status.PAID);
            orderRepository.save(order);

            // Optionally clear the cart, etc.
            //cartService.clearCart(order.getUser());
        }

        return ResponseEntity.ok("{\"received\":true}");
    }


}