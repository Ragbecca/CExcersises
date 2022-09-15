public class Phone {
    private final int phone_id;
    private String brand;
    private String type;
    private String description;
    private double price;
    private int stock;

    public Phone(int phone_id, String brand, String type, String description, double price, int stock) {
        this.phone_id = phone_id;
        this.brand = brand;
        this.type = type;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
