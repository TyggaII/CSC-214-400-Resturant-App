package com.example.resturantapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    private final String LOGIN_FILE = "login.txt";


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
            return name + " -$" + String.format("%.2f, price);
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
    // can be anything from food to gift cards
    public class SingleItem extends MenuItem {
        public SingleItem(String name, double price) {
            super(name, price);
        }
    }

    //Login class
    // Add these methods to handle login/signup
    @FXML
    private void handleSignup(ActionEvent event) {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessage.setText("Username and password required");
            return;
        }

        try {
            // Create a new file if one doesn't exist
            File loginFile = new File(LOGIN_FILE);
            if (!loginFile.exists()) {
                loginFile.createNewFile();
            }

            // Checks user exist in login file
            if (isUsernameTaken(username)) {
                loginMessage.setText("Username already taken");
                return;
            }

            // Save new credentials (format: username:password)
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

        try {//if you haven't signed up yet
            File loginFile = new File(LOGIN_FILE);
            if (!loginFile.exists()) {
                loginMessage.setText("No accounts exist yet");
                return;
            }

            Scanner scanner = new Scanner(loginFile);//searches for your username and password
            while (scanner.hasNextLine()) {
                String[] credentials = scanner.nextLine().split(":");
                if (credentials.length == 2 &&
                        credentials[0].equals(username) &&
                        credentials[1].equals(password)) {
                    loginMessage.setText("Login successful!");

                    //hides all screens, ran into an error when it just showed up completely white so had to put this in here
                    for (javafx.scene.Node node : mainPane.getChildren()) {
                        if (node instanceof VBox) {
                            node.setVisible(false);
                        }
                    }

                    // Switches back to the main menu screen, ran into an error where it just crashes when you put in a succesful login so had to put this here
                    for (javafx.scene.Node node : mainPane.getChildren()) {
                        if (node.getId() != null && node.getId().equals("mainMenuScreen")) {
                            node.setVisible(true);
                            break;
                        }
                    }
                    return;
                }
            }
            loginMessage.setText("Invalid credentials");
        } catch (IOException e) {
            loginMessage.setText("Error reading login file");
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

    //A.t.c class
    public class AboutTheCompany {
        @FXML private Label titleLabel;
        @FXML private TextArea aboutText;

        public void loadContent() {}
    }

    //complaint class
    public class IssueComplaint {
        @FXML private TextField complaintText;
        @FXML private TextField custName;
        @FXML private TextField custEmail;

        public void handleSubmit(ActionEvent event) {}

        public boolean validateEmail() {
            return false;
        }
    }

    //Methods for main functions
    @FXML
    public void initialize() {
        itemTypeComboBox.getItems().addAll("Meal", "Drink", "Single Item");
        itemTypeComboBox.getSelectionModel().selectFirst();
        loadMenuItemsFromFile();
    }


 private void loadMenuItemsFromFile() {
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
    private void handleAddToOrder(ActionEvent event) {}

    @FXML
    private void handleAddMenuItem(ActionEvent event) {}

    private void saveMenuItemToFile(String itemType, MenuItem item) {}

    @FXML
    private void handleGenerateReceipt(ActionEvent event) {}

    @FXML
    private void handleSubmitComplaint(ActionEvent event) {}

    @FXML
    private void showAboutCompany(ActionEvent event) {}



}
