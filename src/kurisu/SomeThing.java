package kurisu;


/**
 * @author MakiseKurisu
 * @date 2018-12-24 19:43
 */
public class SomeThing {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private boolean isSale;
    private String dealer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public boolean isSale() {
        return isSale;
    }

    public void setSale(boolean sale) {
        isSale = sale;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public SomeThing(Long id, String name, Double price, Integer quantity, boolean isSale, String dealer) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.isSale = isSale;
        this.dealer = dealer;
    }
}
