package com.ragbecca;

import java.math.BigDecimal;

public class Phone {
    private final int PHONEID;
    private String brand;
    private String type;
    private String description;
    private BigDecimal price;
    private int stock;

    public Phone(int phone_id, String brand, String type, String description, BigDecimal price, int stock) {
        this.PHONEID = phone_id;
        this.brand = brand;
        this.type = type;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public int getPhoneId() {
        return PHONEID;
    }

    public String getBrand() {
        return brand;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

}
