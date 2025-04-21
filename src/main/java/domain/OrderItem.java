package domain;

import jakarta.persistence.*;

@Entity
public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id") // face legătura cu OrderEntity
    private OrderEntity order;

    @ManyToOne
    private Coffee coffee;

    private int quantity;
    private double price;

    public OrderItem() {};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public OrderEntity getOrder() {
//        return orderEntity;
//    }

//    public void setOrder(OrderEntity orderEntity) {
//        this.orderEntity = orderEntity;
//    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

