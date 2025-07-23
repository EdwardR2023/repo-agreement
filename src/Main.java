
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

/**
 * Prints the details of a list of internal collateral bonds to the standard output.
 * Each bond is printed followed by a separator line. At the end, the total number
 * of bonds loaded is displayed.
 *
 * @param bonds the list of Bond objects representing internal collateral bonds
 */
   private static void printInternalCollateral(List<Bond> bonds) {
    System.out.println("\n==================== Collateral Bonds ====================");
    for (Bond bond : bonds) {
        System.out.println(bond);
        System.out.println("---------------------------------------------------------");
    }
    System.out.println("Loaded " + bonds.size() + " internal collateral bonds.");
}

/**
 * Prints a formatted list of possible borrowed bonds to the console.
 * Displays each bond in the provided list, separated by a line,
 * and shows the total number of external market bonds loaded.
 *
 * @param possibleBorrowedBonds the list of PossibleBorrowedBond objects to display
 */
private static void printBorrowMarket(List<PossibleBorrowedBond> possibleBorrowedBonds) {
    System.out.println("\n==================== Borrow Market Bonds ====================");
    for (PossibleBorrowedBond possibleBorrowedBond : possibleBorrowedBonds) {
        System.out.println(possibleBorrowedBond);
        System.out.println("------------------------------------------------------------");
    }
    System.out.println("Loaded " + possibleBorrowedBonds.size() + " external market bonds.");
}

/**
 * Prints a formatted list of repo deals to the standard output.
 * Each deal is separated by a line, and the total number of deals is displayed at the end.
 *
 * @param deals the list of {@link RepoDeal} objects to be printed
 */
private static void printRepoDeals(List<RepoDeal> deals){
    System.out.println("\n==================== Repo Deals ====================");
    for (RepoDeal repoDeal : deals) {
        System.out.println(repoDeal);
        System.out.println("---------------------------------------------------");
    }
    System.out.println("Loaded " + deals.size() + " repo deals.");
}
    /**
     * Loads a list of collateral bonds from a CSV file located at "src/assets/collateral.csv".
     * <p>
     * This method attempts to read bond data using the {@link DataLoader#loadBonds(String)} method.
     * If an {@link java.io.IOException} or {@link RuntimeException} occurs during loading,
     * it logs the error message to standard error and returns an empty list.
     *
     * @return a {@code List<Bond>} containing the loaded collateral bonds, or an empty list if loading fails
     */
    private static List<Bond> loadCollateralBonds() {
        String filepath = Paths.get("src", "assets", "collateral.csv").toString();

        try {
            return DataLoader.loadBonds(filepath);
        } catch (java.io.IOException | RuntimeException e) {
            System.err.println("Error loading bonds: " + e.getMessage());
            return List.of(); // return empty list on failure
        }

    }

    /**
     * Loads a list of possible borrowed bonds from the borrow market CSV file.
     * <p>
     * This method attempts to read bond data from the file located at
     * "src/assets/borrow_market.csv" and parse it into a list of
     * {@link PossibleBorrowedBond} objects using the {@link DataLoader}.
     * If an error occurs during loading or parsing, an empty list is returned
     * and the error is logged to standard error.
     *
     * @return a list of {@link PossibleBorrowedBond} objects loaded from the CSV file,
     *         or an empty list if loading fails
     */
    private static List<PossibleBorrowedBond> loadBorrowMarket() {
        String filepath = Paths.get("src", "assets", "borrow_market.csv").toString();

        try {
            return DataLoader.loadPossibleBorrowedBonds(filepath);
        } catch (java.io.IOException | RuntimeException e) {
            System.err.println("Error loading bonds: " + e.getMessage());
            return List.of(); // return empty list on failure
        }

    }

    /**
     * Loads a list of RepoDeal objects from a CSV file located at "src/assets/repo_deals.csv".
     * <p>
     * This method attempts to read and parse the repo deals from the specified CSV file
     * using the {@link DataLoader#loadRepoDeals(String)} method. If an {@link java.io.IOException}
     * or {@link RuntimeException} occurs during loading, an error message is printed to standard error,
     * and an empty list is returned.
     *
     * @return a {@code List<RepoDeal>} containing the loaded repo deals, or an empty list if loading fails
     */
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

