package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GUI implements ActionListener {
    private JFrame frame;

    private JTextField idField;
    private JTextField quantityField;
    private JTextField detailsField;
    private JTextField subtotalField; // New text field for current subtotal

    private List<Product> productList;
    private List<Product> cart;
    
    private JButton addButton;
    private JButton findItemButton;

    private JPanel cartPanel;

    private int cartItemCounter;  // Counter for cart items
    private int findItemCounter; // Counter for items found

    public GUI(List<Product> productList) {
        this.productList = productList;
        this.cart = new ArrayList<>();
        this.cartItemCounter = 1;
        this.findItemCounter = 1;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Product Form");

        // Create components
        idField = new JTextField(10);
        quantityField = new JTextField(5);
        detailsField = new JTextField(20);
        detailsField.setEditable(false);  // Make it read-only

        // New text field for current subtotal
        subtotalField = new JTextField(20);
        subtotalField.setEditable(false);

        // Create "Add Product" button
        addButton = new JButton("Add Item #" + cartItemCounter + " To Cart");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductToCart();
            }
        });

        // Create "Find Item" button
        findItemButton = new JButton("Find Item #" + cartItemCounter);
        findItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findItemById();
            }
        });

        // Create cart panel
        cartPanel = createCartPanel();

        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCart();
            }
        });

        // Create "Exit (Close App)" button
        JButton exitButton = new JButton("Exit (Close App)");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        });

        // Create top panel with ID, Quantity, Details, and Subtotal fields
        JPanel topPanel = createTopPanel();

        // Create bottom panel with "Add Product," "Find Item," "View Cart," and "Exit (Close App)" buttons
        JPanel bottomPanel = createBottomPanel(addButton, findItemButton, viewCartButton, exitButton);

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

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();

        // Dynamically update the label text based on the number of items in the cart
        String itemLabelText = "Enter item ID for Item #" + (cartItemCounter) + ":";
        topPanel.add(new JLabel(itemLabelText));
        topPanel.add(idField);
        
        String quantityLabelText = "Enter quantity for Item #" + (cartItemCounter) + ":";
        topPanel.add(new JLabel(quantityLabelText));
        topPanel.add(quantityField);
        
        String detailsLabelText = "Details for item #" + (findItemCounter) + ":";
        topPanel.add(new JLabel(detailsLabelText));
        topPanel.add(detailsField);

        // New label for current subtotal
        topPanel.add(new JLabel("Current subtotal for 0 Item(s):"));
        topPanel.add(subtotalField);

        return topPanel;
    }

    private JPanel createBottomPanel(JButton addButton, JButton findItemButton, JButton viewCartButton, JButton exitButton) {
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        bottomPanel.add(findItemButton);
        bottomPanel.add(viewCartButton);
        bottomPanel.add(exitButton);
        return bottomPanel;
    }

    private void viewCart() {
        StringBuilder cartItems = new StringBuilder("Shopping Cart:\n");

        for (Product cartItem : cart) {
            cartItems.append(cartItem.getName()).append(" - Quantity: ").append(cartItem.getQuantity()).append("\n");
        }

        // Display a popup message with all items in the shopping cart
        JOptionPane.showMessageDialog(frame, cartItems.toString(), "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createCartPanel() {
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Your Current Shopping Cart with 1 Item(s)"));

        for (int i = 0; i < 6; i++) {
            JPanel cartItemPanel = new JPanel();
            JLabel cartLabel = new JLabel("Item " + (i + 1) + ":");
            JTextField cartTextField = new JTextField(20);
            cartTextField.setEditable(false);  // Make it read-only
            cartItemPanel.add(cartLabel);
            cartItemPanel.add(cartTextField);
            cartPanel.add(cartItemPanel);
        }

        return cartPanel;
    }

    private void addProductToCart() {
        try {
            String id = idField.getText();

            // Check if a quantity has been inputted
            if (quantityField.getText().isEmpty() && idField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please input a quantity and an id.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if a quantity has been inputted
            if (quantityField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please input a quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if an id has been inputted
            if (idField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please input an id.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText());

            // Search for the item in the product list
            for (Product product : productList) {
                if (product.getId().equals(id)) {
                    // Create a new product with the specified quantity and add it to the cart
                    Product cartItem = new Product(product.getId(), product.getName(), product.isAvailable(), quantity, product.getPrice());
                    cart.add(cartItem);

                    // Update the cart display
                    updateCartDisplay();

                    // Update the subtotal display
                    updateSubtotal();

                    // Optionally, you can print or display the added item in the cart
                    System.out.println("Added to Cart: " + cartItem.getName() + " - Quantity: " + cartItem.getQuantity());

                    // Clear the fields
                    clearFields();

                    // Increment the cart item counter for the next item
                    cartItemCounter++;

                    // Update the top panel to reflect the new item number
                    updateTopPanel();
                    updateAddButton();

                    return;  // Exit the method if the item is found and added to the cart
                }
            }

            // If the item is not found, display a message
            JOptionPane.showMessageDialog(frame, "Item not found in the product list.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
  

    private void findItemById() {
        try {
            String findId = idField.getText(); // Trim leading and trailing spaces
            String findQuantity = quantityField.getText(); // Trim leading and trailing spaces

            // Check if both ID and quantity have been provided
            if (findId.isEmpty() || findQuantity.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please input both ID and quantity to find.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Search for the item in the product list
            for (Product product : productList) {
                if (product.getId().equals(findId)) { // Trim leading and trailing spaces from the product ID
                    // Update the details field with all details of the found item
                    updateDetailsField(product);
                    updateDetailsText();
                    findItemCounter++;
                    updateFindButton();
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

    private void updateCartDisplay() {
        // Update the text fields in the cart panel with the latest items in the cart
        for (int i = 0; i < 6; i++) {
            if (i < cart.size()) {
                Product cartItem = cart.get(i);
                JPanel cartItemPanel = (JPanel) cartPanel.getComponent(i);
                JTextField cartTextField = (JTextField) cartItemPanel.getComponent(1);
                cartTextField.setText(cartItem.getName() + " - Quantity: " + cartItem.getQuantity());
            } else {
                // Clear any remaining text fields
                JPanel cartItemPanel = (JPanel) cartPanel.getComponent(i);
                JTextField cartTextField = (JTextField) cartItemPanel.getComponent(1);
                cartTextField.setText("");
            }
        }
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
                " Original Price: $" + originalPrice +
                " Quantity: " + quantity +
                " Sale Percentage: " + salePercentage + "%" +
                " Discounted Price: $" + discountedPrice);
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

    private void clearFields() {
        idField.setText("");
        quantityField.setText("");
    }

    private void updateTopPanel() {
        // Update the labels based on the new cart item counter
        String itemLabelText = "Enter item ID for Item #" + (cartItemCounter) + ":";
        String quantityLabelText = "Enter quantity for Item #" + (cartItemCounter) + ":";
        String subtotalLabelText = "Current subtotal for " + cart.size() + " Item(s): $";
        
        ((JLabel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(0)).setText(itemLabelText);
        ((JLabel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(2)).setText(quantityLabelText);
        ((JLabel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(6)).setText(subtotalLabelText);

    }
    
    private void updateDetailsText() {
        String detailsLabelText = "Details for item #" + (findItemCounter) + ":";
        ((JLabel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(4)).setText(detailsLabelText);
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
    
    private void updateSubtotal() {
        // Calculate the current subtotal based on the items in the cart
        double subtotal = calculateSubtotal();
        // Update the subtotal field
        subtotalField.setText("$" + subtotal);
    }

    private double calculateSubtotal() {
        double subtotal = 0;
        for (Product cartItem : cart) {
        	double unitPrice = cartItem.getPrice();
        	double initialTotal = unitPrice*cartItem.getQuantity();
        	
            double salePercentage = calculateSalePercentage(cartItem.getQuantity());
            double discountedPrice = calculateDiscountedPrice(initialTotal, salePercentage);
            subtotal += discountedPrice;
        }
        return subtotal;
    }

    private void exitApplication() {
        frame.dispose(); // Close the frame
        System.exit(0);   // Terminate the application
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
