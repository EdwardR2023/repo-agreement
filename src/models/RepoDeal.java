package models;

import java.math.BigDecimal;
import java.util.Map;

//id,counterparty,requiredValue
//min rating requirements: e.g., "AAA" -> 0.9
//min type requirements: e.g., "Municipal" -> 0.5
public class RepoDeal {
    private final String id;
    private final BigDecimal totalValueRequired;

    private final Map<String, BigDecimal> ratingRequirements;

    private final Map<String, BigDecimal> typeRequirements;

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
    }

    // Getters

    public String getId() {
        return id;
    }

    public BigDecimal getTotalValueRequired() {
        return totalValueRequired;
    }

    public Map<String, BigDecimal> getRatingRequirements() {
        return ratingRequirements;
    }

    public Map<String, BigDecimal> getTypeRequirements() {
        return typeRequirements;
    }
}
