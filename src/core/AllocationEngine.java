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
        } catch (UnfulfillableConstraintException e) {
            System.out.println("Greedy strategy failed for deal " + deal.getId() + ": " + e.getMessage());
            System.out.println("Falling back to LP strategy...");
            return calculateFallbackStrategy(deal, borrowMarket);
        }
    }

    private static BigDecimal calculateLowToHighRatingStrategy(RepoDeal deal, List<PossibleBorrowedBond> borrowMarket) {
        BigDecimal totalRequired = deal.getTotalValueRequired();
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal remaining = totalRequired;

        List<Allocation> allocations = new ArrayList<>();

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

        List<String> ratingOrder = List.of("B", "BB", "BBB", "A", "AA", "AAA");

        for (String rating : ratingOrder) {
            BigDecimal needed = ratingLeft.getOrDefault(rating, BigDecimal.ZERO);
            if (needed.compareTo(BigDecimal.ZERO) <= 0) continue;

            PossibleBorrowedBond bond = borrowMarket.stream()
                    .filter(b -> b.getCreditRating().equalsIgnoreCase(rating))
                    .min(Comparator.comparing(PossibleBorrowedBond::getBorrowRate))
                    .orElseThrow(() -> new IllegalArgumentException("No bond found for rating: " + rating));

            BigDecimal rateDecimal = bond.getBorrowRate().divide(BigDecimal.valueOf(100));
            BigDecimal cost = rateDecimal.multiply(needed);
            totalCost = totalCost.add(cost);
            remaining = remaining.subtract(needed);
            ratingLeft.put(rating, BigDecimal.ZERO);

            String bondType = bond.getBondType();
            BigDecimal typeNeed = typeLeft.getOrDefault(bondType, BigDecimal.ZERO);
            if (typeNeed.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal updated = typeNeed.subtract(needed).max(BigDecimal.ZERO);
                typeLeft.put(bondType, updated);
            }

            allocations.add(new Allocation(bond, needed, Set.of(rating, bondType)));
        }

        for (Map.Entry<String, BigDecimal> entry : typeLeft.entrySet()) {
            String type = entry.getKey();
            BigDecimal typeNeed = entry.getValue();
            if (typeNeed.compareTo(BigDecimal.ZERO) <= 0) continue;

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

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            PossibleBorrowedBond bond = borrowMarket.stream()
                    .min(Comparator.comparing(PossibleBorrowedBond::getBorrowRate))
                    .orElseThrow(() -> new IllegalArgumentException("Borrow market is empty"));

            BigDecimal rateDecimal = bond.getBorrowRate().divide(BigDecimal.valueOf(100));
            BigDecimal cost = rateDecimal.multiply(remaining);
            totalCost = totalCost.add(cost);

            allocations.add(new Allocation(bond, remaining, Set.of("Unconstrained")));
        }

        boolean ratingUnmet = ratingLeft.values().stream().anyMatch(v -> v.compareTo(BigDecimal.ZERO) > 0);
        boolean typeUnmet = typeLeft.values().stream().anyMatch(v -> v.compareTo(BigDecimal.ZERO) > 0);

        BigDecimal totalAllocated = allocations.stream()
                .map(a -> a.amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if ((ratingUnmet || typeUnmet) && remaining.compareTo(BigDecimal.ZERO) <= 0 || totalAllocated.compareTo(totalRequired) > 0) {
            throw new UnfulfillableConstraintException("Invalid allocation: constraints unmet or over-allocated.");
        }

        System.out.println("\n---- Allocation Breakdown for Deal " + deal.getId() + " ----");
        allocations.forEach(System.out::println);
        System.out.printf("Total Borrow Cost: $%.2f%n", totalCost);
        System.out.println("--------------------------------------------\n\n\n");

        return totalCost;
    }

    private static BigDecimal calculateFallbackStrategy(RepoDeal deal, List<PossibleBorrowedBond> borrowMarket) {
        List<Allocation> bestSolution = new ArrayList<>();
        BigDecimal[] bestCost = {null};

        backtrack(deal, borrowMarket, 0, new ArrayList<>(), BigDecimal.ZERO, BigDecimal.ZERO, bestSolution, bestCost);

        if (bestSolution.isEmpty()) {
            throw new UnfulfillableConstraintException("Backtracking failed: no valid allocation found.");
        }

        System.out.println("\n\n\n---- Backtracking Allocation Breakdown for Deal " + deal.getId() + " ----");
        BigDecimal totalCost = BigDecimal.ZERO;
        for (Allocation alloc : bestSolution) {
            System.out.println(alloc);
            totalCost = totalCost.add(alloc.rate.divide(BigDecimal.valueOf(100)).multiply(alloc.amount));
        }
        System.out.printf("Total Backtracking Borrow Cost: $%.2f%n", totalCost);
        System.out.println("--------------------------------------------------");

        return totalCost;
    }

    private static void backtrack(
            RepoDeal deal,
            List<PossibleBorrowedBond> market,
            int index,
            List<Allocation> current,
            BigDecimal currentValue,
            BigDecimal currentCost,
            List<Allocation> bestSolution,
            BigDecimal[] bestCost
    ) {
        BigDecimal totalRequired = deal.getTotalValueRequired();

        if (currentValue.compareTo(totalRequired) >= 0) {
            if (isValidAllocation(deal, current)) {
                if (bestCost[0] == null || currentCost.compareTo(bestCost[0]) < 0) {
                    bestCost[0] = currentCost;
                    bestSolution.clear();
                    bestSolution.addAll(new ArrayList<>(current));
                }
            }
            return;
        }

        if (index >= market.size()) return;

        PossibleBorrowedBond bond = market.get(index);
        BigDecimal maxIncrement = totalRequired.subtract(currentValue);

        BigDecimal step = totalRequired.multiply(BigDecimal.valueOf(0.20)); // 20% step

        for (BigDecimal amt = BigDecimal.ZERO;
             amt.compareTo(maxIncrement) <= 0;
             amt = amt.add(step)) {

            if (amt.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = bond.getBorrowRate().divide(BigDecimal.valueOf(100));
                BigDecimal newCost = currentCost.add(rate.multiply(amt));
                current.add(new Allocation(bond, amt, Set.of(bond.getBondType(), bond.getCreditRating())));
                backtrack(deal, market, index + 1, current, currentValue.add(amt), newCost, bestSolution, bestCost);
                current.remove(current.size() - 1);
            } else {
                backtrack(deal, market, index + 1, current, currentValue, currentCost, bestSolution, bestCost);
            }
        }
    }

    private static boolean isValidAllocation(RepoDeal deal, List<Allocation> allocations) {
        BigDecimal total = deal.getTotalValueRequired();

        Map<String, BigDecimal> ratingMap = new HashMap<>();
        Map<String, BigDecimal> typeMap = new HashMap<>();

        for (Allocation alloc : allocations) {
            ratingMap.merge(alloc.creditRating, alloc.amount, BigDecimal::add);
            typeMap.merge(alloc.bondType, alloc.amount, BigDecimal::add);
        }

        for (Map.Entry<String, BigDecimal> req : deal.getRatingRequirements().entrySet()) {
            BigDecimal required = total.multiply(req.getValue()).divide(BigDecimal.valueOf(100));
            if (ratingMap.getOrDefault(req.getKey(), BigDecimal.ZERO).compareTo(required) < 0) return false;
        }

        for (Map.Entry<String, BigDecimal> req : deal.getTypeRequirements().entrySet()) {
            BigDecimal required = total.multiply(req.getValue()).divide(BigDecimal.valueOf(100));
            if (typeMap.getOrDefault(req.getKey(), BigDecimal.ZERO).compareTo(required) < 0) return false;
        }

        return true;
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

class UnfulfillableConstraintException extends RuntimeException {
    public UnfulfillableConstraintException(String message) {
        super(message);
    }
}
