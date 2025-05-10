package shared.dto;

import domain.Coffee;

public record CoffeeDTO(Integer id, String name, String description, double price) {
    public static CoffeeDTO of(Coffee c) {
        return new CoffeeDTO(Math.toIntExact(c.getId()), c.getName(), c.getDescription(), c.getPrice());
    }
}
