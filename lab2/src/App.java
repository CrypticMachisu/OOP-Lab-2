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
import java.util.Optional;

public class App extends Application {

    // ── Customer model — stores all three fields (was lost before) ──
    private static class Customer {
        final String name, phone, email;

        Customer(String name, String phone, String email) {
            this.name = name;
            this.phone = phone.isEmpty() ? "N/A" : phone;
            this.email = email.isEmpty() ? "N/A" : email;
        }
    }

    // ── Shared data ─────────────────────────────────────────────────
    private final ObservableList<String> genreNames = FXCollections.observableArrayList();
    private final Map<String, ObservableList<String>> moviesByGenre = new HashMap<>();
    private final ObservableList<String> customerNames = FXCollections.observableArrayList();
    private final Map<String, Customer> customerDetails = new HashMap<>();
    private final Map<String, ObservableList<String>> borrowedByCustomer = new HashMap<>();
    private final Map<String, ObservableList<String>> returnedByCustomer = new HashMap<>();

    // ── Shared style constants ───────────────────────────────────────
    private static final String BTN_PRIMARY = "-fx-background-color: #3A506B; -fx-text-fill: white; " +
            "-fx-font-size:12pt; -fx-background-radius:6; -fx-cursor:hand; -fx-padding:6 18 6 18;";
    private static final String BTN_DANGER = "-fx-background-color: #B23A48; -fx-text-fill: white; " +
            "-fx-font-size:12pt; -fx-background-radius:6; -fx-cursor:hand; -fx-padding:6 18 6 18;";
    private static final String BTN_SUCCESS = "-fx-background-color: #2D6A4F; -fx-text-fill: white; " +
            "-fx-font-size:12pt; -fx-background-radius:6; -fx-cursor:hand; -fx-padding:6 18 6 18;";
    private static final String LABEL_STYLE = "-fx-font: normal bold 15px 'serif';";
    private static final String PANE_BG = "-fx-background-color: #F5F0E8;";
    private static final String STATUS_OK = "-fx-text-fill: #2D6A4F; -fx-font-size:11pt;";
    private static final String STATUS_ERR = "-fx-text-fill: #B23A48; -fx-font-size:11pt;";

    // ────────────────────────────────────────────────────────────────
    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-tab-min-width:130px; -fx-font-size:11.5pt;");

        tabPane.getTabs().addAll(
                new Tab("🎭  Genres", createGenresPane()),
                new Tab("🎬  Movies", createMoviesPane()),
                new Tab("👤  Customers", createCustomersPane()),
                new Tab("📼  Rentals", createRentalsPane()));

