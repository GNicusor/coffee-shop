package shared.dto;

public class StockDTO {
    private Long id;
    private int stock;

//    public StockDTO() {}

    public StockDTO(Long id, int stock) {
        this.id = id;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public int getStock() {
        return stock;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
