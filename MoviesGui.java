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

class MoviesGUI extends VBox {

    private TextField titleField;
    private ComboBox<String> genreComboBox;
    private ComboBox<String> registeredComboBox;
    private ListView<String> moviesListView;
    private ObservableList<String> moviesList;
    private ObservableList<String> genres;

    public MoviesGUI() {
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
        Text titleText = new Text("Movie Management");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Labels and input fields
        Text nameLabel = new Text("Movie Title:");
        nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        titleField = new TextField();
        titleField.setPromptText("Enter movie title");
        titleField.setPrefWidth(250);

        Text genreLabel = new Text("Genre:");
        genreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        // Sample genres - would come from database
        genres = FXCollections.observableArrayList(
                "Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Romance", "Thriller"
        );
        genreComboBox = new ComboBox<>(genres);
        genreComboBox.setPromptText("Select genre");

        // Buttons
        Button saveButton = new Button("Save Movie");
        Button removeButton = new Button("Remove Movie");

        // Style buttons
        saveButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-size: 12pt; -fx-padding: 8 15;");
        removeButton.setStyle("-fx-background-color: #C62828; -fx-text-fill: white; -fx-font-size: 12pt; -fx-padding: 8 15;");

        // Registered section
        Text registeredLabel = new Text("Registered:");
        registeredLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Text filterLabel = new Text("Filter by Genre:");
        filterLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        registeredComboBox = new ComboBox<>(genres);
        registeredComboBox.setPromptText("All Genres");
        registeredComboBox.setPrefWidth(150);

        moviesList = FXCollections.observableArrayList();
        moviesListView = new ListView<>(moviesList);
        moviesListView.setPrefHeight(200);

        // Add sample data
        addSampleMovies();

        // Arrange nodes in GridPane
        formPane.add(nameLabel, 0, 0);
        formPane.add(titleField, 1, 0);
        formPane.add(genreLabel, 0, 1);
        formPane.add(genreComboBox, 1, 1);
        formPane.add(saveButton, 0, 2);
        formPane.add(removeButton, 1, 2);

        // Registered section VBox
        VBox registeredBox = new VBox(10);
        registeredBox.setPadding(new Insets(20));
        registeredBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: #F5F5F5;");
        registeredBox.getChildren().addAll(registeredLabel, filterLabel, registeredComboBox, moviesListView);

        // Add everything to main VBox
        getChildren().addAll(titleText, formPane, registeredBox);
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #FAFAFA;");

        // Event handlers
        saveButton.setOnAction(e -> saveMovie());
        removeButton.setOnAction(e -> removeMovie());
        registeredComboBox.setOnAction(e -> filterMoviesByGenre());
    }

    private void saveMovie() {
        String title = titleField.getText();
        String genre = genreComboBox.getValue();

        if (title == null || title.trim().isEmpty()) {
            showAlert("Error", "Please enter a movie title");
            return;
        }

        if (genre == null) {
            showAlert("Error", "Please select a genre");
            return;
        }

        moviesList.add(title + " (" + genre + ")");
        titleField.clear();
        genreComboBox.setValue(null);

        showAlert("Success", "Movie added successfully!");
    }

    private void removeMovie() {
        String selected = moviesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            moviesList.remove(selected);
            showAlert("Success", "Movie removed successfully!");
        } else {
            showAlert("Error", "Please select a movie to remove");
        }
    }

    private void filterMoviesByGenre() {
        String selectedGenre = registeredComboBox.getValue();
        if (selectedGenre == null) {
            // Show all movies
            // In real implementation, you'd filter from database
        }
        // Filter logic would go here
    }

    private void addSampleMovies() {
        moviesList.addAll(
                "Inception (Sci-Fi)",
                "The Dark Knight (Action)",
                "Forrest Gump (Drama)",
                "The Hangover (Comedy)"
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