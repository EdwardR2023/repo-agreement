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



public class DataLoader {
    // This class can be expanded to include methods for loading data from files, databases, etc.
    // Currently, it serves as a placeholder for future data loading implementations.

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


    
}
