package models;

//id,bondType,creditRating,borrowRate

import java.math.BigDecimal;

public class BorrowBond {
    private final String id;
    private final String type;
    private final String creditRating;
    private final BigDecimal borrowRate;

    public BorrowBond(String id, String type, String creditRating, BigDecimal borrowRate) {
        this.id = id;
        this.type = type;
        this.creditRating = creditRating;
        this.borrowRate = borrowRate;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCreditRating() {
        return creditRating;
    }

    public BigDecimal getBorrowRate() {
        return borrowRate;
    }

    
}