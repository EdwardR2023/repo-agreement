package core;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import models.PossibleBorrowedBond;
import models.RepoDeal;

public class AllocationEngine {

   public static BigDecimal calculateExternalBorrowCost(
        RepoDeal deal,
        List<PossibleBorrowedBond> borrowMarket) {

    BigDecimal totalRequired = deal.getTotalValueRequired();
    BigDecimal totalCost = BigDecimal.ZERO;

    // 1. Sort borrow market by rate (cheapest first)
    List<PossibleBorrowedBond> sortedMarket = borrowMarket.stream()
        .sorted(Comparator.comparing(PossibleBorrowedBond::getBorrowRate))
        .toList();

    // 2. Fulfill rating constraints
    for (Map.Entry<String, BigDecimal> entry : deal.getRatingRequirements().entrySet()) {
        String requiredRating = entry.getKey();
        BigDecimal requiredPercentage = entry.getValue();
        BigDecimal requiredAmount = totalRequired.multiply(requiredPercentage).divide(BigDecimal.valueOf(100));

        PossibleBorrowedBond match = sortedMarket.stream()
            .filter(b -> b.getCreditRating().equalsIgnoreCase(requiredRating))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No bond found for rating: " + requiredRating));

        BigDecimal rateDecimal = match.getBorrowRate().divide(BigDecimal.valueOf(100));
        BigDecimal cost = rateDecimal.multiply(requiredAmount);
        totalCost = totalCost.add(cost);
    }

    // 3. Fulfill type constraints
    for (Map.Entry<String, BigDecimal> entry : deal.getTypeRequirements().entrySet()) {
        String requiredType = entry.getKey();
        BigDecimal requiredPercentage = entry.getValue();
        BigDecimal requiredAmount = totalRequired.multiply(requiredPercentage).divide(BigDecimal.valueOf(100));

        PossibleBorrowedBond match = sortedMarket.stream()
            .filter(b -> b.getBondType().equalsIgnoreCase(requiredType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No bond found for type: " + requiredType));

        BigDecimal rateDecimal = match.getBorrowRate().divide(BigDecimal.valueOf(100));
        BigDecimal cost = rateDecimal.multiply(requiredAmount);
        totalCost = totalCost.add(cost);
    }

    // 4. Fill any remaining value with the cheapest bond
    BigDecimal totalAllocated = calculateTotalAllocated(deal);
    BigDecimal remaining = BigDecimal.ONE.subtract(totalAllocated.divide(BigDecimal.valueOf(100)))
        .multiply(totalRequired);

    if (remaining.compareTo(BigDecimal.ZERO) > 0) {
        PossibleBorrowedBond cheapest = sortedMarket.get(0);
        BigDecimal rateDecimal = cheapest.getBorrowRate().divide(BigDecimal.valueOf(100));
        BigDecimal cost = rateDecimal.multiply(remaining);
        totalCost = totalCost.add(cost);
    }

    return totalCost;
}

// Helper to calculate total allocation percentage (0 to 100)
private static BigDecimal calculateTotalAllocated(RepoDeal deal) {
    BigDecimal sum = BigDecimal.ZERO;
    for (BigDecimal v : deal.getRatingRequirements().values()) {
        sum = sum.add(v);
    }
    for (BigDecimal v : deal.getTypeRequirements().values()) {
        sum = sum.add(v);
    }
    return sum;
}

    
}
