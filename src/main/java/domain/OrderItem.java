package domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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



}

