package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ReadCSV {
    public static void main(String[] args) {
        List<Product> productList = readCSVFile("/Users/rj/eclipse-workspace/swinggui/src/inventory.csv");

        // Access elements
        for (Product product : productList) {
            System.out.println(product.getId() + " - " + product.getName() + " - " + product.getPrice());
        }
    }

    public static List<Product> readCSVFile(String filePath) {
        List<Product> productList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");

                // Assuming the CSV columns are in the order of id, name, available, quantity, price
                String id = tokenizer.nextToken().trim();
                String rawName = tokenizer.nextToken().trim();
                // Remove quotes from the name
                String name = rawName.substring(1, rawName.length() - 1);
                boolean available = Boolean.parseBoolean(tokenizer.nextToken().trim());
                int quantity = Integer.parseInt(tokenizer.nextToken().trim());
                double price = Double.parseDouble(tokenizer.nextToken().trim());

                productList.add(new Product(id, name, available, quantity, price));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return productList;
    }
}
