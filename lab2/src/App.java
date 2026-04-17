import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * Movie Library System — main application class.
 *
 * The app is organised into four tabs:
 * 1. Genres — add / remove genre categories
 * 2. Movies — add / remove movies linked to a genre
 * 3. Customers — register / remove customers
 * 4. Rentals — rent a movie to a customer and track returns
 *
 * All data is stored in ObservableLists and Maps declared as fields so
 * every tab can read and write the same data (shared state).
 */
public class App extends Application {

    // ──────────────────────────────────────────────────────────────
    // SHARED DATA
    // These collections are the single source of truth for the app.
    // Because they are ObservableLists, JavaFX ComboBoxes that are
    // bound to them automatically update when items are added/removed.
    // ──────────────────────────────────────────────────────────────

    /** Names of all genres that have been saved */
    private final ObservableList<String> genreNames = FXCollections.observableArrayList();

    /** Maps each genre name → list of movies belonging to that genre */
    private final Map<String, ObservableList<String>> moviesByGenre = new HashMap<>();

    /** Names of all registered customers */
    private final ObservableList<String> customerNames = FXCollections.observableArrayList();

    /** Maps each customer name → list of movies they currently have borrowed */
    private final Map<String, ObservableList<String>> borrowedByCustomer = new HashMap<>();

    /** Maps each customer name → list of movies they have already returned */
    private final Map<String, ObservableList<String>> returnedByCustomer = new HashMap<>();

    // ──────────────────────────────────────────────────────────────
    // APPLICATION ENTRY POINT
    // ──────────────────────────────────────────────────────────────

    @Override
    public void start(Stage stage) {

        // TabPane acts as the main navigation — one tab per module
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // tabs cannot be closed

        tabPane.getTabs().addAll(
                new Tab("1. Genres", createGenresPane()),
                new Tab("2. Movies", createMoviesPane()),
                new Tab("3. Customers", createCustomersPane()),
                new Tab("4. Rentals", createRentalsPane()));

        // Creating a scene object — the scene holds the entire UI tree
        Scene scene = new Scene(tabPane, 700, 520);

        // Setting title to the Stage (the OS window)
        stage.setTitle("Movie Library System");

        // Adding scene to the stage
        stage.setScene(scene);

        // Displaying the contents of the stage
        stage.show();
    }

    // ──────────────────────────────────────────────────────────────
    // TAB 1 — GENRES
    // Allows the user to add new genre names and remove existing ones.
    // ──────────────────────────────────────────────────────────────

    private GridPane createGenresPane() {

        // step 1: create label for the Name field
        Text text1 = new Text("Name:");

        // step 2: create label for the Registered dropdown
        Text text2 = new Text("Registered:");

        // step 3: create the text field where the user types a genre name
        TextField textField1 = new TextField();

        // step 4: create a ComboBox that shows all saved genres (bound to genreNames
        // list)
        ComboBox<String> comboBox = new ComboBox<>(genreNames);

        // step 5: create the Save and Remove action buttons
        Button button1 = new Button("Save");
        Button button2 = new Button("Remove");

        // step 6: create the GridPane layout container
        GridPane gridPane = new GridPane();

        // step 7: set minimum size of the pane
        gridPane.setMinSize(600, 400);

        // step 8: set padding around the pane edges
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        // step 9: set vertical and horizontal gaps between grid cells
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // step 10: centre the grid inside the tab
        gridPane.setAlignment(Pos.CENTER);

        // step 11: place each node at its column (0=left, 1=right) and row position
        gridPane.add(text1, 0, 0); // "Name:" label — column 0, row 0
        gridPane.add(textField1, 1, 0); // name text field — column 1, row 0
        gridPane.add(button1, 1, 1); // Save button — column 1, row 1
        gridPane.add(text2, 0, 2); // "Registered:" label— column 0, row 2
        gridPane.add(comboBox, 1, 2); // genre dropdown — column 1, row 2
        gridPane.add(button2, 1, 3); // Remove button — column 1, row 3

        // step 12: apply styles to make the UI look polished
        button1.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        button2.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        text1.setStyle("-fx-font: normal bold 20px 'serif' ");
        text2.setStyle("-fx-font: normal bold 20px 'serif' ");
        gridPane.setStyle("-fx-background-color: BEIGE;");

        // Save button — adds the typed genre to the shared list
        button1.setOnAction(e -> {
            String name = textField1.getText().trim();
            if (!name.isEmpty() && !genreNames.contains(name)) {
                genreNames.add(name); // update shared list
                moviesByGenre.put(name, FXCollections.observableArrayList()); // create empty movie list for this genre
                textField1.clear();
            }
        });

        // Remove button — deletes the genre selected in the ComboBox
        button2.setOnAction(e -> {
            String selected = comboBox.getValue();
            if (selected != null) {
                genreNames.remove(selected); // remove from shared list
                moviesByGenre.remove(selected); // remove its movie list too
                comboBox.setValue(null);
            }
        });

        return gridPane;
    }

