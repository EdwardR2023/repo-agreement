import java.nio.file.Paths;
import java.util.List;
import models.Bond;
import models.PossibleBorrowedBond;
import util.DataLoader;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello, World! This is a Repo Agreement application.");

        List<Bond> bonds = loadCollateralBonds();
        List<PossibleBorrowedBond> possibleBorrowedBonds = loadBorrowMarket();

        System.out.println("Collateral Bonds:");
         for (Bond bond : bonds) {
            System.out.println(bond);
        } 
        System.out.println("\n\n\n\nBorrow Market Bonds:");

        for (PossibleBorrowedBond possibleBorrowedBond : possibleBorrowedBonds) {
            System.out.println(possibleBorrowedBond);
        }

        System.out.println("Loaded " + bonds.size() + " internal collateral bonds.");
        System.out.println("Loaded " + possibleBorrowedBonds.size() + " possible borrowed bonds.");

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

    private static List<PossibleBorrowedBond> loadBorrowMarket(){
        String filepath = Paths.get("src", "assets", "borrow_market.csv").toString();

        try{
            return DataLoader.loadPossibleBorrowedBonds(filepath);
        } catch (java.io.IOException | RuntimeException e){
            System.err.println("Error loading bonds: " + e.getMessage());
            return List.of(); // return empty list on failure
        }

    }
}
