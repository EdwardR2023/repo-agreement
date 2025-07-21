import java.util.List;
import models.Bond;
import util.DataLoader;

public class Main {
   public static void main(String[] args) {

        System.out.println("Hello, World! This is a Repo Agreement application. ");
        // Load bonds from the CSV file
        String path = "src/assets";
        String name = "collateral.csv";
        String filepath = path + "/" + name;
        try {
            List<Bond> bonds = DataLoader.loadBonds(filepath);

            // Print to verify
            for (Bond bond : bonds) {
                System.out.println(bond);
            }

            // Next: Pass bonds to your repo allocation logic


        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