        stage.setTitle("Movie Library System");
        stage.setScene(new Scene(tabPane, 720, 560));
        stage.setResizable(false);
        stage.show();
    }

    // ── Utility helpers ──────────────────────────────────────────────

    /** Inline feedback label — no dialog pop-up needed for trivial messages */
    private Label statusLabel() {
        Label lbl = new Label();
        lbl.setWrapText(true);
        lbl.setMaxWidth(240);
        return lbl;
    }

    private void ok(Label lbl, String msg) {
        lbl.setText("✔  " + msg);
        lbl.setStyle(STATUS_OK);
    }

    private void err(Label lbl, String msg) {
        lbl.setText("⚠  " + msg);
        lbl.setStyle(STATUS_ERR);
    }

    /** Confirmation dialog for destructive actions */
    private boolean confirmRemove(String item) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirm Removal");
        a.setHeaderText(null);
        a.setContentText("Remove \"" + item + "\"? This cannot be undone.");
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }

    private void applyLabelStyle(Text... labels) {
        for (Text t : labels)
            t.setStyle(LABEL_STYLE);
    }

    // ══════════════════════════════════════════════════════════
    // TAB 1 — GENRES
    // ══════════════════════════════════════════════════════════
    private GridPane createGenresPane() {
        Label status = statusLabel();

        Text lblName = new Text("Genre Name:");
        Text lblList = new Text("Registered:");
        applyLabelStyle(lblName, lblList);

        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Action, Drama, Sci-Fi …");
        nameField.setPrefWidth(220);

        // FIX: ListView gives a much clearer view of all genres vs a single ComboBox
        ListView<String> genreList = new ListView<>(genreNames);
        genreList.setPrefHeight(130);
        genreList.setPrefWidth(220);

        Button saveBtn = new Button("✔  Save");
        Button removeBtn = new Button("✖  Remove");
        saveBtn.setStyle(BTN_PRIMARY);
        removeBtn.setStyle(BTN_DANGER);

        GridPane gp = baseGrid();
        gp.add(lblName, 0, 0);
        gp.add(nameField, 1, 0);
        gp.add(saveBtn, 1, 1);
        gp.add(status, 1, 2);
        gp.add(lblList, 0, 3);
        gp.add(genreList, 1, 3);
        gp.add(removeBtn, 1, 4);

        // ── Save
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                err(status, "Genre name cannot be empty.");
                return;
            }
            if (genreNames.contains(name)) {
                err(status, "Genre already exists.");
                return;
            }
            genreNames.add(name);
            moviesByGenre.put(name, FXCollections.observableArrayList());
            nameField.clear();
            ok(status, "\"" + name + "\" saved.");
        });

        // ── Remove (FIX: block removal when genre still has movies)
        removeBtn.setOnAction(e -> {
            String sel = genreList.getSelectionModel().getSelectedItem();
            if (sel == null) {
                err(status, "Select a genre from the list.");
                return;
            }
            if (!moviesByGenre.getOrDefault(sel, FXCollections.observableArrayList()).isEmpty()) {
                err(status, "Remove all movies in this genre first.");
                return;
            }
            if (confirmRemove(sel)) {
                genreNames.remove(sel);
                moviesByGenre.remove(sel);
                ok(status, "\"" + sel + "\" removed.");
            }
        });

        return gp;
    }

    // ══════════════════════════════════════════════════════════
    // TAB 2 — MOVIES
    // ══════════════════════════════════════════════════════════
    private GridPane createMoviesPane() {
        Label status = statusLabel();

        Text lblGenre = new Text("Genre:");
        Text lblName = new Text("Movie Title:");
        Text lblList = new Text("In Genre:");
        applyLabelStyle(lblGenre, lblName, lblList);

        ComboBox<String> genreCombo = new ComboBox<>(genreNames);
        genreCombo.setPromptText("Select genre");
        genreCombo.setPrefWidth(220);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter movie title …");
        nameField.setPrefWidth(220);

        // FIX: ListView replaces ComboBox for Registered — shows all titles at once
        ListView<String> movieList = new ListView<>();
        movieList.setPrefHeight(130);
        movieList.setPrefWidth(220);

        Button saveBtn = new Button("✔  Save");
        Button removeBtn = new Button("✖  Remove");
        saveBtn.setStyle(BTN_PRIMARY);
        removeBtn.setStyle(BTN_DANGER);

        genreCombo.setOnAction(e -> {
            String genre = genreCombo.getValue();
            if (genre != null) {
                movieList.setItems(moviesByGenre.getOrDefault(genre, FXCollections.observableArrayList()));
                movieList.getSelectionModel().clearSelection();
            }
        });

        GridPane gp = baseGrid();
        gp.add(lblGenre, 0, 0);
        gp.add(genreCombo, 1, 0);
        gp.add(lblName, 0, 1);
        gp.add(nameField, 1, 1);
        gp.add(saveBtn, 1, 2);
        gp.add(status, 1, 3);
        gp.add(lblList, 0, 4);
        gp.add(movieList, 1, 4);
        gp.add(removeBtn, 1, 5);

        // ── Save (FIX: duplicate movie check added)
        saveBtn.setOnAction(e -> {
            String genre = genreCombo.getValue();
            String name = nameField.getText().trim();
            if (genre == null) {
                err(status, "Please select a genre first.");
                return;
            }
            if (name.isEmpty()) {
                err(status, "Movie title cannot be empty.");
                return;
            }
            ObservableList<String> list = moviesByGenre.computeIfAbsent(genre,
                    k -> FXCollections.observableArrayList());
            if (list.contains(name)) {
                err(status, "Movie already exists in this genre.");
                return;
            }
            list.add(name);
            movieList.setItems(list);
            nameField.clear();
            ok(status, "\"" + name + "\" added to " + genre + ".");
        });

        // ── Remove
        removeBtn.setOnAction(e -> {
            String genre = genreCombo.getValue();
            String sel = movieList.getSelectionModel().getSelectedItem();
            if (genre == null || sel == null) {
                err(status, "Select a genre and a movie.");
                return;
            }
            if (confirmRemove(sel)) {
                moviesByGenre.get(genre).remove(sel);
                ok(status, "\"" + sel + "\" removed.");
            }
        });

        return gp;
    }

    // ══════════════════════════════════════════════════════════
    // TAB 3 — CUSTOMERS
    // ══════════════════════════════════════════════════════════
    private GridPane createCustomersPane() {
        Label status = statusLabel();

        Text lblName = new Text("Name:");
        Text lblPhone = new Text("Phone:");
        Text lblEmail = new Text("Email:");
        Text lblList = new Text("Registered:");
        Text lblDetails = new Text("Details:");
        applyLabelStyle(lblName, lblPhone, lblEmail, lblList, lblDetails);

        TextField nameField = new TextField();
        nameField.setPromptText("Full name");
        TextField phoneField = new TextField();
        phoneField.setPromptText("+254 …");
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");

        ListView<String> customerList = new ListView<>(customerNames);
        customerList.setPrefHeight(100);
        customerList.setPrefWidth(220);

        // FIX: Details pane — phone/email are now stored and displayed (previously
        // discarded)
        Label detailsLbl = new Label("Select a customer to view their details.");
        detailsLbl.setStyle("-fx-font-size:11pt; -fx-text-fill:#555;");
        detailsLbl.setWrapText(true);
        detailsLbl.setMaxWidth(230);

        customerList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null && customerDetails.containsKey(sel)) {
                Customer c = customerDetails.get(sel);
                detailsLbl.setText("📞  " + c.phone + "\n✉   " + c.email);
                detailsLbl.setStyle("-fx-font-size:11pt; -fx-text-fill:#333;");
            } else {
                detailsLbl.setText("Select a customer to view their details.");
                detailsLbl.setStyle("-fx-font-size:11pt; -fx-text-fill:#555;");
            }
        });

        Button saveBtn = new Button("✔  Save");
        Button removeBtn = new Button("✖  Remove");
        saveBtn.setStyle(BTN_PRIMARY);
        removeBtn.setStyle(BTN_DANGER);

        GridPane gp = baseGrid();
        gp.setPadding(new Insets(20, 40, 20, 40));
        gp.add(lblName, 0, 0);
        gp.add(nameField, 1, 0);
        gp.add(lblPhone, 0, 1);
        gp.add(phoneField, 1, 1);
        gp.add(lblEmail, 0, 2);
        gp.add(emailField, 1, 2);
        gp.add(saveBtn, 1, 3);
        gp.add(status, 1, 4);
        gp.add(lblList, 0, 5);
        gp.add(customerList, 1, 5);
        gp.add(lblDetails, 0, 6);
        gp.add(detailsLbl, 1, 6);
        gp.add(removeBtn, 1, 7);

        // ── Save
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isEmpty()) {
                err(status, "Customer name cannot be empty.");
                return;
            }
            if (customerNames.contains(name)) {
                err(status, "Customer already registered.");
                return;
            }
            customerNames.add(name);
            customerDetails.put(name, new Customer(name, phone, email));
            borrowedByCustomer.put(name, FXCollections.observableArrayList());
            returnedByCustomer.put(name, FXCollections.observableArrayList());
            nameField.clear();
            phoneField.clear();
            emailField.clear();
            ok(status, "\"" + name + "\" registered.");
        });

        // ── Remove (FIX: block removal when customer has unreturned movies)
        removeBtn.setOnAction(e -> {
            String sel = customerList.getSelectionModel().getSelectedItem();
            if (sel == null) {
                err(status, "Select a customer from the list.");
                return;
            }
            if (!borrowedByCustomer.getOrDefault(sel, FXCollections.observableArrayList()).isEmpty()) {
                err(status, "Customer has unreturned movies.");
                return;
            }
            if (confirmRemove(sel)) {
                customerNames.remove(sel);
                customerDetails.remove(sel);
                borrowedByCustomer.remove(sel);
                returnedByCustomer.remove(sel);
                detailsLbl.setText("Select a customer to view their details.");
                ok(status, "\"" + sel + "\" removed.");
            }
        });

        return gp;
    }

    // ══════════════════════════════════════════════════════════
    // TAB 4 — RENTALS
    // ══════════════════════════════════════════════════════════
    private GridPane createRentalsPane() {
        Label status = statusLabel();

        Text lblCustomer = new Text("Customer:");
        Text lblGenre = new Text("Genre:");
        Text lblMovie = new Text("Movie:");
        Text lblBorrowed = new Text("Borrowed:");
        Text lblReturned = new Text("Returned:");
        applyLabelStyle(lblCustomer, lblGenre, lblMovie, lblBorrowed, lblReturned);

        ComboBox<String> customerCombo = new ComboBox<>(customerNames);
        ComboBox<String> genreCombo = new ComboBox<>(genreNames);
        ComboBox<String> moviesCombo = new ComboBox<>();

        customerCombo.setPromptText("Select customer");
        customerCombo.setPrefWidth(220);
        genreCombo.setPromptText("Select genre");
        genreCombo.setPrefWidth(220);
        moviesCombo.setPromptText("Select movie");
        moviesCombo.setPrefWidth(220);

        // FIX: ListViews replace ComboBoxes for borrowed/returned — visible at a glance
        ListView<String> borrowedList = new ListView<>();
        borrowedList.setPrefHeight(100);
        borrowedList.setPrefWidth(220);

        ListView<String> returnedList = new ListView<>();
        returnedList.setPrefHeight(100);
        returnedList.setPrefWidth(220);

        Button rentBtn = new Button("📼  Rent");
        Button returnBtn = new Button("↩  Return");
        rentBtn.setStyle(BTN_SUCCESS);
        returnBtn.setStyle(BTN_PRIMARY);

        customerCombo.setOnAction(e -> {
            String c = customerCombo.getValue();
            if (c != null) {
                borrowedList.setItems(borrowedByCustomer.getOrDefault(c, FXCollections.observableArrayList()));
                returnedList.setItems(returnedByCustomer.getOrDefault(c, FXCollections.observableArrayList()));
                borrowedList.getSelectionModel().clearSelection();
                returnedList.getSelectionModel().clearSelection();
                status.setText("");
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
        gp.setPadding(new Insets(16, 40, 16, 40));
        gp.add(lblCustomer, 0, 0);
        gp.add(customerCombo, 1, 0);
        gp.add(lblGenre, 0, 1);
        gp.add(genreCombo, 1, 1);
        gp.add(lblMovie, 0, 2);
        gp.add(moviesCombo, 1, 2);
        gp.add(rentBtn, 1, 3);
        gp.add(status, 1, 4);
        gp.add(lblBorrowed, 0, 5);
        gp.add(borrowedList, 1, 5);
        gp.add(returnBtn, 1, 6);
        gp.add(lblReturned, 0, 7);
        gp.add(returnedList, 1, 7);

        // ── Rent (FIX: guard against duplicate borrowing)
        rentBtn.setOnAction(e -> {
            String customer = customerCombo.getValue();
            String movie = moviesCombo.getValue();
            if (customer == null) {
                err(status, "Please select a customer.");
                return;
            }
            if (movie == null) {
                err(status, "Please select a movie.");
                return;
            }
            ObservableList<String> borrowed = borrowedByCustomer.computeIfAbsent(customer,
                    k -> FXCollections.observableArrayList());
            if (borrowed.contains(movie)) {
                err(status, "Customer already has this movie.");
                return;
            }
            borrowed.add(movie);
            borrowedList.setItems(borrowed);
            moviesCombo.setValue(null);
            ok(status, "\"" + movie + "\" rented to " + customer + ".");
        });

        // ── Return (FIX: select from ListView, not a ComboBox)
        returnBtn.setOnAction(e -> {
            String customer = customerCombo.getValue();
            String movie = borrowedList.getSelectionModel().getSelectedItem();
            if (customer == null) {
                err(status, "Please select a customer.");
                return;
            }
            if (movie == null) {
                err(status, "Select a movie from the Borrowed list.");
                return;
            }
            borrowedByCustomer.get(customer).remove(movie);
            ObservableList<String> returned = returnedByCustomer.computeIfAbsent(customer,
                    k -> FXCollections.observableArrayList());
            returned.add(movie);
            returnedList.setItems(returned);
            ok(status, "\"" + movie + "\" returned successfully.");
        });

        return gp;
    }

    // ── Shared grid factory ──────────────────────────────────────────
    private GridPane baseGrid() {
        GridPane gp = new GridPane();
        gp.setMinSize(600, 400);
        gp.setPadding(new Insets(30, 40, 30, 40));
        gp.setVgap(13);
        gp.setHgap(16);
        gp.setAlignment(Pos.CENTER);
        gp.setStyle(PANE_BG);
        return gp;
    }

    public static void main(String[] args) {
        launch(args);
    }
}