package domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CartItem {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Coffee coffee;

    private int quantity;

    @ManyToOne
    private User user;

    public CartItem () {};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coffee getCoffee() {
        return coffee;
    }

    public void setCoffee(Coffee coffee) {
        this.coffee = coffee;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

