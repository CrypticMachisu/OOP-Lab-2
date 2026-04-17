import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App extends Application {

    // ── SHARED STATE ──────────────────────────────────────────────
    private final ObservableList<String> genreNames = FXCollections.observableArrayList();
    private final Map<String, ObservableList<String>> moviesByGenre = new HashMap<>();
    private final ObservableList<String> customerNames = FXCollections.observableArrayList();
    private final Map<String, ObservableList<String>> borrowedByCustomer = new HashMap<>();
    private final Map<String, ObservableList<String>> returnedByCustomer = new HashMap<>();

    // ── FILE PATH ─────────────────────────────────────────────────
    private static final String DATA_FILE = "library.dat";

    // ── STYLE CONSTANTS (unchanged) ───────────────────────────────
    private static final String BG = "-fx-background-color: #EEEAF6;";
    private static final String BTN_SAVE = "-fx-background-color: #3D3170; -fx-text-fill: white;"
            + " -fx-font-size: 12pt; -fx-padding: 6 22 6 22; -fx-background-radius: 6;";
    private static final String BTN_SAVE_HOVER = "-fx-background-color: #5548A0; -fx-text-fill: white;"
            + " -fx-font-size: 12pt; -fx-padding: 6 22 6 22; -fx-background-radius: 6;";
    private static final String BTN_REM = "-fx-background-color: #7B2020; -fx-text-fill: white;"
            + " -fx-font-size: 12pt; -fx-padding: 6 22 6 22; -fx-background-radius: 6;";
    private static final String BTN_REM_HOVER = "-fx-background-color: #A03030; -fx-text-fill: white;"
            + " -fx-font-size: 12pt; -fx-padding: 6 22 6 22; -fx-background-radius: 6;";
    private static final String LABEL = "-fx-font: normal bold 16px 'serif'; -fx-fill: #2A2040;";
    private static final String FIELD = "-fx-background-radius: 5; -fx-border-radius: 5;"
            + " -fx-border-color: #B0A8D4; -fx-border-width: 1; -fx-padding: 5 10;";
    private static final String COMBO = "-fx-background-radius: 5;";

    // ── PERSISTENCE METHODS ───────────────────────────────────────

    /** Load data from file into memory. Call this BEFORE building UI. */
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No existing data file found. Starting fresh.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            // Read genres
            List<String> loadedGenres = (List<String>) in.readObject();
            genreNames.addAll(loadedGenres);

            // Read movies by genre (stored as plain HashMap with ArrayLists)
            Map<String, List<String>> loadedMovies = (Map<String, List<String>>) in.readObject();
            for (Map.Entry<String, List<String>> entry : loadedMovies.entrySet()) {
                moviesByGenre.put(entry.getKey(), FXCollections.observableArrayList(entry.getValue()));
            }

            // Read customers
            List<String> loadedCustomers = (List<String>) in.readObject();
            customerNames.addAll(loadedCustomers);

            // Read borrowed history
            Map<String, List<String>> loadedBorrowed = (Map<String, List<String>>) in.readObject();
            for (Map.Entry<String, List<String>> entry : loadedBorrowed.entrySet()) {
                borrowedByCustomer.put(entry.getKey(), FXCollections.observableArrayList(entry.getValue()));
            }

            // Read returned history
            Map<String, List<String>> loadedReturned = (Map<String, List<String>>) in.readObject();
            for (Map.Entry<String, List<String>> entry : loadedReturned.entrySet()) {
                returnedByCustomer.put(entry.getKey(), FXCollections.observableArrayList(entry.getValue()));
            }

            System.out.println("Data loaded successfully from " + DATA_FILE);

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load data: " + e.getMessage());
            // Continue with empty data structures
        }
    }

    /** Save data from memory to file. Call this on exit or after modifications. */
    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            // Convert ObservableLists to plain ArrayLists for serialization
            out.writeObject(new ArrayList<>(genreNames));

            // Convert ObservableLists inside map to plain Lists
            Map<String, List<String>> moviesPlain = new HashMap<>();
            for (Map.Entry<String, ObservableList<String>> entry : moviesByGenre.entrySet()) {
                moviesPlain.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            out.writeObject(moviesPlain);

            out.writeObject(new ArrayList<>(customerNames));

            Map<String, List<String>> borrowedPlain = new HashMap<>();
            for (Map.Entry<String, ObservableList<String>> entry : borrowedByCustomer.entrySet()) {
                borrowedPlain.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            out.writeObject(borrowedPlain);

            Map<String, List<String>> returnedPlain = new HashMap<>();
            for (Map.Entry<String, ObservableList<String>> entry : returnedByCustomer.entrySet()) {
                returnedPlain.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            out.writeObject(returnedPlain);

            System.out.println("Data saved successfully to " + DATA_FILE);

        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }

    // ── SHARED HELPERS (unchanged) ────────────────────────────────
    private void styleButton(Button btn, boolean isSave) {
        String base = isSave ? BTN_SAVE : BTN_REM;
        String hover = isSave ? BTN_SAVE_HOVER : BTN_REM_HOVER;
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
    }

    private Text lbl(String text) {
        Text t = new Text(text);
        t.setStyle(LABEL);
        return t;
    }

    private TextField inputField() {
        TextField tf = new TextField();
        tf.setStyle(FIELD);
        tf.setPrefWidth(210);
        return tf;
    }

    private <T> ComboBox<T> comboBox(ObservableList<T> items) {
        ComboBox<T> cb = new ComboBox<>(items);
        cb.setStyle(COMBO);
        cb.setPrefWidth(210);
        return cb;
    }

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
        // 1. LOAD DATA FIRST — before building any UI
        loadData();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-tab-min-width: 110px; -fx-font-size: 11pt;");

        tabPane.getTabs().addAll(
                new Tab("1. Genres", createGenresPane()),
                new Tab("2. Movies", createMoviesPane()),
                new Tab("3. Customers", createCustomersPane()),
                new Tab("4. Rentals", createRentalsPane()));

        // 2. SAVE ON WINDOW CLOSE
        stage.setOnCloseRequest((WindowEvent e) -> {
            saveData();
        });

        stage.setTitle("Movie Library System");
        stage.setScene(new Scene(tabPane, 720, 540));
        stage.show();
    }

    // ── TAB 1 — GENRES ────────────────────────────────────────────
    private GridPane createGenresPane() {
        TextField nameField = inputField();
        nameField.setPromptText("e.g. Action, Horror, Sci-Fi…");
        ComboBox<String> registeredCombo = comboBox(genreNames);

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

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty() && !genreNames.contains(name)) {
                genreNames.add(name);
                moviesByGenre.put(name, FXCollections.observableArrayList());
                nameField.clear();
                saveData(); // 3. AUTO-SAVE AFTER CHANGE
            }
        });

        removeBtn.setOnAction(e -> {
            String selected = registeredCombo.getValue();
            if (selected != null) {
                genreNames.remove(selected);
                moviesByGenre.remove(selected);
                registeredCombo.setValue(null);
                saveData(); // 3. AUTO-SAVE AFTER CHANGE
            }
        });

        return gp;
    }

    // ── TAB 2 — MOVIES ────────────────────────────────────────────
    private GridPane createMoviesPane() {
        ComboBox<String> genreCombo = comboBox(genreNames);
        TextField nameField = inputField();
        nameField.setPromptText("e.g. Inception, The Matrix…");
        ComboBox<String> registeredCombo = comboBox(FXCollections.observableArrayList());

        Button saveBtn = new Button("Save");
        Button removeBtn = new Button("Remove");
        styleButton(saveBtn, true);
        styleButton(removeBtn, false);

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

        saveBtn.setOnAction(e -> {
            String genre = genreCombo.getValue();
            String name = nameField.getText().trim();
            if (genre != null && !name.isEmpty()) {
                moviesByGenre.computeIfAbsent(genre, k -> FXCollections.observableArrayList()).add(name);
                registeredCombo.setItems(moviesByGenre.get(genre));
                nameField.clear();
                saveData(); // AUTO-SAVE
            }
        });

        removeBtn.setOnAction(e -> {
            String genre = genreCombo.getValue();
            String selected = registeredCombo.getValue();
            if (genre != null && selected != null) {
                moviesByGenre.get(genre).remove(selected);
                registeredCombo.setValue(null);
                saveData(); // AUTO-SAVE
            }
        });

        return gp;
    }

    // ── TAB 3 — CUSTOMERS ─────────────────────────────────────────
    private GridPane createCustomersPane() {
        TextField nameField = inputField();
        nameField.setPromptText("e.g. Jane Doe");
        TextField phoneField = inputField();
        phoneField.setPromptText("e.g. +254 712 345 678");
        TextField emailField = inputField();
        emailField.setPromptText("e.g. jane@email.com");
        ComboBox<String> registeredCombo = comboBox(customerNames);

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

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty() && !customerNames.contains(name)) {
                customerNames.add(name);
                borrowedByCustomer.put(name, FXCollections.observableArrayList());
                returnedByCustomer.put(name, FXCollections.observableArrayList());
                nameField.clear();
                phoneField.clear();
                emailField.clear();
                saveData(); // AUTO-SAVE
            }
        });

        removeBtn.setOnAction(e -> {
            String selected = registeredCombo.getValue();
            if (selected != null) {
                customerNames.remove(selected);
                borrowedByCustomer.remove(selected);
                returnedByCustomer.remove(selected);
                registeredCombo.setValue(null);
                saveData(); // AUTO-SAVE
            }
        });

        return gp;
    }

    // ── TAB 4 — RENTALS ───────────────────────────────────────────
    private GridPane createRentalsPane() {
        ComboBox<String> customerCombo = comboBox(customerNames);
        ComboBox<String> genreCombo = comboBox(genreNames);
        ComboBox<String> moviesCombo = comboBox(FXCollections.observableArrayList());
        ComboBox<String> borrowedCombo = comboBox(FXCollections.observableArrayList());
        ComboBox<String> returnedCombo = comboBox(FXCollections.observableArrayList());

        Button saveBtn = new Button("Save");
        Button returnBtn = new Button("Return");
        styleButton(saveBtn, true);
        styleButton(returnBtn, false);

        customerCombo.setOnAction(e -> {
            String customer = customerCombo.getValue();
            if (customer != null) {
                borrowedCombo.setItems(borrowedByCustomer.getOrDefault(customer, FXCollections.observableArrayList()));
                returnedCombo.setItems(returnedByCustomer.getOrDefault(customer, FXCollections.observableArrayList()));
                borrowedCombo.setValue(null);
                returnedCombo.setValue(null);
            }
        });

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

        saveBtn.setOnAction(e -> {
            String customer = customerCombo.getValue();
            String movie = moviesCombo.getValue();
            if (customer != null && movie != null) {
                borrowedByCustomer.computeIfAbsent(customer, k -> FXCollections.observableArrayList()).add(movie);
                borrowedCombo.setItems(borrowedByCustomer.get(customer));
                moviesCombo.setValue(null);
                saveData(); // AUTO-SAVE
            }
        });

        returnBtn.setOnAction(e -> {
            String customer = customerCombo.getValue();
            String movie = borrowedCombo.getValue();
            if (customer != null && movie != null) {
                borrowedByCustomer.get(customer).remove(movie);
                returnedByCustomer.computeIfAbsent(customer, k -> FXCollections.observableArrayList()).add(movie);
                returnedCombo.setItems(returnedByCustomer.get(customer));
                borrowedCombo.setValue(null);
                saveData(); // AUTO-SAVE
            }
        });

        return gp;
    }

    public static void main(String[] args) {
        launch(args);
    }
}