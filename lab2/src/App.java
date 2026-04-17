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
 * Organised into four tabs:
 * 1. Genres — add / remove genre categories
 * 2. Movies — add / remove movies linked to a genre
 * 3. Customers — register / remove customers
 * 4. Rentals — rent movies and process returns
 *
 * All data lives in ObservableLists / Maps declared as fields (shared state).
 * ComboBoxes bound to ObservableLists refresh automatically on add / remove.
 */
public class App extends Application {

    // ── SHARED STATE ──────────────────────────────────────────────
    // Single source of truth used by every tab.

    private final ObservableList<String> genreNames = FXCollections.observableArrayList();
    private final Map<String, ObservableList<String>> moviesByGenre = new HashMap<>();
    private final ObservableList<String> customerNames = FXCollections.observableArrayList();
    private final Map<String, ObservableList<String>> borrowedByCustomer = new HashMap<>();
    private final Map<String, ObservableList<String>> returnedByCustomer = new HashMap<>();

    // ── STYLE CONSTANTS ───────────────────────────────────────────

    // Background — soft lavender instead of plain beige
    private static final String BG = "-fx-background-color: #EEEAF6;";

    // Save / action buttons — deep purple
    private static final String BTN_SAVE = "-fx-background-color: #3D3170; -fx-text-fill: white;"
            + " -fx-font-size: 12pt; -fx-padding: 6 22 6 22; -fx-background-radius: 6;";
    private static final String BTN_SAVE_HOVER = "-fx-background-color: #5548A0; -fx-text-fill: white;"
            + " -fx-font-size: 12pt; -fx-padding: 6 22 6 22; -fx-background-radius: 6;";

    // Remove / destructive buttons — deep red (signals irreversibility)
    private static final String BTN_REM = "-fx-background-color: #7B2020; -fx-text-fill: white;"
            + " -fx-font-size: 12pt; -fx-padding: 6 22 6 22; -fx-background-radius: 6;";
    private static final String BTN_REM_HOVER = "-fx-background-color: #A03030; -fx-text-fill: white;"
            + " -fx-font-size: 12pt; -fx-padding: 6 22 6 22; -fx-background-radius: 6;";

    // Labels — bold serif, dark purple-black
    private static final String LABEL = "-fx-font: normal bold 16px 'serif'; -fx-fill: #2A2040;";

    // Input fields — white with a subtle lavender border
    private static final String FIELD = "-fx-background-radius: 5; -fx-border-radius: 5;"
            + " -fx-border-color: #B0A8D4; -fx-border-width: 1; -fx-padding: 5 10;";

    // ComboBoxes — rounded to match fields
    private static final String COMBO = "-fx-background-radius: 5;";

    // ── SHARED HELPERS ────────────────────────────────────────────
    // Factory methods that apply consistent styles so each tab
    // doesn't repeat the same inline-style strings.

    /**
     * Style a button and attach hover feedback. isSave=true → purple; false → red.
     */
    private void styleButton(Button btn, boolean isSave) {
        String base = isSave ? BTN_SAVE : BTN_REM;
        String hover = isSave ? BTN_SAVE_HOVER : BTN_REM_HOVER;
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
    }

    /** Create a styled bold label. */
    private Text lbl(String text) {
        Text t = new Text(text);
        t.setStyle(LABEL);
        return t;
    }

    /** Create a styled text field with a fixed preferred width. */
    private TextField inputField() {
        TextField tf = new TextField();
        tf.setStyle(FIELD);
        tf.setPrefWidth(210);
        return tf;
    }

    /** Create a styled ComboBox bound to an ObservableList. */
    private <T> ComboBox<T> comboBox(ObservableList<T> items) {
        ComboBox<T> cb = new ComboBox<>(items);
        cb.setStyle(COMBO);
        cb.setPrefWidth(210);
        return cb;
    }

    /** Build a pre-configured GridPane used as the base for every tab. */
    private GridPane baseGrid() {
        GridPane gp = new GridPane();
        gp.setMinSize(600, 400);
        gp.setPadding(new Insets(30, 50, 30, 50));
        gp.setVgap(14);
        gp.setHgap(18);
        gp.setAlignment(Pos.CENTER);
        gp.setStyle(BG);
        return gp;
    }

    // ── ENTRY POINT ───────────────────────────────────────────────

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-tab-min-width: 110px; -fx-font-size: 11pt;");

        tabPane.getTabs().addAll(
                new Tab("1. Genres", createGenresPane()),
                new Tab("2. Movies", createMoviesPane()),
                new Tab("3. Customers", createCustomersPane()),
                new Tab("4. Rentals", createRentalsPane()));

