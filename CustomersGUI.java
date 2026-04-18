import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CustomersGUI extends VBox {

    private TextField nameField;
    private TextField emailField;
    private TextField phoneField;
    private ListView<String> customersListView;
    private ObservableList<String> customersList;

    public CustomersGUI() {
        setupUI();
    }

    private void setupUI() {
        // Create GridPane for form layout
        GridPane formPane = new GridPane();
        formPane.setPadding(new Insets(20));
        formPane.setVgap(15);
        formPane.setHgap(15);
        formPane.setAlignment(Pos.TOP_CENTER);

        // Title
        Text titleText = new Text("Customer Management");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Labels and input fields
        Text nameLabel = new Text("Full Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        nameField = new TextField();
        nameField.setPromptText("Enter customer name");
        nameField.setPrefWidth(250);

        Text emailLabel = new Text("Email:");
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        emailField = new TextField();
        emailField.setPromptText("Enter email address");

        Text phoneLabel = new Text("Phone:");
        phoneLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        phoneField = new TextField();
        phoneField.setPromptText("Enter phone number");

        // Buttons
        Button saveButton = new Button("Save Customer");
        Button removeButton = new Button("Remove Customer");
        Button updateButton = new Button("Update Customer");

        // Style buttons
        String buttonStyle = "-fx-background-color: #1565C0; -fx-text-fill: white; -fx-font-size: 12pt; -fx-padding: 8 15;";
        saveButton.setStyle(buttonStyle);
        removeButton.setStyle("-fx-background-color: #C62828; -fx-text-fill: white; -fx-font-size: 12pt; -fx-padding: 8 15;");
        updateButton.setStyle("-fx-background-color: #FF8F00; -fx-text-fill: white; -fx-font-size: 12pt; -fx-padding: 8 15;");

        // Registered section
        Text registeredLabel = new Text("Registered Customers");
        registeredLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        customersList = FXCollections.observableArrayList();
        customersListView = new ListView<>(customersList);
        customersListView.setPrefHeight(250);

        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search customers...");

        // Arrange nodes in GridPane
        formPane.add(nameLabel, 0, 0);
        formPane.add(nameField, 1, 0);
        formPane.add(emailLabel, 0, 1);
        formPane.add(emailField, 1, 1);
        formPane.add(phoneLabel, 0, 2);
        formPane.add(phoneField, 1, 2);
        formPane.add(saveButton, 0, 3);
        formPane.add(updateButton, 1, 3);
        formPane.add(removeButton, 2, 3);

        // Registered section VBox
        VBox registeredBox = new VBox(10);
        registeredBox.setPadding(new Insets(20));
        registeredBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: #F5F5F5;");
        registeredBox.getChildren().addAll(registeredLabel, searchField, customersListView);

        // Add everything to main VBox
        getChildren().addAll(titleText, formPane, registeredBox);
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #FAFAFA;");

        // Event handlers
        saveButton.setOnAction(e -> saveCustomer());
        removeButton.setOnAction(e -> removeCustomer());
        updateButton.setOnAction(e -> updateCustomer());

        customersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadCustomerData(newVal);
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchCustomers(newVal));

        // Add sample data
        addSampleCustomers();
    }

    private void saveCustomer() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (name == null || name.trim().isEmpty()) {
            showAlert("Error", "Please enter customer name");
            return;
        }

        String customerInfo = name + " | " + (email != null ? email : "N/A") + " | " + (phone != null ? phone : "N/A");
        customersList.add(customerInfo);
        clearFields();

        showAlert("Success", "Customer added successfully!");
    }

    private void removeCustomer() {
        String selected = customersListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            customersList.remove(selected);
            clearFields();
            showAlert("Success", "Customer removed successfully!");
        } else {
            showAlert("Error", "Please select a customer to remove");
        }
    }

    private void updateCustomer() {
        String selected = customersListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int index = customersList.indexOf(selected);
            String updatedInfo = nameField.getText() + " | " + emailField.getText() + " | " + phoneField.getText();
            customersList.set(index, updatedInfo);
            clearFields();
            showAlert("Success", "Customer updated successfully!");
        } else {
            showAlert("Error", "Please select a customer to update");
        }
    }

    private void loadCustomerData(String customerInfo) {
        String[] parts = customerInfo.split(" \\| ");
        if (parts.length >= 1) {
            nameField.setText(parts[0]);
            if (parts.length > 1) emailField.setText(parts[1]);
            if (parts.length > 2) phoneField.setText(parts[2]);
        }
    }

    private void searchCustomers(String query) {
        // Search logic would go here
        if (query == null || query.trim().isEmpty()) {
            // Show all customers
        }
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
    }

    private void addSampleCustomers() {
        customersList.addAll(
                "John Smith | john@email.com | 555-0101",
                "Sarah Johnson | sarah@email.com | 555-0102",
                "Michael Brown | michael@email.com | 555-0103",
                "Emily Davis | emily@email.com | 555-0104"
        );
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}