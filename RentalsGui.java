import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RentalsGui extends VBox {

    private ComboBox<String> customerComboBox;
    private ComboBox<String> genreComboBox;
    private ComboBox<String> moviesComboBox;
    private ListView<String> borrowedListView;
    private ListView<String> returnedListView;
    private ObservableList<String> customers;
    private ObservableList<String> genres;
    private ObservableList<String> movies;
    private ObservableList<String> borrowedList;
    private ObservableList<String> returnedList;

    public RentalsGui() {
        setupUI();
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #FAFAFA;");

        // Title
        Text titleText = new Text("Rental Management");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Create form section
        GridPane formPane = new GridPane();
        formPane.setPadding(new Insets(15));
        formPane.setVgap(15);
        formPane.setHgap(15);
        formPane.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: #FFFFFF;");

        // Customer selection
        Text customerLabel = new Text("Customer:");
        customerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        customers = FXCollections.observableArrayList();
        customerComboBox = new ComboBox<>(customers);
        customerComboBox.setPromptText("Select customer");
        customerComboBox.setPrefWidth(200);

        // Genre selection
        Text genreLabel = new Text("Genre:");
        genreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        genres = FXCollections.observableArrayList(
                "Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Romance", "Thriller"
        );
        genreComboBox = new ComboBox<>(genres);
        genreComboBox.setPromptText("Select genre");

        // Movies selection
        Text moviesLabel = new Text("Movies:");
        moviesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        movies = FXCollections.observableArrayList();
        moviesComboBox = new ComboBox<>(movies);
        moviesComboBox.setPromptText("Select movie");
        moviesComboBox.setPrefWidth(250);

        // Buttons
        Button saveRentalButton = new Button("Save Rental");
        Button returnMovieButton = new Button("Return Movie");

        saveRentalButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-size: 12pt; -fx-padding: 8 20;");
        returnMovieButton.setStyle("-fx-background-color: #E65100; -fx-text-fill: white; -fx-font-size: 12pt; -fx-padding: 8 20;");

        HBox buttonBox = new HBox(20, saveRentalButton, returnMovieButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Arrange form elements
        formPane.add(customerLabel, 0, 0);
        formPane.add(customerComboBox, 1, 0);
        formPane.add(genreLabel, 0, 1);
        formPane.add(genreComboBox, 1, 1);
        formPane.add(moviesLabel, 0, 2);
        formPane.add(moviesComboBox, 1, 2);
        formPane.add(buttonBox, 1, 3);

        // Borrowed and Returned sections
        Text borrowedLabel = new Text("Borrowed Movies");
        borrowedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        borrowedList = FXCollections.observableArrayList();
        borrowedListView = new ListView<>(borrowedList);
        borrowedListView.setPrefHeight(150);

        Text returnedLabel = new Text("Returned Movies");
        returnedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        returnedList = FXCollections.observableArrayList();
        returnedListView = new ListView<>(returnedList);
        returnedListView.setPrefHeight(150);

        VBox borrowedBox = new VBox(10);
        borrowedBox.setPadding(new Insets(15));
        borrowedBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: #FFF8E1;");
        borrowedBox.getChildren().addAll(borrowedLabel, borrowedListView);

        VBox returnedBox = new VBox(10);
        returnedBox.setPadding(new Insets(15));
        returnedBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: #E8F5E9;");
        returnedBox.getChildren().addAll(returnedLabel, returnedListView);

        HBox listsBox = new HBox(20, borrowedBox, returnedBox);
        listsBox.setAlignment(Pos.CENTER);

        // Add everything to main VBox
        getChildren().addAll(titleText, formPane, listsBox);

        // Event handlers
        customerComboBox.setOnAction(e -> loadCustomerRentals());
        genreComboBox.setOnAction(e -> loadMoviesByGenre());
        saveRentalButton.setOnAction(e -> saveRental());
        returnMovieButton.setOnAction(e -> returnMovie());

        // Add sample data
        addSampleData();
    }

    private void saveRental() {
        String customer = customerComboBox.getValue();
        String movie = moviesComboBox.getValue();

        if (customer == null) {
            showAlert("Error", "Please select a customer");
            return;
        }

        if (movie == null) {
            showAlert("Error", "Please select a movie");
            return;
        }

        // Check if movie is already borrowed
        if (borrowedList.contains(movie)) {
            showAlert("Error", "This movie is already borrowed by the customer");
            return;
        }

        borrowedList.add(movie);
        showAlert("Success", "Movie rented successfully to " + customer);
    }

    private void returnMovie() {
        String selectedMovie = borrowedListView.getSelectionModel().getSelectedItem();

        if (selectedMovie != null) {
            borrowedList.remove(selectedMovie);
            returnedList.add(selectedMovie);
            showAlert("Success", "Movie returned successfully!");
        } else {
            showAlert("Error", "Please select a movie to return");
        }
    }

    private void loadCustomerRentals() {
        String selectedCustomer = customerComboBox.getValue();
        if (selectedCustomer != null) {
            // In real implementation, load rentals from database
            borrowedList.clear();
            returnedList.clear();

            // Sample data based on customer
            if (selectedCustomer.contains("John Smith")) {
                borrowedList.addAll("Inception (Sci-Fi)", "The Dark Knight (Action)");
                returnedList.add("Forrest Gump (Drama)");
            } else if (selectedCustomer.contains("Sarah Johnson")) {
                borrowedList.add("The Hangover (Comedy)");
            }
        }
    }

    private void loadMoviesByGenre() {
        String selectedGenre = genreComboBox.getValue();
        movies.clear();

        if (selectedGenre != null) {
            // Sample movies by genre
            switch (selectedGenre) {
                case "Action":
                    movies.addAll("The Dark Knight", "John Wick", "Mad Max");
                    break;
                case "Comedy":
                    movies.addAll("The Hangover", "Superbad", "Bridesmaids");
                    break;
                case "Drama":
                    movies.addAll("Forrest Gump", "The Shawshank Redemption", "The Green Mile");
                    break;
                case "Sci-Fi":
                    movies.addAll("Inception", "Interstellar", "The Matrix");
                    break;
                default:
                    movies.addAll("Sample Movie 1", "Sample Movie 2");
            }
        }
    }

    private void addSampleData() {
        customers.addAll(
                "John Smith",
                "Sarah Johnson",
                "Michael Brown",
                "Emily Davis"
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