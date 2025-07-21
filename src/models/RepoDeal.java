package models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

//id,counterparty,requiredValue
//min rating requirements: e.g., "AAA" -> 0.9
//min type requirements: e.g., "Municipal" -> 0.5
public class RepoDeal {

    private final String id;
    private final BigDecimal totalValueRequired;
    private BigDecimal borrowCost = BigDecimal.ZERO;

    private final Map<String, BigDecimal> ratingRequirements;
    private final Map<String, BigDecimal> typeRequirements;

    private Map<String, BigDecimal> ratingFulfilled;
    private Map<String, BigDecimal> typeFulfilled;

    public RepoDeal(
            String id,
            BigDecimal totalValueRequired,
            Map<String, BigDecimal> ratingRequirements,
            Map<String, BigDecimal> typeRequirements
    ) {
        this.id = id;
        this.totalValueRequired = totalValueRequired;
        this.ratingRequirements = ratingRequirements;
        this.typeRequirements = typeRequirements;

        // Initialize fulfilled maps with zero values
        this.ratingFulfilled = new HashMap<>();
        for (String key : ratingRequirements.keySet()) {
            this.ratingFulfilled.put(key, BigDecimal.ZERO);
        }

        this.typeFulfilled = new HashMap<>();
        for (String key : typeRequirements.keySet()) {
            this.typeFulfilled.put(key, BigDecimal.ZERO);
        }

    }

    public void addToBorrowCost(BigDecimal cost) {
        this.borrowCost = this.borrowCost.add(cost);
    }

    public void subtractFromBorrowCost(BigDecimal cost) {
        this.borrowCost = this.borrowCost.subtract(cost);
    }

    public BigDecimal getRequiredValueForRating(String rating) {
        return totalValueRequired.multiply(
                ratingRequirements.getOrDefault(rating, BigDecimal.ZERO)
        );

    }

    public BigDecimal getRequiredValueForType(String type) {
        return totalValueRequired.multiply(
                typeRequirements.getOrDefault(type, BigDecimal.ZERO)
        );
    }

    // Getters
    public String getId() {
        return id;
    }

    public BigDecimal getTotalValueRequired() {
        return totalValueRequired;
    }

    public BigDecimal getBorrowCost() {
        return borrowCost;
    }

    public Map<String, BigDecimal> getRatingRequirements() {
        return ratingRequirements;
    }

    public Map<String, BigDecimal> getTypeRequirements() {
        return typeRequirements;
    }

    //setter
    public void setBorrowCost(BigDecimal borrowCost) {
        this.borrowCost = borrowCost;
    }

    @Override
    public String toString() {
        return "RepoDeal{"
                + "id='" + id + '\''
                + ", totalValueRequired=" + totalValueRequired
                + ", ratingRequirements=" + ratingRequirements
                + ", typeRequirements=" + typeRequirements
                + ", borrowCost=" + borrowCost
                + ", ratingFulfilled=" + ratingFulfilled
                + ", typeFulfilled=" + typeFulfilled
                + '}';
    }

}
