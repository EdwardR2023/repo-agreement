package util;

// This class is a placeholder for data loading functionality.
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public static List<RepoDeal> loadRepoDeals(String filepath) throws IOException {
    System.out.println("Loading repo deals from: " + filepath);
    List<RepoDeal> repoDeals = new ArrayList<>();

    try (BufferedReader br = Files.newBufferedReader(Paths.get(filepath))) {
        String line;
        boolean isFirstLine = true;

        while ((line = br.readLine()) != null) {

            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }

            String[] parts = line.split(",");
            System.out.println("Columns: " + parts.length);

            if (parts.length < 12) {
                System.out.println("Skipping line, not enough columns: " + line);
                continue;
            }

            try {
                String id = parts[0].trim();
                BigDecimal totalValueRequired = new BigDecimal(parts[2].trim());

                Map<String, BigDecimal> ratingRequirements = new HashMap<>();
                if (!parts[3].trim().equals("0")) ratingRequirements.put("AAA", new BigDecimal(parts[3].trim()));
                if (!parts[4].trim().equals("0")) ratingRequirements.put("AA", new BigDecimal(parts[4].trim()));
                if (!parts[5].trim().equals("0")) ratingRequirements.put("A", new BigDecimal(parts[5].trim()));
                if (!parts[6].trim().equals("0")) ratingRequirements.put("BBB", new BigDecimal(parts[6].trim()));
                if (!parts[7].trim().equals("0")) ratingRequirements.put("BB", new BigDecimal(parts[7].trim()));
                if (!parts[8].trim().equals("0")) ratingRequirements.put("B", new BigDecimal(parts[8].trim()));

                Map<String, BigDecimal> typeRequirements = new HashMap<>();
                if (!parts[9].trim().equals("0")) typeRequirements.put("Municipal", new BigDecimal(parts[9].trim()));
                if (!parts[10].trim().equals("0")) typeRequirements.put("Sovereign", new BigDecimal(parts[10].trim()));
                if (!parts[11].trim().equals("0")) typeRequirements.put("Corporate", new BigDecimal(parts[11].trim()));

                System.out.println("Parsed RepoDeal: id=" + id + ", totalValueRequired=" + totalValueRequired +
                    ", ratingRequirements=" + ratingRequirements + ", typeRequirements=" + typeRequirements);

                repoDeals.add(new RepoDeal(id, totalValueRequired, ratingRequirements, typeRequirements));
            } catch (Exception e) {
                System.out.println("Error parsing line: " + line);
                e.printStackTrace();
            }
        }
    }
    System.out.println("Total loaded repo deals: " + repoDeals.size());
    return repoDeals;
}

}
