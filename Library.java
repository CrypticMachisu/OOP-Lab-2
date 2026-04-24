import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Library extends Application {

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        // Create tabs with content
        Tab moviesTab = new Tab("Movies");
        Tab customersTab = new Tab("Customers");
        Tab rentalsTab = new Tab("Rentals");

        // Set content for each tab
        moviesTab.setContent(createMoviesContent());
        customersTab.setContent(createCustomersContent());
        rentalsTab.setContent(createRentalsContent());

        // Make tabs not closable
        moviesTab.setClosable(false);
        customersTab.setClosable(false);
        rentalsTab.setClosable(false);

        // Add tabs to TabPane
        tabPane.getTabs().addAll(moviesTab, customersTab, rentalsTab);

        // Create scene
        Scene scene = new Scene(tabPane, 750, 500);

        // Setup stage
        primaryStage.setTitle("Video Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ========== MOVIES TAB CONTENT ==========
    private VBox createMoviesContent() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #FAFAFA;");

        // Title
        Text title = new Text("Movie Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // Form GridPane
        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);
        formGrid.setHgap(15);
        formGrid.setAlignment(Pos.TOP_LEFT);

        // Movie Title
        Text nameLabel = new Text("Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        TextField nameField = new TextField();
        nameField.setPromptText("Enter movie title");
        nameField.setPrefWidth(250);

        // Genre
        Text genreLabel = new Text("Genre:");
        genreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        ComboBox<String> genreCombo = new ComboBox<>();
        genreCombo.getItems().addAll("Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Romance", "Thriller");
        genreCombo.setPromptText("Select genre");
        genreCombo.setPrefWidth(200);

        // Buttons
        Button saveButton = new Button("Save");
        Button removeButton = new Button("Remove");
        saveButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-padding: 8 20;");
        removeButton.setStyle("-fx-background-color: #C62828; -fx-text-fill: white; -fx-padding: 8 20;");

        HBox buttonBox = new HBox(15, saveButton, removeButton);

        // Add to grid
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(genreLabel, 0, 1);
        formGrid.add(genreCombo, 1, 1);
        formGrid.add(buttonBox, 1, 2);

        // Registered Section
        Text registeredTitle = new Text("Registered Movies");
        registeredTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All Genres", "Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Romance", "Thriller");
        filterCombo.setValue("All Genres");
        filterCombo.setPrefWidth(150);

        ListView<String> moviesList = new ListView<>();
        moviesList.getItems().addAll(
                "Inception (Sci-Fi)",
                "The Dark Knight (Action)",
                "Forrest Gump (Drama)",
                "The Hangover (Comedy)"
        );
        moviesList.setPrefHeight(180);

        VBox registeredBox = new VBox(10);
        registeredBox.setPadding(new Insets(15));
        registeredBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: #F5F5F5;");
        registeredBox.getChildren().addAll(registeredTitle, filterCombo, moviesList);

        // Add all to main box
        mainBox.getChildren().addAll(title, formGrid, registeredBox);

        // Event handlers
        saveButton.setOnAction(e -> {
            if (!nameField.getText().isEmpty() && genreCombo.getValue() != null) {
                moviesList.getItems().add(nameField.getText() + " (" + genreCombo.getValue() + ")");
                nameField.clear();
                genreCombo.setValue(null);
                showAlert("Success", "Movie added successfully!");
            } else {
                showAlert("Error", "Please enter movie title and select genre");
            }
        });

        removeButton.setOnAction(e -> {
            String selected = moviesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                moviesList.getItems().remove(selected);
                showAlert("Success", "Movie removed successfully!");
            } else {
                showAlert("Error", "Please select a movie to remove");
            }
        });

        return mainBox;
    }

    // ========== CUSTOMERS TAB CONTENT ==========
    private VBox createCustomersContent() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #FAFAFA;");

        // Title
        Text title = new Text("Customer Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // Form GridPane
        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);
        formGrid.setHgap(15);

        Text nameLabel = new Text("Full Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        TextField nameField = new TextField();
        nameField.setPromptText("Enter customer name");
        nameField.setPrefWidth(250);

        Text emailLabel = new Text("Email:");
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email address");

        Text phoneLabel = new Text("Phone:");
        phoneLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter phone number");

        // Buttons
        Button saveButton = new Button("Save Customer");
        Button removeButton = new Button("Remove Customer");
        saveButton.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white; -fx-padding: 8 20;");
        removeButton.setStyle("-fx-background-color: #C62828; -fx-text-fill: white; -fx-padding: 8 20;");

        HBox buttonBox = new HBox(15, saveButton, removeButton);

        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(emailLabel, 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(phoneLabel, 0, 2);
        formGrid.add(phoneField, 1, 2);
        formGrid.add(buttonBox, 1, 3);

        // Registered Section
        Text registeredTitle = new Text("Registered Customers");
        registeredTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField searchField = new TextField();
        searchField.setPromptText("Search customers...");

        ListView<String> customersList = new ListView<>();
        customersList.getItems().addAll(
                "John Smith | john@email.com | 555-0101",
                "Sarah Johnson | sarah@email.com | 555-0102",
                "Michael Brown | michael@email.com | 555-0103"
        );
        customersList.setPrefHeight(200);

        VBox registeredBox = new VBox(10);
        registeredBox.setPadding(new Insets(15));
        registeredBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: #F5F5F5;");
        registeredBox.getChildren().addAll(registeredTitle, searchField, customersList);

        mainBox.getChildren().addAll(title, formGrid, registeredBox);

        // Event handlers
        saveButton.setOnAction(e -> {
            if (!nameField.getText().isEmpty()) {
                String customer = nameField.getText() + " | " + emailField.getText() + " | " + phoneField.getText();
                customersList.getItems().add(customer);
                nameField.clear();
                emailField.clear();
                phoneField.clear();
                showAlert("Success", "Customer added successfully!");
            } else {
                showAlert("Error", "Please enter customer name");
            }
        });

        removeButton.setOnAction(e -> {
            String selected = customersList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                customersList.getItems().remove(selected);
                showAlert("Success", "Customer removed successfully!");
            } else {
                showAlert("Error", "Please select a customer to remove");
            }
        });

        return mainBox;
    }

    // ========== RENTALS TAB CONTENT ==========
    private VBox createRentalsContent() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #FAFAFA;");

        // Title
        Text title = new Text("Rental Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // Form GridPane
        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);
        formGrid.setHgap(15);

        // Customer selection
        Text customerLabel = new Text("Customer:");
        customerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        ComboBox<String> customerCombo = new ComboBox<>();
        customerCombo.getItems().addAll("John Smith", "Sarah Johnson", "Michael Brown");
        customerCombo.setPromptText("Select customer");
        customerCombo.setPrefWidth(200);

        // Genre selection
        Text genreLabel = new Text("Genre:");
        genreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        ComboBox<String> genreCombo = new ComboBox<>();
        genreCombo.getItems().addAll("Action", "Comedy", "Drama", "Horror", "Sci-Fi");
        genreCombo.setPromptText("Select genre");

        // Movies selection
        Text movieLabel = new Text("Movies:");
        movieLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        ComboBox<String> movieCombo = new ComboBox<>();
        movieCombo.setPromptText("Select movie");
        movieCombo.setPrefWidth(250);

        // Buttons
        Button saveButton = new Button("Save Rental");
        Button returnButton = new Button("Return Movie");
        saveButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-padding: 8 20;");
        returnButton.setStyle("-fx-background-color: #E65100; -fx-text-fill: white; -fx-padding: 8 20;");

        HBox buttonBox = new HBox(15, saveButton, returnButton);

        formGrid.add(customerLabel, 0, 0);
        formGrid.add(customerCombo, 1, 0);
        formGrid.add(genreLabel, 0, 1);
        formGrid.add(genreCombo, 1, 1);
        formGrid.add(movieLabel, 0, 2);
        formGrid.add(movieCombo, 1, 2);
        formGrid.add(buttonBox, 1, 3);

        // Borrowed and Returned sections
        Text borrowedTitle = new Text("Borrowed Movies");
        borrowedTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        ListView<String> borrowedList = new ListView<>();
        borrowedList.setPrefHeight(120);

        Text returnedTitle = new Text("Returned Movies");
        returnedTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        ListView<String> returnedList = new ListView<>();
        returnedList.setPrefHeight(120);

        VBox borrowedBox = new VBox(10);
        borrowedBox.setPadding(new Insets(10));
        borrowedBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: #FFF8E1;");
        borrowedBox.getChildren().addAll(borrowedTitle, borrowedList);

        VBox returnedBox = new VBox(10);
        returnedBox.setPadding(new Insets(10));
        returnedBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: #E8F5E9;");
        returnedBox.getChildren().addAll(returnedTitle, returnedList);

        HBox listsBox = new HBox(20, borrowedBox, returnedBox);

        mainBox.getChildren().addAll(title, formGrid, listsBox);

        // Event handlers
        genreCombo.setOnAction(e -> {
            movieCombo.getItems().clear();
            String genre = genreCombo.getValue();
            if ("Action".equals(genre)) {
                movieCombo.getItems().addAll("The Dark Knight", "John Wick", "Mad Max");
            } else if ("Comedy".equals(genre)) {
                movieCombo.getItems().addAll("The Hangover", "Superbad");
            } else if ("Drama".equals(genre)) {
                movieCombo.getItems().addAll("Forrest Gump", "The Shawshank Redemption");
            }
        });

        saveButton.setOnAction(e -> {
            if (customerCombo.getValue() != null && movieCombo.getValue() != null) {
                borrowedList.getItems().add(movieCombo.getValue());
                showAlert("Success", "Movie rented to " + customerCombo.getValue());
            } else {
                showAlert("Error", "Please select customer and movie");
            }
        });

        returnButton.setOnAction(e -> {
            String selected = borrowedList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                borrowedList.getItems().remove(selected);
                returnedList.getItems().add(selected);
                showAlert("Success", "Movie returned successfully!");
            } else {
                showAlert("Error", "Please select a movie to return");
            }
        });

        return mainBox;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

}