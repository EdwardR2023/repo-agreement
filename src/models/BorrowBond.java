package models;

//id,bondType,creditRating,borrowRate

import java.math.BigDecimal;
import java.math.BigInteger;

public class BorrowBond extends Bond {
    private final BigDecimal borrowRate;



    public BorrowBond(String id, String type, String creditRating, BigInteger quantity, BigDecimal price, BigDecimal borrowRate) {
        super(id, type, creditRating, quantity, price);
        this.borrowRate = borrowRate;
    }

    // Getters
    public BigDecimal getBorrowRate() {
        return borrowRate;
    }

    @Override
    public String toString() {
        return "BorrowBond{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", creditRating='" + getCreditRating() + '\'' +
                ", quantity=" + getQuantity() +
                ", price=" + getPrice() +
                ", borrowRate=" + borrowRate +
                '}';
    }

}
