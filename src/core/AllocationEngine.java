package core;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import models.Bond;
import models.PossibleBorrowedBond;
import models.RepoDeal;

public class AllocationEngine {

    // Calculate predicted external cost based on the best borrow rate for the bond type
    public BigDecimal calculateExternalBorrowCost(
            RepoDeal deal,
            Map<String, Bond> bondMap,
            List<PossibleBorrowedBond> borrowMarket) {

        // 1. Look up the bond to get its type
        Bond bond = bondMap.get(deal.getId());
        if (bond == null) {
            throw new IllegalArgumentException("Bond not found for ID: " + deal.getId());
        }

        String bondType = bond.getType();
        BigDecimal requireValue = deal.getTotalValueRequired(); // already in dollars

        // 2. Get the lowest borrow rate for the same bond type
        return borrowMarket.stream()
            .filter(b -> b.getBondType().equalsIgnoreCase(bondType))
            .map(PossibleBorrowedBond::getBorrowRate)
            .min(Comparator.naturalOrder())
            .map(rate -> rate.multiply(requireValue))
            .orElse(BigDecimal.valueOf(Double.POSITIVE_INFINITY));
    }

    // ... other methods
}