    // ──────────────────────────────────────────────────────────────
    // TAB 2 — MOVIES
    // Allows the user to add movies to a selected genre and remove them.
    // ──────────────────────────────────────────────────────────────

    private GridPane createMoviesPane() {

        // Labels for each row
        Text textGenres = new Text("Genres:");
        Text textName = new Text("Name:");
        Text textRegistered = new Text("Registered:");

        // Genre dropdown — populated from the shared genreNames list
        ComboBox<String> genreCombo = new ComboBox<>(genreNames);

        // Text field for the movie title
        TextField nameField = new TextField();

        // Registered dropdown — shows movies that belong to the selected genre
        ComboBox<String> registeredCombo = new ComboBox<>();

        Button saveBtn = new Button("Save");
        Button removeBtn = new Button("Remove");

        // When the user picks a genre, load that genre's movies into registeredCombo
        genreCombo.setOnAction(e -> {
            String genre = genreCombo.getValue();
            if (genre != null) {
                registeredCombo.setItems(
                        moviesByGenre.getOrDefault(genre, FXCollections.observableArrayList()));
                registeredCombo.setValue(null);
            }
        });

        // Build the GridPane layout
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(600, 400);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(textGenres, 0, 0);
        gridPane.add(genreCombo, 1, 0);
        gridPane.add(textName, 0, 1);
        gridPane.add(nameField, 1, 1);
        gridPane.add(saveBtn, 1, 2);
        gridPane.add(textRegistered, 0, 3);
        gridPane.add(registeredCombo, 1, 3);
        gridPane.add(removeBtn, 1, 4);

        // Styling
        saveBtn.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        removeBtn.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        textGenres.setStyle("-fx-font: normal bold 20px 'serif' ");
        textName.setStyle("-fx-font: normal bold 20px 'serif' ");
        textRegistered.setStyle("-fx-font: normal bold 20px 'serif' ");
        gridPane.setStyle("-fx-background-color: BEIGE;");

        // Save button — adds the movie to the selected genre's list
        saveBtn.setOnAction(e -> {
            String genre = genreCombo.getValue();
            String name = nameField.getText().trim();
            if (genre != null && !name.isEmpty()) {
                moviesByGenre.computeIfAbsent(genre, k -> FXCollections.observableArrayList()).add(name);
                registeredCombo.setItems(moviesByGenre.get(genre)); // refresh dropdown
                nameField.clear();
            }
        });

        // Remove button — removes the selected movie from the genre
        removeBtn.setOnAction(e -> {
            String genre = genreCombo.getValue();
            String selected = registeredCombo.getValue();
            if (genre != null && selected != null) {
                moviesByGenre.get(genre).remove(selected);
                registeredCombo.setValue(null);
            }
        });

        return gridPane;
    }

    // ──────────────────────────────────────────────────────────────
    // TAB 3 — CUSTOMERS
    // Allows the user to register customers (name, phone, email) and remove them.
    // ──────────────────────────────────────────────────────────────

