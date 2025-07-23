package core;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
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
    BigDecimal remainingToFill = totalRequired;

    // Clone the requirement maps so we can modify them safely
    Map<String, BigDecimal> ratingLeft = new HashMap<>(deal.getRatingRequirements());
    Map<String, BigDecimal> typeLeft = new HashMap<>(deal.getTypeRequirements());

    // Convert percentages to dollar values
    ratingLeft.replaceAll((k, v) -> totalRequired.multiply(v).divide(BigDecimal.valueOf(100)));
    typeLeft.replaceAll((k, v) -> totalRequired.multiply(v).divide(BigDecimal.valueOf(100)));

    // Sort borrow market by cheapest rate
    List<PossibleBorrowedBond> sortedMarket = borrowMarket.stream()
        .sorted(Comparator.comparing(PossibleBorrowedBond::getBorrowRate))
        .toList();

    for (PossibleBorrowedBond bond : sortedMarket) {
        if (remainingToFill.compareTo(BigDecimal.ZERO) <= 0) break;

        String rating = bond.getCreditRating();
        String type = bond.getBondType();

        BigDecimal rateDecimal = bond.getBorrowRate().divide(BigDecimal.valueOf(100));
        BigDecimal maxAssignable = remainingToFill;

        // Determine how much of this bond can help with rating and type constraints
        BigDecimal amountForRating = ratingLeft.getOrDefault(rating, BigDecimal.ZERO);
        BigDecimal amountForType = typeLeft.getOrDefault(type, BigDecimal.ZERO);

        // Take the max of the two — if it satisfies both, we use one amount to satisfy both
        BigDecimal assignable = amountForRating.max(amountForType).min(maxAssignable);
        if (assignable.compareTo(BigDecimal.ZERO) > 0) {
            // Apply to both maps if applicable
            if (amountForRating.compareTo(BigDecimal.ZERO) > 0)
                ratingLeft.put(rating, amountForRating.subtract(assignable).max(BigDecimal.ZERO));
            if (amountForType.compareTo(BigDecimal.ZERO) > 0)
                typeLeft.put(type, amountForType.subtract(assignable).max(BigDecimal.ZERO));

            totalCost = totalCost.add(rateDecimal.multiply(assignable));
            remainingToFill = remainingToFill.subtract(assignable);
        }
    }

    // If constraints are satisfied but some value is still left, fill remainder with cheapest
    if (remainingToFill.compareTo(BigDecimal.ZERO) > 0) {
        PossibleBorrowedBond cheapest = sortedMarket.get(0);
        BigDecimal rateDecimal = cheapest.getBorrowRate().divide(BigDecimal.valueOf(100));
        totalCost = totalCost.add(rateDecimal.multiply(remainingToFill));
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
