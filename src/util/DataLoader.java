package util;

// This class is a placeholder for data loading functionality.
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import models.Bond;
import models.PossibleBorrowedBond;
import models.RepoDeal;

public class DataLoader {

    public static List<PossibleBorrowedBond> loadPossibleBorrowedBonds(String filepath) throws IOException {
        List<PossibleBorrowedBond> borrowMarketList = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filepath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");

                String id = parts[0].trim();
                String bondType = parts[1].trim();
                String creditRating = parts[2].trim();
                BigDecimal borrowRate = new BigDecimal(parts[3].trim());

                borrowMarketList.add(new PossibleBorrowedBond(id, bondType, creditRating, borrowRate));
            }
        }

        return borrowMarketList;
    }

    public static List<Bond> loadBonds(String filepath) throws IOException {
        List<Bond> bonds = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filepath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // skip header
                    continue;
                }

                String[] parts = line.split(",");

                String id = parts[0].trim();
                String bondType = parts[1].trim();
                String creditRating = parts[2].trim();
                BigInteger quantity = BigInteger.valueOf(Integer.parseInt(parts[3].trim()));
                BigDecimal price = new BigDecimal(parts[4].trim());

                bonds.add(new Bond(id, bondType, creditRating, quantity, price));
            }
        }

        return bonds;
    }

// PG-1
// TODO: Implement a method that loads data from a `RepoDeals.csv` file and returns a List<RepoDeal>.
// The format will be similar to how bonds are loaded in `loadBonds(...)` above.
// You'll need to:
// - Parse each line of the CSV (skip the header).
// - Extract the required fields for each RepoDeal:
//     > id (String)
//     > totalValueRequired (BigDecimal)
//     > ratingRequirements (Map<String, BigDecimal>)
//     > typeRequirements (Map<String, BigDecimal>)
// - Construct and return RepoDeal objects.
//
// Let me know if you need help designing the CSV format or parsing the 
// requirement maps (they may look like: "AAA:0.5,AA:0.3,B:0.2").

// NOTE: You do not need to manually set the shortfall — it is automatically initialized
// to totalValueRequired inside the RepoDeal constructor.



//this is what you need to fill in
    public static List<RepoDeal> loadRepoDeals(String filepath) throws IOException {
        List<RepoDeal> deals = new ArrayList<>();

        //this is temp
        return deals;
    }
}
