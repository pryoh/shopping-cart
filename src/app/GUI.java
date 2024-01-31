/* Ryan Monahan
 * Course: CNT 4714 - Spring 2024
 * Project 1 - An Event-driven Enterprise Simulation
 * Date: 1/30/24
 */
package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GUI implements ActionListener {
    private JFrame frame;

    private JTextField idField;
    private JTextField quantityField;
    private JTextField detailsField;
    private JTextField subtotalField;

    private List<Product> productList;
    private List<Product> cart;
    private Product lastFoundItem;
    
    private JButton addButton;
    private JButton findItemButton;
    private JButton emptyCartButton;
    private JButton checkoutButton;
    private JButton viewCartButton;
    private JButton exitButton;

    private JPanel topPanel;
    private JPanel cartPanel;
    private JPanel bottomPanel;

    private int cartItemCounter;
    private int findItemCounter;

    public GUI(List<Product> productList) {
        this.productList = productList;
        this.cart = new ArrayList<>();
        this.cartItemCounter = 1;
        this.findItemCounter = 1;
        initialize();
    }

    private void initialize() {
        // Create frame
        frame = new JFrame("Nile.com - Spring 2024");

        // Create components
        idField = new JTextField(40);
        quantityField = new JTextField(40);
        detailsField = new JTextField(40);
        detailsField.setEditable(false);
        subtotalField = new JTextField(20);
        subtotalField.setEditable(false);

        // Create "Find Item" button
        findItemButton = new JButton("Find Item #" + cartItemCounter);
        findItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findItemById();
            }
        });

        // Create "Add Product" button
        addButton = new JButton("Add Item #" + cartItemCounter + " To Cart");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductToCart();
            }
        });
        addButton.setEnabled(false);

        // Create "View Cart" button
        viewCartButton = new JButton("View Cart");
        viewCartButton.setEnabled(false);
        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCart();
            }
        });
        viewCartButton.setEnabled(false);

        // Create "Check Out" button
        checkoutButton = new JButton("Check Out");
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkOut();
            }
        });
        checkoutButton.setEnabled(false);

        // Create "Exit (Close App)" button
        exitButton = new JButton("Exit (Close App)");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        });

        // Create "Empty Cart - Start a New Order" button
        emptyCartButton = new JButton("Empty Cart - Start a New Order");
        emptyCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emptyCart();
            }
        });

        // Create panels
        topPanel = createTopPanel();
        cartPanel = createCartPanel();
        bottomPanel = createBottomPanel(findItemButton, addButton, viewCartButton, checkoutButton, emptyCartButton, exitButton);

        // Set layout for the frame
        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(cartPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Set frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);  // Center the frame
    }
    // Top panel method
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(0, 2)); // 0 rows, 2 columns for a vertical layout
    
        // Dynamically update the label text based on the number of items in the cart

        JLabel itemLabelText = new JLabel("Enter item ID for Item #" + (cartItemCounter) + ":");
        itemLabelText.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(itemLabelText);
        topPanel.add(idField);
    
        JLabel quantityLabelText = new JLabel("Enter quantity for Item #" + (cartItemCounter) + ":");
        quantityLabelText.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(quantityLabelText);
        topPanel.add(quantityField);
    
        JLabel detailsLabelText = new JLabel("Details for item #" + (findItemCounter) + ":");
        detailsLabelText.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(detailsLabelText);
        topPanel.add(detailsField);

        JLabel subtotalLabelText = new JLabel("Current subtotal for " + cart.size() + " Item(s):");
        subtotalLabelText.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(subtotalLabelText);
        topPanel.add(subtotalField);

        return topPanel;
    }

    // Update top panel
    private void updateTopPanel() {
        // Update the labels based on the new cart item counter
        String itemLabelText = "Enter item ID for Item #" + (cartItemCounter) + ":";
        String quantityLabelText = "Enter quantity for Item #" + (cartItemCounter) + ":";
        String subtotalLabelText = "Current subtotal for " + cart.size() + " Item(s):";
        
        ((JLabel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(0)).setText(itemLabelText);
        ((JLabel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(2)).setText(quantityLabelText);
        ((JLabel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(6)).setText(subtotalLabelText);
    }

    private void updateDetailsField(Product product) {
        // Update the details field with all details of the found item including sale percentage
        String findQuantity = quantityField.getText(); // Trim leading and trailing spaces
        int quantity = Integer.parseInt(findQuantity);
        double originalPrice = product.getPrice();
        double salePercentage = calculateSalePercentage(quantity);
        double discountedPrice = calculateDiscountedPrice(originalPrice, salePercentage);

        detailsField.setText(product.getId() +
                " \"" + product.getName() + "\"" +
                "  $" + originalPrice +
                "  " + quantity +
                "  " + salePercentage + "%" +
                "  $" + (discountedPrice*quantity));
    }

    private void updateDetailsText() {
        String detailsLabelText = "Details for item #" + (findItemCounter) + ":";
        ((JLabel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(4)).setText(detailsLabelText);
    }

    // Cart Panel Method
    // Cart Panel Method
    private JPanel createCartPanel() {
        JPanel cartPanel = new JPanel(new BorderLayout());

        // Dynamically update the label based on the number of items in the cart
        JLabel cartLabel = new JLabel("Your Current Shopping Cart Is Empty");
        cartLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cartPanel.add(cartLabel, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < 5; i++) {
            JPanel cartItemPanel = new JPanel();
            JLabel itemLabel = new JLabel("Item " + (i + 1) + ":");
            JTextField cartTextField = new JTextField(50);
            cartTextField.setEditable(false);  // Make it read-only
            cartItemPanel.add(itemLabel);
            cartItemPanel.add(cartTextField);
            itemsPanel.add(cartItemPanel);
        }

        cartPanel.add(itemsPanel, BorderLayout.CENTER);
        cartPanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);
        cartPanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);


        return cartPanel;
    }


    // Update cart panel
    private void updateCartPanel() {
        // Update the label based on the number of items in the cart
        JLabel cartLabel = (JLabel) cartPanel.getComponent(0);
        cartLabel.setText("Your Current Shopping Cart with " + cart.size() + " Item(s)");
    
        // Update the text fields in the items panel with the latest items in the cart
        JPanel itemsPanel = (JPanel) cartPanel.getComponent(1);
        for (int i = 0; i < 5; i++) {
            Component cartComponent = itemsPanel.getComponent(i);
    
            if (cartComponent instanceof JPanel) {
                JPanel cartItemPanel = (JPanel) cartComponent;
    
                // Find the JTextField within the JPanel
                for (Component panelComponent : cartItemPanel.getComponents()) {
                    if (panelComponent instanceof JTextField) {
                        JTextField cartTextField = (JTextField) panelComponent;
    
                        if (i < cart.size()) {
                            // If there's an item in the cart, update the text field
                            Product cartItem = cart.get(i);
                            double salePercentage = calculateSalePercentage(cartItem.getQuantity());
                            double total = calculateDiscountedPrice(cartItem.getPrice(), salePercentage);
                            cartTextField.setText("Item " + (i + 1) + " - " + "SKU: " + cartItem.getId() + ", " +
                                    "Desc: " + cartItem.getName() + ", " + "Price Ea. " + cartItem.getPrice() + ", " +
                                    "Qty: " + cartItem.getQuantity() + ", " + "Total: $" + total*cartItem.getQuantity());
                        } else {
                            // If no item in the cart, clear the text field
                            cartTextField.setText("");
                        }
                    }
                }
            }
        }
        if (cart.size() >= 5) {
            findItemButton.setEnabled(false);
            addButton.setEnabled(false);
            idField.setEditable(false);
            quantityField.setEditable(false);
        }
    }
    
    

    //Bottom panel methods
    private void findItemById() {
        try {
            String findId = idField.getText(); // Trim leading and trailing spaces
            String findQuantity = quantityField.getText(); // Trim leading and trailing spaces

            // Check if both ID and quantity have been provided
            if (findId.isEmpty() || findQuantity.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please input both ID and quantity to find.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int desiredQuantity = Integer.parseInt(findQuantity);

            // Search for the item in the product list
            for (Product product : productList) {
                if (product.getId().equals(findId)) { // Trim leading and trailing spaces from the product ID
                    // Update the details field with all details of the found item
                    lastFoundItem = product;
                    updateDetailsField(product);
                    updateDetailsText();
                    updateSubtotal();
                    findItemCounter++;
                    addButton.setEnabled(true);
                    findItemButton.setEnabled(false);
                    if(product.getQuantity() < desiredQuantity) {
                        // Display a message about insufficient stock
                        String message = "Insufficient stock. Only " + product.getQuantity() + " on hand. Please reduce the quantity.";
                        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
                        detailsField.setText("");  // Clear details field when stock is insufficient
                        return;
                    }
                    if(lastFoundItem != null) lastFoundItem.setQuantity(Integer.parseInt(quantityField.getText()));
                    return;  // Exit the method if the item is found
                }
            }

            // If the item is not found, display a message
            JOptionPane.showMessageDialog(frame, "No product found with the entered ID.", "Error", JOptionPane.ERROR_MESSAGE);
            detailsField.setText("");  // Clear details field when item is not found
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProductToCart() {
    
        // Use the stored last found item
        if (lastFoundItem != null) {
            lastFoundItem.setQuantity(Integer.parseInt(quantityField.getText()));
            // Create a new product with the specified quantity and add it to the cart
            cart.add(lastFoundItem);
    
            // Update the cart display
            updateCartPanel();
    
            // Update the subtotal display
    
            // Optionally, you can print or display the added item in the cart
            System.out.println("Added to Cart: " + lastFoundItem.getName() + " - Quantity: " + lastFoundItem.getQuantity());
    
            // Clear the fields
            idField.setText("");
            quantityField.setText("");
    
            // Increment the cart item counter for the next item
            cartItemCounter++;
    
            if(cart.size() <= 5) {
                updateTopPanel();
                updateAddButton();
                updateCartPanel();
                updateFindButton();
            }
            
    
            // Reset the last found item
            lastFoundItem = null;

            checkoutButton.setEnabled(true);
            viewCartButton.setEnabled(true);
            findItemButton.setEnabled(true);
            addButton.setEnabled(false);
    
            return;  // Exit the method if the item is found and added to the cart
        }
    
        // If the last found item is null, display a message
        JOptionPane.showMessageDialog(frame, "Please find an item before adding it to the cart.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void viewCart() {
        StringBuilder cartItems = new StringBuilder("Shopping Cart:\n");
    
        for (int i = 0; i < cart.size(); i++) {
            Product cartItem = cart.get(i);
            double salePercentage = calculateSalePercentage(cartItem.getQuantity());
            double total = calculateDiscountedPrice(cartItem.getPrice(), salePercentage);
    
            cartItems.append((i+1) + ". ")
                    .append(cartItem.getId()).append(" ")
                    .append("\"").append(cartItem.getName()).append("\"").append(" ")
                    .append("$").append(cartItem.getPrice()).append(" ")
                    .append(cartItem.getQuantity()).append(" ")
                    .append(salePercentage).append("% ")
                    .append("$").append(total).append("\n");
        }
    
        // Display a popup message with all items in the shopping cart
        JOptionPane.showMessageDialog(frame, cartItems.toString(), "Nile Dot Com - Current Shopping Cart Status", JOptionPane.INFORMATION_MESSAGE);
        }
    
    
    
        private void checkOut() {
        // Check if the cart is empty
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "The shopping cart is empty. Add items before checking out.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create the final invoice
        StringBuilder invoice = createFinalInvoice();

        // Write details of each item in the cart to the transaction.csv file
        try (FileWriter writer = new FileWriter("transaction.csv", true)) {
            Random random = new Random();
            long randomId = (long) (Math.pow(10, 9) + random.nextDouble() * Math.pow(10, 9));
            for (Product cartItem : cart) {
                String line = randomId + ", " + cartItem.getId() + ", " + "\"" + cartItem.getName() + "\", " + cartItem.isAvailable() + ", " +
                        cartItem.getQuantity() + ", " + cartItem.getPrice();
                writer.write(line + System.lineSeparator());
            }

            // Add a blank line to separate each order
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error writing to transaction.csv file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // Clear the cart
        emptyCart();

        // Display the final invoice
        JOptionPane.showMessageDialog(frame, invoice.toString(), "Nile Dot Com - FINAL INVOICE", JOptionPane.INFORMATION_MESSAGE);

        // Disable buttons after checkout
        viewCartButton.setEnabled(false);
        checkoutButton.setEnabled(false);
    }

    private StringBuilder createFinalInvoice() {
        // Get the current date and time in the correct timezone
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York")); // Adjust timezone as needed
        String dateTime = dateFormat.format(new Date());

        StringBuilder invoice = new StringBuilder("Date: " + dateTime + "\n\n");
        invoice.append("Number of line items: ").append((cartItemCounter-1)).append("\n\n");
        invoice.append("Item# / ID / Title / Price / Qty / Disc% / Subtotal: \n\n");

        // Iterate through items in the cart
        for (int i = 0; i < cart.size(); i++) {
            Product cartItem = cart.get(i);
            double salePercentage = calculateSalePercentage(cartItem.getQuantity());
            double total = calculateDiscountedPrice(cartItem.getPrice(), salePercentage);

            invoice.append((i + 1) + ". ")
                    .append(cartItem.getId()).append(" - ")
                    .append("\"").append(cartItem.getName()).append("\"").append(" ")
                    .append("$").append(cartItem.getPrice()).append(" ")
                    .append(cartItem.getQuantity()).append(" ")
                    .append(salePercentage).append("% ")
                    .append("$").append(total).append("\n");
        }

        // Calculate subtotal, tax rate, tax amount, and order total
        double subtotal = calculateSubtotal();
        double taxRate = 0.06; // 6% tax rate
        double taxAmount = subtotal * taxRate;
        double orderTotal = subtotal + taxAmount;

        // Format currency values
        DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

        // Include subtotal, tax details, and order total
        invoice.append("\n");
        invoice.append("\nOrder Subtotal: ").append(currencyFormat.format(subtotal)).append("\n");
        invoice.append("Tax Rate: 6% ").append("\n");
        invoice.append("Tax Amount: ").append(currencyFormat.format(taxAmount)).append("\n");
        invoice.append("ORDER TOTAL: ").append(currencyFormat.format(orderTotal)).append("\n");

        // Include a closing message
        invoice.append("\nThanks for shopping at Nile Dot Com!");

        return invoice;
    }


    private void emptyCart() {
        // Clear the cart list
        cart.clear();
    
        // Reset the cart item counter
        cartItemCounter = 1;
    
        // Reset the find item counter
        findItemCounter = 1;
    
        // Update the cart display
        updateCartPanel();
    
        // Update the top panel to reflect the new item number
        updateTopPanel();
    
        // Update the "Add Item" button text
        updateAddButton();
    
        // Update the "Find Item" button text
        updateFindButton();
    
        // Update the details field
        detailsField.setText("");
    
        // Update the details label text
        updateDetailsText();
    
        // Update the subtotal field
        updateSubtotal();
    
        // Update the cart label
        updateCartPanel();

        viewCartButton.setEnabled(false);
        checkoutButton.setEnabled(false);
    }
    
    private void exitApplication() {
        frame.dispose(); // Close the frame
        System.exit(0);   // Terminate the application
    }
  

    // Bottom panel method
    // Bottom panel method
    private JPanel createBottomPanel(JButton findItemButton, JButton addButton, JButton viewCartButton, JButton checkoutButton, JButton emptyCartButton, JButton exitButton) {
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Create menuLabel
        JLabel menuLabel = new JLabel("USER CONTROLS");
        menuLabel.setFont(menuLabel.getFont().deriveFont(Font.BOLD));
        menuLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(0, 2));
        centerPanel.add(findItemButton);
        centerPanel.add(addButton);
        centerPanel.add(viewCartButton);
        centerPanel.add(checkoutButton);
        centerPanel.add(emptyCartButton);
        centerPanel.add(exitButton);

        bottomPanel.add(menuLabel, BorderLayout.NORTH);
        bottomPanel.add(centerPanel, BorderLayout.CENTER);

        return bottomPanel;
    }

    
    

    private void updateAddButton() {
        // Update "Add Item" button text
        String addItemButtonText = "Add Item #" + cartItemCounter + " to Cart";
        addButton.setText(addItemButtonText);
    }
    
    private void updateFindButton() {
        // Update "Add Item" button text
        String findItemButtonText = "Find Item #" + findItemCounter;
        findItemButton.setText(findItemButtonText);
    }
    

    
    // Subtotal methods
    private void updateSubtotal() {
        // Calculate the current subtotal based on the items in the cart
        double subtotal = calculateSubtotal();
        // Update the subtotal field
        subtotalField.setText("$" + subtotal);
    }

    private double calculateSubtotal() {
        double subtotal = 0.0;
    
        // Consider all items in the cart, including the last found item
        for (Product cartItem : cart) {
            double salePercentage = calculateSalePercentage(cartItem.getQuantity());
            double total = calculateDiscountedPrice(cartItem.getPrice(), salePercentage);
            subtotal += total * cartItem.getQuantity();
        }
    
        // Add the subtotal of the last found item if it is not null
        if (lastFoundItem != null) {
            lastFoundItem.setQuantity(Integer.parseInt(quantityField.getText()));
            double salePercentage = calculateSalePercentage(lastFoundItem.getQuantity());
            double total = calculateDiscountedPrice(lastFoundItem.getPrice(), salePercentage);
            subtotal += total * lastFoundItem.getQuantity();
        }
    
        return subtotal;
    }
    
    
    
    
    

    private double calculateSalePercentage(int quantity) {
        // Calculate sale percentage based on quantity (5% for every 5 quantity)
        int baseQuantity = 5;
        return Math.floor(quantity / baseQuantity) * 5.0;
    }

    private double calculateDiscountedPrice(double originalPrice, double salePercentage) {
        // Calculate discounted price
        return originalPrice - (originalPrice * salePercentage / 100.0);
    }

    
    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public static void main(String[] args) {
        List<Product> existingProducts = new ReadCSV().readCSVFile("/Users/rj/eclipse-workspace/swinggui/src/inventory.csv");
        GUI manager = new GUI(existingProducts);
        manager.show();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
         
    }
}
