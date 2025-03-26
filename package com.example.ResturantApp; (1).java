package com.example.ResturantApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.io.*;
import java.util.ArrayList;

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
            return "";
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
    public class LoginSystem {
        @FXML private TextField userField;
        @FXML private TextField passField;
        @FXML private Label errorLabel;
        
        private boolean loggedIn = false;
        
        public void handleSubmit(ActionEvent event) {}
        
        public boolean validateSubmit() {
            return false;
        }
        
        public boolean isLoggedIn() {
            return loggedIn;
        }
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
    public void initialize() {}

    private void loadMenuItemsFromFile() {}

    @FXML
    private void switchToScreen(ActionEvent event) {}

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

    @FXML
    private void handleLogin(ActionEvent event) {}

    @FXML
    private void handleSignup(ActionEvent event) {}

    private boolean validateLogin(String username, String password) {
        return false;
    }

    private void saveUserCredentials(String username, String password) {}
}