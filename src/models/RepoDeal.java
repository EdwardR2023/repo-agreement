package models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a repo agreement with specific rating and bond type collateral
 * requirements. Tracks total value required, shortfall, borrow cost, and
 * fulfillment progress.
 */
public class RepoDeal {

    private final String id;
    private final BigDecimal totalValueRequired;
    private BigDecimal borrowCost = BigDecimal.ZERO;
    private BigDecimal shortfall;

    private final Map<String, BigDecimal> ratingRequirements;
    private final Map<String, BigDecimal> typeRequirements;

    private Map<String, BigDecimal> ratingFulfilled;
    private Map<String, BigDecimal> typeFulfilled;

    /**
     * Constructs a RepoDeal with given parameters and initializes fulfillment
     * maps to zero.
     *
     * @param id unique identifier for the deal
     * @param totalValueRequired the total value required to satisfy the repo
     * @param ratingRequirements map of minimum proportions by credit rating
     * (e.g. "AAA" → 0.5)
     * @param typeRequirements map of minimum proportions by bond type (e.g.
     * "Corporate" → 0.6)
     */
    public RepoDeal(
            String id,
            BigDecimal totalValueRequired,
            Map<String, BigDecimal> ratingRequirements,
            Map<String, BigDecimal> typeRequirements
    ) {
        this.id = id;
        this.totalValueRequired = totalValueRequired;
        this.shortfall = this.totalValueRequired;
        this.ratingRequirements = ratingRequirements;
        this.typeRequirements = typeRequirements;

        this.ratingFulfilled = new HashMap<>();
        for (String key : ratingRequirements.keySet()) {
            this.ratingFulfilled.put(key, BigDecimal.ZERO);
        }

        this.typeFulfilled = new HashMap<>();
        for (String key : typeRequirements.keySet()) {
            this.typeFulfilled.put(key, BigDecimal.ZERO);
        }
    }

    /**
     * Adds to the running total borrow cost.
     *
     * @param cost amount to add
     */
    public void addToBorrowCost(BigDecimal cost) {
        this.borrowCost = this.borrowCost.add(cost);
    }

    /**
     * Subtracts from the borrow cost (if needed for corrections).
     *
     * @param cost amount to subtract
     */
    public void subtractFromBorrowCost(BigDecimal cost) {
        this.borrowCost = this.borrowCost.subtract(cost);
    }

    /**
     * Subtracts a value from the remaining shortfall.
     *
     * @param value amount of collateral applied
     */
    public void subtractFromShortfall(BigDecimal value) {
        this.shortfall = this.shortfall.subtract(value);
    }

