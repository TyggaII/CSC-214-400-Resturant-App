package com.example.resturantapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResturantAppController {

    // the various ui
    @FXML private StackPane mainPane;
    @FXML private ListView<MenuItem> mealListView;
    @FXML private ListView<MenuItem> drinkListView;
    @FXML private ListView<MenuItem> singleItemListView;
    @FXML private ListView<MenuItem> selectedItemsList;
    @FXML private Label totalPriceLabel;
    @FXML private TextField itemNameInput;
    @FXML private TextField itemPriceInput;
    @FXML private ComboBox<String> itemTypeComboBox;
    @FXML private TextField loginUsername;
    @FXML private TextField loginPassword;
    @FXML private Label loginMessage;
    @FXML private Button staffButton;
    @FXML private Label loggedInUserLabel;

    // to compile data in arrays
    private final ArrayList<MenuItem> meals = new ArrayList<>();
    private final ArrayList<MenuItem> drinks = new ArrayList<>();
    private final ArrayList<MenuItem> singleItems = new ArrayList<>();
    private final ArrayList<MenuItem> selectedItems = new ArrayList<>();

    // overall price
    private double totalPrice = 0.0;

    // to call files
    private final String MENU_FILE = "menu.txt";
    private final String RECEIPT_FILE = "receipt.txt";
    private final String COMPLAINT_FILE = "complaint.txt";
    private final String LOGIN_FILE = "login.txt";//file
    private final String STAFF_FILE = "staff.txt"; //file for staff credentials
    private String currentUser = null;
    private boolean isStaff = false;

    public abstract class MenuItem {
        private final String name;
        private final double price;

        public MenuItem(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return name + " - $" + String.format("%.2f", price);
        }
    }

    //Menu items classes
    public class Meal extends MenuItem {
        public Meal(String name, double price) {
            super(name, price);
        }
    }

    public class Drink extends MenuItem {
        public Drink(String name, double price) {
            super(name, price);
        }
    }

    public class SingleItem extends MenuItem {
        public SingleItem(String name, double price) {
            super(name, price);
        }
    }

    @FXML
    public void initialize() {
        itemTypeComboBox.getItems().addAll("Meal", "Drink", "Single Item");
        itemTypeComboBox.getSelectionModel().selectFirst();
        loadMenuItemsFromFile();
    }

    private void loadMenuItemsFromFile() {//Loads items from file
        File file = new File(MENU_FILE);
        if (!file.exists()) {
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String itemType = parts[0];
                    String itemName = parts[1];
                    double itemPrice = Double.parseDouble(parts[2]);

                    MenuItem newItem;
                    switch (itemType) {
                        case "Meal":
                            newItem = new Meal(itemName, itemPrice);
                            meals.add(newItem);
                            mealListView.getItems().add(newItem);
                            break;
                        case "Drink":
                            newItem = new Drink(itemName, itemPrice);
                            drinks.add(newItem);
                            drinkListView.getItems().add(newItem);
                            break;
                        case "Single Item":
                            newItem = new SingleItem(itemName, itemPrice);
                            singleItems.add(newItem);
                            singleItemListView.getItems().add(newItem);
                            break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Failed to load menu items from the file. " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format in the menu file. " + e.getMessage());
        }
    }

    @FXML
    private void switchToScreen(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String screenId = sourceButton.getId() + "Screen";

        for (javafx.scene.Node node : mainPane.getChildren()) {
            if (node instanceof VBox) {
                node.setVisible(false);
            }
        }

        // Shows current menu screen
        for (javafx.scene.Node node : mainPane.getChildren()) {
            if (node.getId() != null && node.getId().equals(screenId)) {
                node.setVisible(true);
                break;
            }
        }

        //takes you back to the main menu
        if (sourceButton.getId().equals("mainMenu")) {
            for (javafx.scene.Node node : mainPane.getChildren()) {
                if (node.getId() != null && node.getId().equals("mainMenuScreen")) {
                    node.setVisible(true);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleAddToOrder(ActionEvent event) {//adds customer order
        MenuItem selectedItem = null;

        if (mealListView.isVisible() && mealListView.getSelectionModel().getSelectedItem() != null) {
            selectedItem = mealListView.getSelectionModel().getSelectedItem();
        } else if (drinkListView.isVisible() && drinkListView.getSelectionModel().getSelectedItem() != null) {
            selectedItem = drinkListView.getSelectionModel().getSelectedItem();
        } else if (singleItemListView.isVisible() && singleItemListView.getSelectionModel().getSelectedItem() != null) {
            selectedItem = singleItemListView.getSelectionModel().getSelectedItem();
        }

        if (selectedItem != null) {
            selectedItems.add(selectedItem);
            selectedItemsList.getItems().add(selectedItem);
            totalPrice += selectedItem.getPrice();
            totalPriceLabel.setText(String.format("Total: $%.2f", totalPrice));

            if (mealListView.isVisible()) {
                mealListView.getSelectionModel().clearSelection();
            } else if (drinkListView.isVisible()) {
                drinkListView.getSelectionModel().clearSelection();
            } else if (singleItemListView.isVisible()) {
                singleItemListView.getSelectionModel().clearSelection();
            }
        } else {
            System.out.println("Error: Please select an item to add to the order.");
        }
    }

    @FXML
    private void handleAddMenuItem(ActionEvent event) {//adds item 
        String itemName = itemNameInput.getText();
        String itemPriceText = itemPriceInput.getText();
        String itemType = itemTypeComboBox.getSelectionModel().getSelectedItem();

        if (itemName.isEmpty() || itemPriceText.isEmpty()) {
            System.out.println("Error: Please enter both item name and price.");
            return;
        }

        double itemPrice;
        try {
            itemPrice = Double.parseDouble(itemPriceText);
        } catch (NumberFormatException e) {
            System.out.println("Error: Price must be a valid number.");
            return;
        }

        MenuItem newItem;//handles new item creation
        if ("Meal".equals(itemType)) {
            newItem = new Meal(itemName, itemPrice);
            meals.add(newItem);
            mealListView.getItems().add(newItem);
        } else if ("Drink".equals(itemType)) {
            newItem = new Drink(itemName, itemPrice);
            drinks.add(newItem);
            drinkListView.getItems().add(newItem);
        } else if ("Single Item".equals(itemType)) {
            newItem = new SingleItem(itemName, itemPrice);
            singleItems.add(newItem);
            singleItemListView.getItems().add(newItem);
        } else {
            return;
        }

        saveMenuItemToFile(itemType, newItem);

        itemNameInput.clear();
        itemPriceInput.clear();
    }

    private void saveMenuItemToFile(String itemType, MenuItem item) {//saves newly added menu items to file
        try (PrintWriter writer = new PrintWriter(new FileWriter(MENU_FILE, true))) {
            writer.println(itemType + "," + item.getName() + "," + item.getPrice());
        } catch (IOException e) {
            System.out.println("Error: Failed to save the menu item to the file. " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateReceipt(ActionEvent event) {//generates the overall recipt of order
        if (selectedItems.isEmpty()) {
            System.out.println("No items have been selected for the receipt.");
            return;
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("Receipt:\n\n");

        for (MenuItem item : selectedItems) {
            receipt.append(item.toString()).append("\n");
        }

        receipt.append("\nTotal: $").append(String.format("%.2f", totalPrice)).append("\n");

        System.out.println(receipt);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECEIPT_FILE))) {
            writer.write(receipt.toString());
        } catch (IOException e) {
            System.out.println("Error: Failed to save receipt to file. " + e.getMessage());
        }
    }

    // Login/Signup
    @FXML
    private void handleSignup(ActionEvent event) {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessage.setText("Username and password required");
            return;
        }

        try {
            File loginFile = new File(LOGIN_FILE);
            if (!loginFile.exists()) {
                loginFile.createNewFile();
            }

            if (isUsernameTaken(username)) {
                loginMessage.setText("Username already taken");
                return;
            }

            FileWriter writer = new FileWriter(LOGIN_FILE, true);
            writer.write(username + ":" + password + "\n");
            writer.close();

            loginMessage.setText("Account created successfully!");
        } catch (IOException e) {
            loginMessage.setText("Error creating account");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        try {
            // Checks if the credentials are staff
            File staffFile = new File(STAFF_FILE);
            if (staffFile.exists()) {
                Scanner staffScanner = new Scanner(staffFile);
                while (staffScanner.hasNextLine()) {
                    String[] credentials = staffScanner.nextLine().split(":");
                    if (credentials.length == 2 &&
                            credentials[0].equals(username) &&
                            credentials[1].equals(password)) {
                        currentUser = username;
                        isStaff = true;
                        updateUIAfterLogin();
                        return;
                    }
                }
            }

            // Checks if the credentials are normal user
            File loginFile = new File(LOGIN_FILE);
            if (!loginFile.exists()) {
                loginMessage.setText("No accounts exist yet");
                return;
            }

            Scanner scanner = new Scanner(loginFile);
            while (scanner.hasNextLine()) {
                String[] credentials = scanner.nextLine().split(":");
                if (credentials.length == 2 &&
                        credentials[0].equals(username) &&
                        credentials[1].equals(password)) {
                    currentUser = username;
                    isStaff = false;
                    updateUIAfterLogin();
                    return;
                }
            }
            loginMessage.setText("Invalid credentials");
        } catch (IOException e) {
            loginMessage.setText("Error reading login file");
        }
    }

    private void updateUIAfterLogin() {
        loginMessage.setText("Login successful!");
        loggedInUserLabel.setText("Logged in as: " + currentUser);
        staffButton.setVisible(isStaff);

        for (javafx.scene.Node node : mainPane.getChildren()) {
            if (node instanceof VBox) {
                node.setVisible(false);
            }
        }

        for (javafx.scene.Node node : mainPane.getChildren()) {
            if (node.getId() != null && node.getId().equals("mainMenuScreen")) {
                node.setVisible(true);
                break;
            }
        }
    }


    private boolean isUsernameTaken(String username) throws IOException {
        File loginFile = new File(LOGIN_FILE);
        if (!loginFile.exists()) return false;

        Scanner scanner = new Scanner(loginFile);
        while (scanner.hasNextLine()) {
            String[] credentials = scanner.nextLine().split(":");
            if (credentials.length > 0 && credentials[0].equals(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLogin(String username, String password) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(LOGIN_FILE));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading login file");
        }
        return false;
    }

    
    @FXML
    private void handleSubmitComplaint(ActionEvent event) {}

    @FXML
    private void showAboutCompany(ActionEvent event) {}
}
