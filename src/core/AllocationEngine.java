package core;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import models.PossibleBorrowedBond;
import models.RepoDeal;

public class AllocationEngine {

    public static BigDecimal calculateExternalBorrowCost(RepoDeal deal, List<PossibleBorrowedBond> borrowMarket) {
        try {
            return calculateLowToHighRatingStrategy(deal, borrowMarket);
        } catch (IllegalStateException e) {
            System.out.println("Primary strategy failed for deal " + deal.getId() + ": " + e.getMessage());
            System.out.println("Falling back to alternate strategy...");
            return calculateFallbackStrategy(deal, borrowMarket);
        }

    }

    //still has issues with deals that have too many constraints like deal 5.
    //this works for all other deals but needs more testing
    //will need to create a new strategy for those cases
    //or improve this one to handle more complex constraints
    private static BigDecimal calculateLowToHighRatingStrategy(RepoDeal deal, List<PossibleBorrowedBond> borrowMarket) {
        // Your current low-to-high credit rating implementation goes here.
        // It must throw UnfulfillableConstraintException if any constraint is unmet and total value is capped.
        BigDecimal totalRequired = deal.getTotalValueRequired();
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal remaining = totalRequired;

        // Track allocations for debugging
        List<Allocation> allocations = new ArrayList<>();

        // Prepare mutable requirement maps with values in dollars
        Map<String, BigDecimal> ratingLeft = deal.getRatingRequirements().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> totalRequired.multiply(e.getValue()).divide(BigDecimal.valueOf(100))
                ));

        Map<String, BigDecimal> typeLeft = deal.getTypeRequirements().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> totalRequired.multiply(e.getValue()).divide(BigDecimal.valueOf(100))
                ));

        // Define credit rating order (lowest quality to highest)
        List<String> ratingOrder = List.of("B", "BB", "BBB", "A", "AA", "AAA");

        // 1. Fulfill rating constraints (low to high)
        for (String rating : ratingOrder) {
            BigDecimal needed = ratingLeft.getOrDefault(rating, BigDecimal.ZERO);
            if (needed.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // Find the cheapest bond with this rating (any type)
            PossibleBorrowedBond bond = borrowMarket.stream()
                    .filter(b -> b.getCreditRating().equalsIgnoreCase(rating))
                    .min(Comparator.comparing(PossibleBorrowedBond::getBorrowRate))
                    .orElseThrow(() -> new IllegalArgumentException("No bond found for rating: " + rating));

            BigDecimal rateDecimal = bond.getBorrowRate().divide(BigDecimal.valueOf(100));
            BigDecimal cost = rateDecimal.multiply(needed);
            totalCost = totalCost.add(cost);
            remaining = remaining.subtract(needed);
            ratingLeft.put(rating, BigDecimal.ZERO);

            // Check if it fulfills a type constraint too
            String bondType = bond.getBondType();
            BigDecimal typeNeed = typeLeft.getOrDefault(bondType, BigDecimal.ZERO);
            if (typeNeed.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal updated = typeNeed.subtract(needed).max(BigDecimal.ZERO);
                typeLeft.put(bondType, updated);
            }

            allocations.add(new Allocation(bond, needed, Set.of(rating, bondType)));
        }

        // 2. Fulfill remaining type constraints
        for (Map.Entry<String, BigDecimal> entry : typeLeft.entrySet()) {
            String type = entry.getKey();
            BigDecimal typeNeed = entry.getValue();
            if (typeNeed.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            PossibleBorrowedBond bond = borrowMarket.stream()
                    .filter(b -> b.getBondType().equalsIgnoreCase(type))
                    .min(Comparator.comparing(PossibleBorrowedBond::getBorrowRate))
                    .orElseThrow(() -> new IllegalArgumentException("No bond found for type: " + type));

            BigDecimal rateDecimal = bond.getBorrowRate().divide(BigDecimal.valueOf(100));
            BigDecimal cost = rateDecimal.multiply(typeNeed);
            totalCost = totalCost.add(cost);
            remaining = remaining.subtract(typeNeed);
            typeLeft.put(type, BigDecimal.ZERO);

            allocations.add(new Allocation(bond, typeNeed, Set.of(type)));
        }

        // 3. Fill remaining value with the cheapest bond overall
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            PossibleBorrowedBond bond = borrowMarket.stream()
                    .min(Comparator.comparing(PossibleBorrowedBond::getBorrowRate))
                    .orElseThrow(() -> new IllegalArgumentException("Borrow market is empty"));

            BigDecimal rateDecimal = bond.getBorrowRate().divide(BigDecimal.valueOf(100));
            BigDecimal cost = rateDecimal.multiply(remaining);
            totalCost = totalCost.add(cost);

            allocations.add(new Allocation(bond, remaining, Set.of("Unconstrained")));
        }

        // Debug print
        System.out.println("\n\n\n\n---- Allocation Breakdown for Deal " + deal.getId() + " ----");
        allocations.forEach(System.out::println);
        System.out.printf("Total Borrow Cost: $%.2f%n", totalCost);
        System.out.println("--------------------------------------------");

        // Check if any constraints are unmet
        boolean ratingUnmet = ratingLeft.values().stream().anyMatch(v -> v.compareTo(BigDecimal.ZERO) > 0);
        boolean typeUnmet = typeLeft.values().stream().anyMatch(v -> v.compareTo(BigDecimal.ZERO) > 0);

        if ((ratingUnmet || typeUnmet) && remaining.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Could not fulfill all constraints within total value.");

        }

        return totalCost;

    }

    private static BigDecimal calculateFallbackStrategy(RepoDeal deal, List<PossibleBorrowedBond> borrowMarket) {
        // Simple greedy fallback or new logic you want to try
        // For now just throw until implemented
        throw new UnsupportedOperationException("Fallback strategy not yet implemented.");
    }

}

class Allocation {

    public final String bondId;
    public final String bondType;
    public final String creditRating;
    public final BigDecimal rate;
    public final BigDecimal amount;
    public final Set<String> constraintsUsed;

    public Allocation(PossibleBorrowedBond bond, BigDecimal amount, Set<String> constraintsUsed) {
        this.bondId = bond.getId();
        this.bondType = bond.getBondType();
        this.creditRating = bond.getCreditRating();
        this.rate = bond.getBorrowRate();
        this.amount = amount;
        this.constraintsUsed = constraintsUsed;
    }

    @Override
    public String toString() {
        return String.format("Bond %s (%s/%s @ %s%%) → $%.2f used for %s",
                bondId, creditRating, bondType, rate, amount, constraintsUsed);
    }
}
