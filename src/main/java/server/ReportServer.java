package server;

import domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import repository.CoffeeRepository;
import repository.UserRepository;
import service.CartService;
import shared.dto.AddLineCmd;
import shared.dto.CartDTO;
import shared.dto.CoffeeDTO;

import java.util.List;

//class where all the requests will be processed
//in this class , we gonna gather all the information
@RestController
@RequestMapping(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class ReportServer {

    @Autowired
    private CartService service;

    @Autowired
    private UserRepository users;

    @Autowired
    private CoffeeRepository coffeeRepo;

    /** POST /api/cart/items  (body: { coffeeId, qty }) */
    @GetMapping("/view")
    public CartDTO view(@RequestParam("userId") Long userId) {   // ← explicit
        User user = users.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user"));
        return CartDTO.of(service.getActiveCart(user));
    }

    /* POST /api/cart/items?userId=7   body: { coffeeId, qty } */
    //modified, now you can pass -1 and i will remove that item from the cart (pls pay attention cause it will crash the server
    //if you pass like -999 or some shit
    @PostMapping("/items")
    public CartDTO add(@RequestParam("userId") Long userId,
                       @RequestBody AddLineCmd cmd) {
        User user = users.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user"));
        return CartDTO.of(service.addItem(user, cmd.coffeeId(), cmd.qty()));
    }

    @GetMapping("/coffees")
    public List<CoffeeDTO> getAllCoffees() {
        return coffeeRepo.findAll().stream()
                .map(CoffeeDTO::of)
                .toList();
    }

    @GetMapping("/users")
    public List<User> listUsers() {
        return users.findAll();
    }

    @GetMapping("/user")
    public User getUser(@RequestParam("id") Long id) {
        return users.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User " + id));
    }

    @PostMapping("/clear")
    public CartDTO clear(
            @RequestParam("userId") Long userId
    ) {
        User user = users.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user"));
        service.clearCart(user);
        return CartDTO.of(service.getActiveCart(user));
    }

}
