package shared.dto;

import domain.CartItem;

import java.math.BigDecimal;
import java.util.UUID;

/** One row inside the basket. */
public class CartLineDTO {

    private long coffeeId;
    private String name;           // nice for the UI
    private int qty;
    private BigDecimal unitPrice;  // optional
    private BigDecimal lineTotal;  // qty × unitPrice

    public CartLineDTO() { }

    public CartLineDTO(long coffeeId, String name,
                       int qty, BigDecimal unitPrice, BigDecimal lineTotal) {
        this.coffeeId   = coffeeId;
        this.name       = name;
        this.qty        = qty;
        this.unitPrice  = unitPrice;
        this.lineTotal  = lineTotal;
    }

    public static CartLineDTO of(CartItem item) {
        var coffee = item.getCoffee();
        BigDecimal unit = BigDecimal.valueOf(coffee.getPrice());                 // assume getter
        BigDecimal total = unit.multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartLineDTO(
                coffee.getId(),
                coffee.getName(),
                item.getQuantity(),
                unit,
                total
        );
    }
}