    private GridPane createCustomersPane() {

        // Labels for each input row
        Text textName = new Text("Name:");
        Text textPhone = new Text("Phone:");
        Text textEmail = new Text("Email:");
        Text textRegistered = new Text("Registered:");

        // Input fields
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();

        // Registered dropdown — shows all saved customers
        ComboBox<String> registeredCombo = new ComboBox<>(customerNames);

        Button saveBtn = new Button("Save");
        Button removeBtn = new Button("Remove");

        // Build the GridPane layout
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(600, 400);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(textName, 0, 0);
        gridPane.add(nameField, 1, 0);
        gridPane.add(textPhone, 0, 1);
        gridPane.add(phoneField, 1, 1);
        gridPane.add(textEmail, 0, 2);
        gridPane.add(emailField, 1, 2);
        gridPane.add(saveBtn, 1, 3);
        gridPane.add(textRegistered, 0, 4);
        gridPane.add(registeredCombo, 1, 4);
        gridPane.add(removeBtn, 1, 5);

        // Styling
        saveBtn.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        removeBtn.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        textName.setStyle("-fx-font: normal bold 20px 'serif' ");
        textPhone.setStyle("-fx-font: normal bold 20px 'serif' ");
        textEmail.setStyle("-fx-font: normal bold 20px 'serif' ");
        textRegistered.setStyle("-fx-font: normal bold 20px 'serif' ");
        gridPane.setStyle("-fx-background-color: BEIGE;");

        // Save button — registers a new customer and sets up their rental history
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty() && !customerNames.contains(name)) {
                customerNames.add(name); // add to shared customer list
                borrowedByCustomer.put(name, FXCollections.observableArrayList()); // start with empty borrowed list
                returnedByCustomer.put(name, FXCollections.observableArrayList()); // start with empty returned list
                nameField.clear();
                phoneField.clear();
                emailField.clear();
            }
        });

        // Remove button — deletes the selected customer and their rental history
        removeBtn.setOnAction(e -> {
            String selected = registeredCombo.getValue();
            if (selected != null) {
                customerNames.remove(selected);
                borrowedByCustomer.remove(selected);
                returnedByCustomer.remove(selected);
                registeredCombo.setValue(null);
            }
        });

        return gridPane;
    }

    // ──────────────────────────────────────────────────────────────
    // TAB 4 — RENTALS
    // Links a movie to a customer (rent), and allows returning borrowed movies.
    // Borrowed = movies the customer currently has.
    // Returned = movies the customer has brought back.
    // ──────────────────────────────────────────────────────────────

    private GridPane createRentalsPane() {

        // Labels for each row
        Text textCustomer = new Text("Customer:");
        Text textGenre = new Text("Genre:");
        Text textMovies = new Text("Movies:");
        Text textBorrowed = new Text("Borrowed:");
        Text textReturned = new Text("Returned:");

        // Dropdowns for selecting who is renting and what they want
        ComboBox<String> customerCombo = new ComboBox<>(customerNames); // from shared list
        ComboBox<String> genreCombo = new ComboBox<>(genreNames); // from shared list
        ComboBox<String> moviesCombo = new ComboBox<>(); // filled when genre is selected

        // Dropdowns showing the customer's rental history
        ComboBox<String> borrowedCombo = new ComboBox<>();
        ComboBox<String> returnedCombo = new ComboBox<>();

        Button saveBtn = new Button("Save");
        Button returnBtn = new Button("Return");

        // When a customer is selected, load their borrowed and returned movies
        customerCombo.setOnAction(e -> {
            String customer = customerCombo.getValue();
            if (customer != null) {
                borrowedCombo.setItems(borrowedByCustomer.getOrDefault(customer, FXCollections.observableArrayList()));
                returnedCombo.setItems(returnedByCustomer.getOrDefault(customer, FXCollections.observableArrayList()));
                borrowedCombo.setValue(null);
                returnedCombo.setValue(null);
            }
        });

        // When a genre is selected, load its movies into the Movies dropdown
        genreCombo.setOnAction(e -> {
            String genre = genreCombo.getValue();
            if (genre != null) {
                moviesCombo.setItems(moviesByGenre.getOrDefault(genre, FXCollections.observableArrayList()));
                moviesCombo.setValue(null);
            }
        });

        // Build the GridPane layout
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(600, 400);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(textCustomer, 0, 0);
        gridPane.add(customerCombo, 1, 0);
        gridPane.add(textGenre, 0, 1);
        gridPane.add(genreCombo, 1, 1);
        gridPane.add(textMovies, 0, 2);
        gridPane.add(moviesCombo, 1, 2);
        gridPane.add(saveBtn, 1, 3); // "Save rental" button
        gridPane.add(textBorrowed, 0, 4);
        gridPane.add(borrowedCombo, 1, 4);
        gridPane.add(returnBtn, 1, 5); // "Return movie" button
        gridPane.add(textReturned, 0, 6);
        gridPane.add(returnedCombo, 1, 6);

        // Styling
        saveBtn.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        returnBtn.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        textCustomer.setStyle("-fx-font: normal bold 20px 'serif' ");
        textGenre.setStyle("-fx-font: normal bold 20px 'serif' ");
        textMovies.setStyle("-fx-font: normal bold 20px 'serif' ");
        textBorrowed.setStyle("-fx-font: normal bold 20px 'serif' ");
        textReturned.setStyle("-fx-font: normal bold 20px 'serif' ");
        gridPane.setStyle("-fx-background-color: BEIGE;");

        // Save button — assigns the selected movie to the customer's borrowed list
        saveBtn.setOnAction(e -> {
            String customer = customerCombo.getValue();
            String movie = moviesCombo.getValue();
            if (customer != null && movie != null) {
                borrowedByCustomer.computeIfAbsent(customer, k -> FXCollections.observableArrayList()).add(movie);
                borrowedCombo.setItems(borrowedByCustomer.get(customer)); // refresh borrowed dropdown
                moviesCombo.setValue(null);
            }
        });

        // Return button — moves the selected movie from Borrowed → Returned
        returnBtn.setOnAction(e -> {
            String customer = customerCombo.getValue();
            String movie = borrowedCombo.getValue();
            if (customer != null && movie != null) {
                borrowedByCustomer.get(customer).remove(movie); // remove from borrowed
                returnedByCustomer.computeIfAbsent(customer, k -> FXCollections.observableArrayList()).add(movie); // add
                                                                                                                   // to
                                                                                                                   // returned
                returnedCombo.setItems(returnedByCustomer.get(customer)); // refresh returned dropdown
                borrowedCombo.setValue(null);
            }
        });

        return gridPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}