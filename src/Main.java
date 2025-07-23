
import java.nio.file.Paths;
import java.util.List;
import models.Bond;
import models.PossibleBorrowedBond;
import models.RepoDeal;
import util.DataLoader;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello, World! This is a Repo Agreement application.");

        List<Bond> bonds = loadCollateralBonds();
        List<PossibleBorrowedBond> possibleBorrowedBonds = loadBorrowMarket();
        List<RepoDeal> deals = loadDeals();
        

        printInternalCollateral(bonds);
        printBorrowMarket(possibleBorrowedBonds);
        printRepoDeals(deals);



       
    }

   private static void printInternalCollateral(List<Bond> bonds) {
    System.out.println("\n==================== Collateral Bonds ====================");
    for (Bond bond : bonds) {
        System.out.println(bond);
        System.out.println("---------------------------------------------------------");
    }
    System.out.println("Loaded " + bonds.size() + " internal collateral bonds.");
}

private static void printBorrowMarket(List<PossibleBorrowedBond> possibleBorrowedBonds) {
    System.out.println("\n==================== Borrow Market Bonds ====================");
    for (PossibleBorrowedBond possibleBorrowedBond : possibleBorrowedBonds) {
        System.out.println(possibleBorrowedBond);
        System.out.println("------------------------------------------------------------");
    }
    System.out.println("Loaded " + possibleBorrowedBonds.size() + " external market bonds.");
}

private static void printRepoDeals(List<RepoDeal> deals){
    System.out.println("\n==================== Repo Deals ====================");
    for (RepoDeal repoDeal : deals) {
        System.out.println(repoDeal);
        System.out.println("---------------------------------------------------");
    }
    System.out.println("Loaded " + deals.size() + " repo deals.");
}
    private static List<Bond> loadCollateralBonds() {
        String filepath = Paths.get("src", "assets", "collateral.csv").toString();

        try {
            return DataLoader.loadBonds(filepath);
        } catch (java.io.IOException | RuntimeException e) {
            System.err.println("Error loading bonds: " + e.getMessage());
            return List.of(); // return empty list on failure
        }

    }

    private static List<PossibleBorrowedBond> loadBorrowMarket() {
        String filepath = Paths.get("src", "assets", "borrow_market.csv").toString();

        try {
            return DataLoader.loadPossibleBorrowedBonds(filepath);
        } catch (java.io.IOException | RuntimeException e) {
            System.err.println("Error loading bonds: " + e.getMessage());
            return List.of(); // return empty list on failure
        }

    }

    private static List<RepoDeal> loadDeals(){
        String filepath = Paths.get("src", "assets", "repo_deals.csv").toString();
        
        try {
            return DataLoader.loadRepoDeals(filepath);
        } catch (java.io.IOException | RuntimeException e) {
            System.err.println("Error loading deals: " + e.getMessage());
            return List.of(); // return empty list on failure
        }


    } 

}