        stage.setTitle("Movie Library System");
        stage.setScene(new Scene(tabPane, 720, 540));
        stage.show();
    }

    // ── TAB 1 — GENRES ────────────────────────────────────────────
    // Add a genre (Save) or delete the one selected in the dropdown (Remove).

    private GridPane createGenresPane() {

        TextField nameField = inputField();
        nameField.setPromptText("e.g. Action, Horror, Sci-Fi…");
        ComboBox<String> registeredCombo = comboBox(genreNames); // bound to shared list — auto-refreshes

        Button saveBtn = new Button("Save");
        Button removeBtn = new Button("Remove");
        styleButton(saveBtn, true);
        styleButton(removeBtn, false);

        GridPane gp = baseGrid();
        gp.add(lbl("Name:"), 0, 0);
        gp.add(nameField, 1, 0);
        gp.add(saveBtn, 1, 1);
        gp.add(lbl("Registered:"), 0, 2);
        gp.add(registeredCombo, 1, 2);
        gp.add(removeBtn, 1, 3);

        // Save — add the new genre and initialise its empty movie list
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty() && !genreNames.contains(name)) {
                genreNames.add(name);
                moviesByGenre.put(name, FXCollections.observableArrayList());
                nameField.clear();
            }
        });

        // Remove — delete the selected genre and all its associated movies
        removeBtn.setOnAction(e -> {
            String selected = registeredCombo.getValue();
            if (selected != null) {
                genreNames.remove(selected);
                moviesByGenre.remove(selected);
                registeredCombo.setValue(null);
            }
        });

        return gp;
    }

    // ── TAB 2 — MOVIES ────────────────────────────────────────────
    // Add movies to a genre (Save) or delete a selected one (Remove).

    private GridPane createMoviesPane() {

        ComboBox<String> genreCombo = comboBox(genreNames); // shared genre list
        TextField nameField = inputField();
        nameField.setPromptText("e.g. Inception, The Matrix…");
        ComboBox<String> registeredCombo = comboBox(FXCollections.observableArrayList());

        Button saveBtn = new Button("Save");
        Button removeBtn = new Button("Remove");
        styleButton(saveBtn, true);
        styleButton(removeBtn, false);

        // Choosing a genre loads its movies into the Registered dropdown
        genreCombo.setOnAction(e -> {
            String genre = genreCombo.getValue();
            if (genre != null) {
                registeredCombo.setItems(moviesByGenre.getOrDefault(genre, FXCollections.observableArrayList()));
                registeredCombo.setValue(null);
            }
        });

        GridPane gp = baseGrid();
        gp.add(lbl("Genre:"), 0, 0);
        gp.add(genreCombo, 1, 0);
        gp.add(lbl("Name:"), 0, 1);
        gp.add(nameField, 1, 1);
        gp.add(saveBtn, 1, 2);
        gp.add(lbl("Registered:"), 0, 3);
        gp.add(registeredCombo, 1, 3);
        gp.add(removeBtn, 1, 4);

        // Save — add the typed title to the selected genre's movie list
        saveBtn.setOnAction(e -> {
            String genre = genreCombo.getValue();
            String name = nameField.getText().trim();
            if (genre != null && !name.isEmpty()) {
                moviesByGenre.computeIfAbsent(genre, k -> FXCollections.observableArrayList()).add(name);
                registeredCombo.setItems(moviesByGenre.get(genre));
                nameField.clear();
            }
        });

        // Remove — delete the selected movie from the genre
        removeBtn.setOnAction(e -> {
            String genre = genreCombo.getValue();
            String selected = registeredCombo.getValue();
            if (genre != null && selected != null) {
                moviesByGenre.get(genre).remove(selected);
                registeredCombo.setValue(null);
            }
        });

        return gp;
    }

    // ── TAB 3 — CUSTOMERS ─────────────────────────────────────────
    // Register a customer with name, phone, and email (Save); remove one (Remove).

    private GridPane createCustomersPane() {

        TextField nameField = inputField();
        nameField.setPromptText("e.g. Jane Doe");
        TextField phoneField = inputField();
        phoneField.setPromptText("e.g. +254 712 345 678");
        TextField emailField = inputField();
        emailField.setPromptText("e.g. jane@email.com");
        ComboBox<String> registeredCombo = comboBox(customerNames); // bound to shared list

        Button saveBtn = new Button("Save");
        Button removeBtn = new Button("Remove");
        styleButton(saveBtn, true);
        styleButton(removeBtn, false);

        GridPane gp = baseGrid();
        gp.add(lbl("Name:"), 0, 0);
        gp.add(nameField, 1, 0);
        gp.add(lbl("Phone:"), 0, 1);
        gp.add(phoneField, 1, 1);
        gp.add(lbl("Email:"), 0, 2);
        gp.add(emailField, 1, 2);
        gp.add(saveBtn, 1, 3);
        gp.add(lbl("Registered:"), 0, 4);
        gp.add(registeredCombo, 1, 4);
        gp.add(removeBtn, 1, 5);

        // Save — register the customer and create empty borrowed / returned lists
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty() && !customerNames.contains(name)) {
                customerNames.add(name);
                borrowedByCustomer.put(name, FXCollections.observableArrayList());
                returnedByCustomer.put(name, FXCollections.observableArrayList());
                nameField.clear();
                phoneField.clear();
                emailField.clear();
            }
        });

        // Remove — delete the customer and wipe their rental history
        removeBtn.setOnAction(e -> {
            String selected = registeredCombo.getValue();
            if (selected != null) {
                customerNames.remove(selected);
                borrowedByCustomer.remove(selected);
                returnedByCustomer.remove(selected);
                registeredCombo.setValue(null);
            }
        });

        return gp;
    }

    // ── TAB 4 — RENTALS ───────────────────────────────────────────
    // Save — rent a movie to a customer (adds to Borrowed).
    // Return — move a movie from Borrowed → Returned.

    private GridPane createRentalsPane() {

        ComboBox<String> customerCombo = comboBox(customerNames); // shared list
        ComboBox<String> genreCombo = comboBox(genreNames); // shared list
        ComboBox<String> moviesCombo = comboBox(FXCollections.observableArrayList());
        ComboBox<String> borrowedCombo = comboBox(FXCollections.observableArrayList());
        ComboBox<String> returnedCombo = comboBox(FXCollections.observableArrayList());

        Button saveBtn = new Button("Save");
        Button returnBtn = new Button("Return");
        styleButton(saveBtn, true);
        styleButton(returnBtn, false);

        // Selecting a customer loads their current Borrowed and Returned history
        customerCombo.setOnAction(e -> {
            String customer = customerCombo.getValue();
            if (customer != null) {
                borrowedCombo.setItems(borrowedByCustomer.getOrDefault(customer, FXCollections.observableArrayList()));
                returnedCombo.setItems(returnedByCustomer.getOrDefault(customer, FXCollections.observableArrayList()));
                borrowedCombo.setValue(null);
                returnedCombo.setValue(null);
            }
        });

        // Selecting a genre loads its available movies into the Movies dropdown
        genreCombo.setOnAction(e -> {
            String genre = genreCombo.getValue();
            if (genre != null) {
                moviesCombo.setItems(moviesByGenre.getOrDefault(genre, FXCollections.observableArrayList()));
                moviesCombo.setValue(null);
            }
        });

        GridPane gp = baseGrid();
        gp.add(lbl("Customer:"), 0, 0);
        gp.add(customerCombo, 1, 0);
        gp.add(lbl("Genre:"), 0, 1);
        gp.add(genreCombo, 1, 1);
        gp.add(lbl("Movies:"), 0, 2);
        gp.add(moviesCombo, 1, 2);
        gp.add(saveBtn, 1, 3);
        gp.add(lbl("Borrowed:"), 0, 4);
        gp.add(borrowedCombo, 1, 4);
        gp.add(returnBtn, 1, 5);
        gp.add(lbl("Returned:"), 0, 6);
        gp.add(returnedCombo, 1, 6);

        // Save — add the selected movie to the customer's Borrowed list
        saveBtn.setOnAction(e -> {
            String customer = customerCombo.getValue();
            String movie = moviesCombo.getValue();
            if (customer != null && movie != null) {
                borrowedByCustomer.computeIfAbsent(customer, k -> FXCollections.observableArrayList()).add(movie);
                borrowedCombo.setItems(borrowedByCustomer.get(customer));
                moviesCombo.setValue(null);
            }
        });

        // Return — move the selected movie from Borrowed → Returned
        returnBtn.setOnAction(e -> {
            String customer = customerCombo.getValue();
            String movie = borrowedCombo.getValue();
            if (customer != null && movie != null) {
                borrowedByCustomer.get(customer).remove(movie);
                returnedByCustomer.computeIfAbsent(customer, k -> FXCollections.observableArrayList()).add(movie);
                returnedCombo.setItems(returnedByCustomer.get(customer));
                borrowedCombo.setValue(null);
            }
        });

        return gp;
    }

    public static void main(String[] args) {
        launch(args);
    }
}