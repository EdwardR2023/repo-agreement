## Objective

To build an engine that:
- Reads available internal collateral (bonds) from CSV
- Reads a set of repo deals with constraints (e.g., minimum % of AAA-rated bonds, minimum % of Municipal bonds)
- Satisfies as much of the repo deal requirements as possible using internal collateral
- Borrows any shortfall from an external borrowing market (also defined in CSV)
- Minimizes the total borrowing cost when external collateral is needed
- **Uses a greedy approach that prioritizes deals that would be most expensive to borrow for**

## Approach

We use a greedy algorithm to minimize total borrowing cost across all repo deals:

1. **Estimate Worst-Case Borrow Cost**  
   For each repo deal, calculate how expensive it would be to fulfill using only the external borrowing market. This includes checking the borrow rate for each required bond type and credit rating.

2. **Sort Deals by Estimated Borrow Cost (Descending)**  
   The deals are sorted in descending order based on their worst-case borrowing cost. This ensures the most expensive-to-borrow deals are solved first.

3. **Allocate Internal Inventory First**  
   For each deal, attempt to satisfy the constraints using internal collateral. If internal collateral is insufficient, borrow bonds from the external market.

4. **Track and Minimize Borrow Cost**  
   Every unit of borrowed collateral is multiplied by its borrow rate to compute the total borrow cost for that deal. The engine tracks the overall borrowing cost and quantity used.

This greedy strategy ensures internal collateral is used where it matters most and prevents expensive deals from draining the borrow market late in the process.
