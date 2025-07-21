import java.nio.file.Paths;
import java.util.List;
import models.Bond;
import util.DataLoader;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello, World! This is a Repo Agreement application.");

        List<Bond> bonds = loadCollateralBonds();

        for (Bond bond : bonds) {
            System.out.println(bond);
        }

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
}
