public class Product extends Adv {
    public enum ProductState {
        NEW,
        LIKE_NEW,
        GOOD,
        FAIR,
        DAMAGED,
        REFURBISHED
    }

    private ProductState stateOfProduct;
    private String brand;
    private String model;
    private String constructor;  // سازنده
    private Category category;
    private long price;          // قیمت به تومان

    public Product() {
        super();
        this.setAdvType(AdvType.PRODUCT);
    }

    public Product(String description, User user, String fullName, City city,
                   ProductState stateOfProduct, String brand, String model,
                   String constructor, Category category, long price) {
        super(description, AdvType.PRODUCT, user, fullName, city);
        this.stateOfProduct = stateOfProduct;
        this.brand = brand;
        this.model = model;
        this.constructor = constructor;
        this.category = category;
        this.price = price;
    }

    // Getter و Setter
    public ProductState getStateOfProduct() { return stateOfProduct; }
    public void setStateOfProduct(ProductState stateOfProduct) { this.stateOfProduct = stateOfProduct; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getConstructor() { return constructor; }
    public void setConstructor(String constructor) { this.constructor = constructor; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", brand='" + brand + '\'' +
                ", stateOfProduct=" + stateOfProduct +
                ", status=" + getStatus() +
                '}';
    }
}