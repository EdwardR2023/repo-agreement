package models;

//id,bondType,creditRating,quantity,price

import java.math.BigDecimal;
import java.math.BigInteger;

public class Bond {
    private final String id;
    private final String type;
    private final String creditRating;
    private final BigInteger quantity;
    private final BigDecimal price;

    public Bond(String id, String type, String creditRating, BigInteger quantity, BigDecimal price) {
        this.id = id;
        this.type = type;
        this.creditRating = creditRating;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters here
    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public String getCreditRating() {
        return creditRating;
    }
    public BigInteger getQuantity() {
        return quantity;
    }
    public BigDecimal getPrice() {
        return price;
    }


}

