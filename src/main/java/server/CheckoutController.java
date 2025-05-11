package server;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import domain.User;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import repository.UserRepository;
import service.OrderService;

import java.util.Map;
import java.util.stream.Collectors;

//FOR MY DARLING VICU ONLY:()
//aici sunt endpointurile prin care tu vei vorbi cand incepe user-ul sa porneasca purchase
//vezi ca m-am luat dupa tine si la stripeservice cand e succesfully am pus linkurile tale de la paymentcontroller
//probabil am facut acelasi lucru (payment/stripe service dar mergi pe mana mea)
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

    private final OrderService   orders;
    private final UserRepository users;
    @Value("${stripe.webhook.secret}")
    String webhookSecret;

    /** POST /api/checkout?userId=1  body empty */
    @PostMapping
    public Map<String, String> start(@RequestParam Long userId) throws StripeException {
        User user = users.findById(userId)              // lookup
                .orElseThrow(() ->
                        new EntityNotFoundException("User " + userId));
        String sessionId = orders.startCheckout(user);
        return Map.of("sessionId", sessionId);   // FE redirects to Stripe
    }

    /** Stripe webhooks POST here */
    @PostMapping("/webhook")
    public void stripeWebhook(HttpServletRequest request,
                              @RequestHeader("Stripe-Signature") String sig)
            throws Exception {

        String payload = request.getReader().lines().collect(Collectors.joining());
        Event event = Webhook.constructEvent(payload, sig, webhookSecret);

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject().orElseThrow();
            orders.handleStripeWebhook(session.getId(), true);
        } else if ("payment_intent.payment_failed".equals(event.getType())) {
            PaymentIntent pi = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject().orElseThrow();
            orders.handleStripeWebhook(pi.getId(), false);
        }
    }
}