    /**
     * Checks whether the total shortfall has been fully satisfied.
     *
     * @return true if shortfall is zero or less, false otherwise
     */
    public boolean isFullySatisfied() {
        return shortfall.compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * Gets the required dollar value for a specific rating.
     *
     * @param rating the credit rating (e.g., "AAA")
     * @return required value for the rating
     */
    public BigDecimal getRequiredValueForRating(String rating) {
        return totalValueRequired.multiply(
                ratingRequirements.getOrDefault(rating, BigDecimal.ZERO)
        );
    }

    /**
     * Gets the required dollar value for a specific bond type.
     *
     * @param type the bond type (e.g., "Municipal")
     * @return required value for the type
     */
    public BigDecimal getRequiredValueForType(String type) {
        return totalValueRequired.multiply(
                typeRequirements.getOrDefault(type, BigDecimal.ZERO)
        );
    }

    /**
     * Validates that all rating and type requirement proportions are ≥ 0 and
     * their totals ≤ 1.
     *
     * @return true if all requirements are valid, false otherwise
     */
    public boolean isValidRequirements() {
        BigDecimal ratingSum = ratingRequirements.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal typeSum = typeRequirements.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean ratingsValid = ratingSum.compareTo(BigDecimal.ONE) <= 0
                && ratingRequirements.values().stream().allMatch(v -> v.compareTo(BigDecimal.ZERO) >= 0);

        boolean typesValid = typeSum.compareTo(BigDecimal.ONE) <= 0
                && typeRequirements.values().stream().allMatch(v -> v.compareTo(BigDecimal.ZERO) >= 0);

        return ratingsValid && typesValid;
    }

    /**
     * Throws an exception if the rating or type requirement maps are invalid.
     */
    public void validateRequirementsOrThrow() {
        if (!isValidRequirements()) {
            throw new IllegalArgumentException("Invalid rating/type requirement proportions in RepoDeal: " + id);
        }
    }

    // === Getters ===
    /**
     * @return the deal ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return the total value required for this repo deal
     */
    public BigDecimal getTotalValueRequired() {
        return totalValueRequired;
    }

    /**
     * @return the current accumulated borrow cost
     */
    public BigDecimal getBorrowCost() {
        return borrowCost;
    }

    /**
     * @return the current remaining shortfall value
     */
    public BigDecimal getShortfall() {
        return shortfall;
    }

    /**
     * @return the rating requirements map
     */
    public Map<String, BigDecimal> getRatingRequirements() {
        return ratingRequirements;
    }

    /**
     * @return the type requirements map
     */
    public Map<String, BigDecimal> getTypeRequirements() {
        return typeRequirements;
    }

    /**
     * @return the fulfilled values for each credit rating
     */
    public Map<String, BigDecimal> getRatingFulfilled() {
        return ratingFulfilled;
    }

    /**
     * @return the fulfilled values for each bond type
     */
    public Map<String, BigDecimal> getTypeFulfilled() {
        return typeFulfilled;
    }

    // === Setters ===
    /**
     * Sets the borrow cost (overrides current value).
     *
     * @param borrowCost new total borrow cost
     */
    public void setBorrowCost(BigDecimal borrowCost) {
        this.borrowCost = borrowCost;
    }

    /**
     * Sets the remaining shortfall.
     *
     * @param shortfall new shortfall value
     */
    public void setShortfall(BigDecimal shortfall) {
        this.shortfall = shortfall;
    }

    /**
     * Sets the entire rating fulfillment map.
     *
     * @param ratingFulfilled new fulfilled map
     */
    public void setRatingFulfilled(Map<String, BigDecimal> ratingFulfilled) {
        this.ratingFulfilled = ratingFulfilled;
    }

    /**
     * Sets the entire type fulfillment map.
     *
     * @param typeFulfilled new fulfilled map
     */
    public void setTypeFulfilled(Map<String, BigDecimal> typeFulfilled) {
        this.typeFulfilled = typeFulfilled;
    }

    /**
     * Adds a fulfilled amount to the specified credit rating bucket.
     *
     * @param rating credit rating label
     * @param value amount to add
     */
    public void addToRatingFulfilled(String rating, BigDecimal value) {
        this.ratingFulfilled.put(rating, this.ratingFulfilled.getOrDefault(rating, BigDecimal.ZERO).add(value));
    }

    /**
     * Adds a fulfilled amount to the specified bond type bucket.
     *
     * @param type bond type label
     * @param value amount to add
     */
    public void addToTypeFulfilled(String type, BigDecimal value) {
        this.typeFulfilled.put(type, this.typeFulfilled.getOrDefault(type, BigDecimal.ZERO).add(value));
    }

    /**
     * Returns a string representation of this RepoDeal for debugging or
     * logging.
     *
     * @return string with key fields
     */
    @Override
    public String toString() {
        return "RepoDeal{"
                + "id='" + id + '\''
                + ", totalValueRequired=" + totalValueRequired
                + ", shortfall=" + shortfall
                + ", ratingRequirements=" + ratingRequirements
                + ", typeRequirements=" + typeRequirements
                + ", borrowCost=" + borrowCost
                + ", ratingFulfilled=" + ratingFulfilled
                + ", typeFulfilled=" + typeFulfilled
                + '}';
    }
}
