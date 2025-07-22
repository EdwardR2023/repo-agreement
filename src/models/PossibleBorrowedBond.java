package models;

import java.math.BigDecimal;

public class PossibleBorrowedBond {
    private final String id;
    private final String bondType;
    private final String creditRating;
    private final BigDecimal borrowRate;

    public PossibleBorrowedBond(String id, String bondType, String creditRating, BigDecimal borrowRate) {
        this.id = id;
        this.bondType = bondType;
        this.creditRating = creditRating;
        this.borrowRate = borrowRate;
    }

    public String getId() {
        return id;
    }

    public String getBondType() {
        return bondType;
    }

    public String getCreditRating() {
        return creditRating;
    }

    public BigDecimal getBorrowRate() {
        return borrowRate;
    }

    @Override
    public String toString() {
        return "BorrowBond{" +
                "id='" + id + '\'' +
                ", bondType='" + bondType + '\'' +
                ", creditRating='" + creditRating + '\'' +
                ", borrowRate=" + borrowRate +
                '}';
    }
}

