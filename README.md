## 📘 What is a Repurchase Agreement?

A **repurchase agreement (repo)** is a short-term loan where one party sells securities (typically bonds) to another with an agreement to repurchase them later at a higher price. It's a common method for financial institutions to raise liquidity.

- **Seller = Borrower**: Needs cash now, so they "sell" a bond.
- **Buyer = Lender**: Provides cash in exchange for the bond as collateral.
- The **repurchase price** includes interest — that’s the lender’s profit.

In this project, we simulate the internal decision-making process of a firm attempting to fulfill repo deals using its own bond inventory. If internal assets are insufficient, the firm must borrow external collateral — but at a higher cost.

The goal is to **minimize total borrow cost** while satisfying each repo deal’s constraints on bond type and credit rating.


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
   External collateral is not modeled in units. Instead, the shortfall is treated as a dollar value (e.g., $126), and the borrow cost is computed by multiplying that shortfall by the applicable borrow rate from the external market.

This greedy strategy ensures internal collateral is used where it matters most and prevents expensive deals from draining the borrow market late in the process.


## How to Compile and Run the Project
Prerequisites:
   Java 17+ installed
   This project uses plain .java files (no Maven or Gradle)

Compile:
Run this from the project root (where the src/ folder is located): javac -d out src/models/*.java src/util/*.java src/core/*.java src/Main.java


Run:
After compiling, run the program with: java -cp out Main



