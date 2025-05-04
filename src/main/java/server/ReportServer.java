package server;

import domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import repository.UserRepository;
import service.CartService;
import shared.dto.AddLineCmd;
import shared.dto.CartDTO;

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

 /*
 * @RequestMapping(value = "allMetStations", method = RequestMethod.GET)
	@PermitAll
	@CrossOrigin
	public String getMetStations(@RequestParam("time") int time) {
	    try {
	        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	        cal.add(Calendar.HOUR, -time);
	        Date dat = cal.getTime();
	        Set<Integer> ids = metRepo.findMetStationIdsWithDateTakenAfter(dat);
	        List<UserGroup> users = group.findUserGroupsByMetStationIds(ids);
	        ObjectMapper objectMapper = new ObjectMapper();
	        String jsonArray = objectMapper.writeValueAsString(users);
	        return jsonArray;
	    } catch (Exception t) {
	        return "An error occurred: " + t.getMessage();
	    }
	}
  */

    /** POST /api/cart/items  (body: { coffeeId, qty }) */
    @GetMapping
    public CartDTO view(@RequestParam Integer userId) {
        User user = users.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user"));
        return CartDTO.of(service.getActiveCart(user));
    }

    /* POST /api/cart/items?userId=7   body: { coffeeId, qty } */
    @PostMapping("/items")
    public CartDTO add(@RequestParam Integer userId,
                       @RequestBody AddLineCmd cmd) {
        User user = users.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user"));
        return CartDTO.of(service.addItem(user, cmd.coffeeId(), cmd.qty()));
    }

}
