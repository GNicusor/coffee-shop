package domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id","coffee_id"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CartItem {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Coffee coffee;

    private int quantity;

    @ManyToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

}

