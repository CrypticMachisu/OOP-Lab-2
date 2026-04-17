import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

public class App1 extends Application {

    // ── MODERN COLOR PALETTE ─────────────────────────────────────
    private static final String COLOR_BG = "#0F172A";
    private static final String COLOR_SURFACE = "#1E293B";
    private static final String COLOR_BORDER = "#64748B"; // Even lighter border
    private static final String COLOR_TEXT = "#FFFFFF"; // Pure white
    private static final String COLOR_TEXT_MUTED = "#94A3B8"; // Light gray
    private static final String COLOR_PRIMARY = "#3B82F6";
    private static final String COLOR_PRIMARY_HOVER = "#2563EB";
    private static final String COLOR_DANGER = "#EF4444";
    private static final String COLOR_DANGER_HOVER = "#DC2626";
    private static final String COLOR_SUCCESS = "#10B981";
    private static final String COLOR_ACCENT = "#8B5CF6";

    // ── STATE ────────────────────────────────────────────────────
    private final ObservableList<String> genreNames = FXCollections.observableArrayList();
    private final Map<String, ObservableList<String>> moviesByGenre = new HashMap<>();
    private final ObservableList<String> customerNames = FXCollections.observableArrayList();
    private final Map<String, ObservableList<String>> borrowedByCustomer = new HashMap<>();
    private final Map<String, ObservableList<String>> returnedByCustomer = new HashMap<>();

    // ── UI COMPONENTS ─────────────────────────────────────────────
    private StackPane mainContent;

    // ── DATA FILE ─────────────────────────────────────────────────
    private static final String DATA_FILE = "library.dat";

    // ── SORTING HELPERS ───────────────────────────────────────────
    private void sortList(ObservableList<String> list) {
        FXCollections.sort(list, String.CASE_INSENSITIVE_ORDER);
    }

    private void addAndSort(ObservableList<String> list, String item) {
        list.add(item);
        sortList(list);
    }

    // ── PERSISTENCE ──────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists())
            return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            List<String> loadedGenres = (List<String>) in.readObject();
            genreNames.addAll(loadedGenres);
            sortList(genreNames);

            Map<String, List<String>> loadedMovies = (Map<String, List<String>>) in.readObject();
            for (Map.Entry<String, List<String>> entry : loadedMovies.entrySet()) {
                ObservableList<String> sorted = FXCollections.observableArrayList(entry.getValue());
                sortList(sorted);
                moviesByGenre.put(entry.getKey(), sorted);
            }

            List<String> loadedCustomers = (List<String>) in.readObject();
            customerNames.addAll(loadedCustomers);
            sortList(customerNames);

            Map<String, List<String>> loadedBorrowed = (Map<String, List<String>>) in.readObject();
            for (Map.Entry<String, List<String>> entry : loadedBorrowed.entrySet()) {
                ObservableList<String> sorted = FXCollections.observableArrayList(entry.getValue());
                sortList(sorted);
                borrowedByCustomer.put(entry.getKey(), sorted);
            }

            Map<String, List<String>> loadedReturned = (Map<String, List<String>>) in.readObject();
            for (Map.Entry<String, List<String>> entry : loadedReturned.entrySet()) {
                ObservableList<String> sorted = FXCollections.observableArrayList(entry.getValue());
                sortList(sorted);
                returnedByCustomer.put(entry.getKey(), sorted);
            }

            showToast("Data loaded");
        } catch (Exception e) {
            showToast("Error loading data");
        }
    }

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(new ArrayList<>(genreNames));

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

            showToast("Data saved");
        } catch (IOException e) {
            showToast("Error saving data");
        }
    }

    // ── UI HELPERS ────────────────────────────────────────────────
    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13px; " +
                        "-fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; " +
                        "-fx-font-weight: bold;",
                COLOR_PRIMARY));
        btn.setMinWidth(120);
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(COLOR_PRIMARY, COLOR_PRIMARY_HOVER)));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(COLOR_PRIMARY_HOVER, COLOR_PRIMARY)));
        return btn;
    }

    private Button createDangerButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13px; " +
                        "-fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; " +
                        "-fx-font-weight: bold;",
                COLOR_DANGER));
        btn.setMinWidth(100);
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(COLOR_DANGER, COLOR_DANGER_HOVER)));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(COLOR_DANGER_HOVER, COLOR_DANGER)));
        return btn;
    }

    private TextField createInputField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-prompt-text-fill: %s; " +
                        "-fx-background-radius: 6; -fx-padding: 10 14; -fx-font-size: 13px; " +
                        "-fx-border-color: %s; -fx-border-radius: 6; -fx-border-width: 1;",
                COLOR_SURFACE, COLOR_TEXT, COLOR_TEXT_MUTED, COLOR_BORDER));
        field.setPrefWidth(200);
        return field;
    }

    private <T> ComboBox<T> createComboBox(ObservableList<T> items) {
        ComboBox<T> cb = new ComboBox<>(items);

        // CRITICAL FIX: Set the ComboBox button text to white using lookup
        Platform.runLater(() -> {
            // Force white text on the button area
            cb.lookup(".list-cell").setStyle(String.format(
                    "-fx-text-fill: %s; -fx-background-color: %s;",
                    COLOR_TEXT, COLOR_SURFACE));
        });

        // Style the main combobox
        cb.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 13px; " +
                        "-fx-background-radius: 6; -fx-border-color: %s; -fx-border-radius: 6; " +
                        "-fx-border-width: 1;",
                COLOR_SURFACE, COLOR_TEXT, COLOR_BORDER));

        // CRITICAL: Custom cell factory that forces WHITE text
        cb.setCellFactory(listView -> {
            ListCell<T> cell = new ListCell<T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle(String.format("-fx-background-color: %s;", COLOR_SURFACE));
                    } else {
                        setText(item.toString());
                        // FORCE WHITE TEXT
                        setTextFill(Color.WHITE);
                        setStyle(String.format(
                                "-fx-background-color: %s; -fx-padding: 10 14; -fx-text-fill: white;",
                                COLOR_SURFACE));
                    }
                }
            };
            return cell;
        });

        // CRITICAL: Button cell (what shows when selected) also white
        cb.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(cb.getPromptText());
                    // FORCE WHITE prompt text
                    setTextFill(Color.web(COLOR_TEXT_MUTED));
                    setStyle(String.format("-fx-background-color: %s;", COLOR_SURFACE));
                } else {
                    setText(item.toString());
                    // FORCE WHITE selected text
                    setTextFill(Color.WHITE);
                    setStyle(String.format("-fx-background-color: %s;", COLOR_SURFACE));
                }
            }
        });

        cb.setPrefWidth(200);
        return cb;
    }

    private Label createTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 28));
        label.setTextFill(Color.web(COLOR_TEXT));
        return label;
    }

    private Label createSubtitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", 14));
        label.setTextFill(Color.web(COLOR_TEXT_MUTED));
        return label;
    }

    private VBox createCard() {
        VBox card = new VBox(16);
        card.setStyle(String.format(
                "-fx-background-color: %s; -fx-background-radius: 12; -fx-padding: 24;",
                COLOR_SURFACE));
        card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.4)));
        return card;
    }

    private void showToast(String message) {
        Label toast = new Label(message);
        toast.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 12 24; " +
                        "-fx-background-radius: 8; -fx-font-size: 13px; -fx-font-weight: bold;",
                COLOR_SUCCESS));
        toast.setOpacity(0);

        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 40, 0));
        mainContent.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toast);
        fadeIn.setToValue(1);
        fadeIn.play();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), toast);
        fadeOut.setDelay(Duration.seconds(2));
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> mainContent.getChildren().remove(toast));
        fadeOut.play();
    }

    // ── NAVIGATION & MAIN LAYOUT ─────────────────────────────────
    @Override
    public void start(Stage stage) {
        // Sidebar setup
        VBox sidebar = new VBox(12);
        sidebar.setStyle(String.format("-fx-background-color: %s; -fx-padding: 24 16;", COLOR_SURFACE));
        sidebar.setPrefWidth(220);
        sidebar.setAlignment(Pos.TOP_CENTER);

        Label logo = new Label("🎬 MovieLib");
        logo.setFont(Font.font("System", FontWeight.BOLD, 22));
        logo.setTextFill(Color.web(COLOR_TEXT));
        VBox.setMargin(logo, new Insets(0, 0, 40, 0));

        ToggleGroup navGroup = new ToggleGroup();

        ToggleButton btnGenres = createNavButton("🎭  Genres", navGroup);
        ToggleButton btnMovies = createNavButton("🎬  Movies", navGroup);
        ToggleButton btnCustomers = createNavButton("👥  Customers", navGroup);
        ToggleButton btnRentals = createNavButton("📋  Rentals", navGroup);

        btnGenres.setOnAction(e -> showGenresView());
        btnMovies.setOnAction(e -> showMoviesView());
        btnCustomers.setOnAction(e -> showCustomersView());
        btnRentals.setOnAction(e -> showRentalsView());

        sidebar.getChildren().addAll(logo, btnGenres, btnMovies, btnCustomers, btnRentals);

        // Main content - INITIALIZE FIRST
        mainContent = new StackPane();
        mainContent.setStyle(String.format("-fx-background-color: %s;", COLOR_BG));

        // NOW load data (after mainContent exists)
        loadData();

        showGenresView();
        btnGenres.setSelected(true);

        HBox root = new HBox();
        root.getChildren().addAll(sidebar, mainContent);
        HBox.setHgrow(mainContent, Priority.ALWAYS);

        // CRITICAL FIX: Add CSS to force all combobox text white
        Scene scene = new Scene(root, 1000, 700);
        scene.getRoot().setStyle(String.format(
                "-fx-base: %s; -fx-control-inner-background: %s; -fx-text-fill: white;",
                COLOR_SURFACE, COLOR_SURFACE));

        stage.setOnCloseRequest((WindowEvent e) -> saveData());
        stage.setTitle("Movie Library System");
        stage.setScene(scene);
        stage.show();
    }

    private ToggleButton createNavButton(String text, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(group);
        btn.setPrefWidth(180);
        btn.setStyle(String.format(
                "-fx-background-color: transparent; -fx-text-fill: %s; " +
                        "-fx-font-size: 14px; -fx-padding: 14 20; -fx-background-radius: 10; " +
                        "-fx-alignment: CENTER_LEFT; -fx-cursor: hand; " +
                        "-fx-font-family: 'Segoe UI', 'System', sans-serif;",
                COLOR_TEXT_MUTED));

        btn.selectedProperty().addListener((obs, old, val) -> {
            if (val) {
                btn.setStyle(String.format(
                        "-fx-background-color: %s; -fx-text-fill: white; " +
                                "-fx-font-size: 14px; -fx-padding: 14 20; -fx-background-radius: 10; " +
                                "-fx-alignment: CENTER_LEFT; -fx-cursor: hand; " +
                                "-fx-font-weight: bold;",
                        COLOR_PRIMARY));
            } else {
                btn.setStyle(String.format(
                        "-fx-background-color: transparent; -fx-text-fill: %s; " +
                                "-fx-font-size: 14px; -fx-padding: 14 20; -fx-background-radius: 10; " +
                                "-fx-alignment: CENTER_LEFT; -fx-cursor: hand;",
                        COLOR_TEXT_MUTED));
            }
        });

        btn.setOnMouseEntered(e -> {
            if (!btn.isSelected()) {
                btn.setStyle(btn.getStyle().replace(COLOR_TEXT_MUTED, COLOR_TEXT));
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.isSelected()) {
                btn.setStyle(btn.getStyle().replace(COLOR_TEXT, COLOR_TEXT_MUTED));
            }
        });

        return btn;
    }

    // ── GENRES VIEW ──────────────────────────────────────────────
    private void showGenresView() {
        VBox container = new VBox(24);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);

        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.getChildren().addAll(createTitle("Genres"), createSubtitle(genreNames.size() + " categories"));

        // Add card
        VBox addCard = createCard();
        TextField nameField = createInputField("e.g. Action, Horror...");
        Button addBtn = createPrimaryButton("+ Add Genre");

        HBox addRow = new HBox(12);
        addRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        addRow.getChildren().addAll(nameField, addBtn);
        addCard.getChildren().addAll(createSubtitle("New Genre"), addRow);

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty() && !genreNames.contains(name)) {
                addAndSort(genreNames, name);
                moviesByGenre.put(name, FXCollections.observableArrayList());
                nameField.clear();
                saveData();
                showGenresView();
                showToast("Added: " + name);
            }
        });

        // List card
        VBox listCard = createCard();
        if (genreNames.isEmpty()) {
            VBox empty = new VBox(16);
            empty.setAlignment(Pos.CENTER);
            empty.getChildren().addAll(
                    new Label("🎭") {
                        {
                            setFont(Font.font(48));
                            setTextFill(Color.web(COLOR_TEXT_MUTED));
                        }
                    },
                    createSubtitle("No genres yet. Add your first genre above."));
            listCard.getChildren().addAll(createSubtitle("Manage Genres"), empty);
        } else {
            ComboBox<String> genreCombo = createComboBox(genreNames);
            genreCombo.setPromptText("Select genre to delete");
            genreCombo.setPrefWidth(300);

            Button deleteBtn = createDangerButton("Delete");
            deleteBtn.setOnAction(e -> {
                String selected = genreCombo.getValue();
                if (selected != null) {
                    ObservableList<String> movies = moviesByGenre.get(selected);
                    if (movies == null || movies.isEmpty()) {
                        genreNames.remove(selected);
                        moviesByGenre.remove(selected);
                        saveData();
                        showGenresView();
                        showToast("Deleted: " + selected);
                    } else {
                        showToast("Cannot delete: genre has movies");
                    }
                }
            });

            HBox manageRow = new HBox(12);
            manageRow.setAlignment(Pos.CENTER_LEFT);
            manageRow.getChildren().addAll(genreCombo, deleteBtn);
            listCard.getChildren().addAll(createSubtitle("Manage Genres"), manageRow);
        }

        container.getChildren().addAll(header, addCard, listCard);
        mainContent.getChildren().setAll(container);
    }

    // ── MOVIES VIEW ──────────────────────────────────────────────
    private void showMoviesView() {
        VBox container = new VBox(24);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);

        int totalMovies = moviesByGenre.values().stream().mapToInt(ObservableList::size).sum();
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.getChildren().addAll(createTitle("Movies"), createSubtitle(totalMovies + " total"));

        // Add card
        VBox addCard = createCard();
        ComboBox<String> genreCombo = createComboBox(genreNames);
        genreCombo.setPromptText("Select genre");
        TextField nameField = createInputField("Movie title");
        Button addBtn = createPrimaryButton("+ Add Movie");

        HBox addRow = new HBox(12);
        addRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        addRow.getChildren().addAll(genreCombo, nameField, addBtn);
        addCard.getChildren().addAll(createSubtitle("New Movie"), addRow);

        addBtn.setOnAction(e -> {
            String genre = genreCombo.getValue();
            String name = nameField.getText().trim();
            if (genre != null && !name.isEmpty()) {
                ObservableList<String> list = moviesByGenre.computeIfAbsent(genre,
                        k -> FXCollections.observableArrayList());
                addAndSort(list, name);
                nameField.clear();
                saveData();
                showMoviesView();
                showToast("Added: " + name);
            }
        });

        // List card
        VBox listCard = createCard();
        if (moviesByGenre.isEmpty() || moviesByGenre.values().stream().allMatch(List::isEmpty)) {
            VBox empty = new VBox(16);
            empty.setAlignment(Pos.CENTER);
            empty.getChildren().addAll(
                    new Label("🎬") {
                        {
                            setFont(Font.font(48));
                            setTextFill(Color.web(COLOR_TEXT_MUTED));
                        }
                    },
                    createSubtitle("No movies yet. Add your first movie above."));
            listCard.getChildren().addAll(createSubtitle("Manage Movies"), empty);
        } else {
            ComboBox<String> genreSelect = createComboBox(genreNames
                    .filtered(g -> !moviesByGenre.getOrDefault(g, FXCollections.emptyObservableList()).isEmpty()));
            genreSelect.setPromptText("Select genre");

            ComboBox<String> movieSelect = createComboBox(FXCollections.observableArrayList());
            movieSelect.setPromptText("Select movie");
            movieSelect.setDisable(true);

            genreSelect.setOnAction(e -> {
                String g = genreSelect.getValue();
                if (g != null) {
                    movieSelect.setItems(moviesByGenre.getOrDefault(g, FXCollections.observableArrayList()));
                    movieSelect.setDisable(false);
                }
            });

            Button deleteBtn = createDangerButton("Delete");
            deleteBtn.setOnAction(e -> {
                String genre = genreSelect.getValue();
                String movie = movieSelect.getValue();
                if (genre != null && movie != null) {
                    moviesByGenre.get(genre).remove(movie);
                    saveData();
                    showMoviesView();
                    showToast("Deleted: " + movie);
                }
            });

            HBox manageRow = new HBox(12);
            manageRow.setAlignment(Pos.CENTER_LEFT);
            manageRow.getChildren().addAll(genreSelect, movieSelect, deleteBtn);
            listCard.getChildren().addAll(createSubtitle("Manage Movies"), manageRow);
        }

        container.getChildren().addAll(header, addCard, listCard);
        mainContent.getChildren().setAll(container);
    }

    // ── CUSTOMERS VIEW ───────────────────────────────────────────
    private void showCustomersView() {
        VBox container = new VBox(24);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);

        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.getChildren().addAll(createTitle("Customers"), createSubtitle(customerNames.size() + " registered"));

        // Add card
        VBox addCard = createCard();
        TextField nameField = createInputField("Full name");
        nameField.setPrefWidth(180);
        TextField phoneField = createInputField("Phone");
        phoneField.setPrefWidth(140);
        TextField emailField = createInputField("Email");
        emailField.setPrefWidth(180);
        Button addBtn = createPrimaryButton("+ Add Customer");

        HBox addRow = new HBox(10);
        addRow.setAlignment(Pos.CENTER_LEFT);
        addRow.getChildren().addAll(nameField, phoneField, emailField, addBtn);
        addCard.getChildren().addAll(createSubtitle("New Customer"), addRow);

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty() && !customerNames.contains(name)) {
                addAndSort(customerNames, name);
                borrowedByCustomer.put(name, FXCollections.observableArrayList());
                returnedByCustomer.put(name, FXCollections.observableArrayList());
                nameField.clear();
                phoneField.clear();
                emailField.clear();
                saveData();
                showCustomersView();
                showToast("Added: " + name);
            }
        });

        // List card
        VBox listCard = createCard();
        if (customerNames.isEmpty()) {
            VBox empty = new VBox(16);
            empty.setAlignment(Pos.CENTER);
            empty.getChildren().addAll(
                    new Label("👥") {
                        {
                            setFont(Font.font(48));
                            setTextFill(Color.web(COLOR_TEXT_MUTED));
                        }
                    },
                    createSubtitle("No customers yet. Add your first customer above."));
            listCard.getChildren().addAll(createSubtitle("Manage Customers"), empty);
        } else {
            ComboBox<String> customerCombo = createComboBox(customerNames);
            customerCombo.setPromptText("Select customer");
            customerCombo.setPrefWidth(300);

            int activeRentals = borrowedByCustomer.values().stream().mapToInt(ObservableList::size).sum();
            Label rentalsLabel = new Label(activeRentals + " active rentals");
            rentalsLabel.setTextFill(Color.web(COLOR_ACCENT));
            rentalsLabel.setFont(Font.font("System", 13));

            Button deleteBtn = createDangerButton("Delete");
            deleteBtn.setOnAction(e -> {
                String selected = customerCombo.getValue();
                if (selected != null) {
                    ObservableList<String> borrowed = borrowedByCustomer.get(selected);
                    if (borrowed == null || borrowed.isEmpty()) {
                        customerNames.remove(selected);
                        borrowedByCustomer.remove(selected);
                        returnedByCustomer.remove(selected);
                        saveData();
                        showCustomersView();
                        showToast("Deleted: " + selected);
                    } else {
                        showToast("Cannot delete: customer has rentals");
                    }
                }
            });

            HBox manageRow = new HBox(12);
            manageRow.setAlignment(Pos.CENTER_LEFT);
            manageRow.getChildren().addAll(customerCombo, rentalsLabel, deleteBtn);
            listCard.getChildren().addAll(createSubtitle("Manage Customers"), manageRow);
        }

        container.getChildren().addAll(header, addCard, listCard);
        mainContent.getChildren().setAll(container);
    }

    // ── RENTALS VIEW ─────────────────────────────────────────────
    private void showRentalsView() {
        VBox container = new VBox(24);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);

        int activeCount = borrowedByCustomer.values().stream().mapToInt(ObservableList::size).sum();
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.getChildren().addAll(createTitle("Rentals"), createSubtitle(activeCount + " active rentals"));

        // Rent card
        VBox rentCard = createCard();
        ComboBox<String> customerCombo = createComboBox(customerNames);
        customerCombo.setPromptText("Select customer");

        // Get available movies
        ObservableList<String> availableMovies = FXCollections.observableArrayList();
        for (ObservableList<String> list : moviesByGenre.values()) {
            availableMovies.addAll(list);
        }
        FXCollections.sort(availableMovies, String.CASE_INSENSITIVE_ORDER);

        ComboBox<String> movieCombo = createComboBox(availableMovies);
        movieCombo.setPromptText("Select movie");

        Button rentBtn = createPrimaryButton("Rent Movie");

        HBox rentRow = new HBox(12);
        rentRow.setAlignment(Pos.CENTER_LEFT);
        rentRow.getChildren().addAll(customerCombo, movieCombo, rentBtn);
        rentCard.getChildren().addAll(createSubtitle("New Rental"), rentRow);

        rentBtn.setOnAction(e -> {
            String customer = customerCombo.getValue();
            String movie = movieCombo.getValue();
            if (customer != null && movie != null) {
                ObservableList<String> borrowed = borrowedByCustomer.computeIfAbsent(customer,
                        k -> FXCollections.observableArrayList());
                addAndSort(borrowed, movie);
                availableMovies.remove(movie);
                saveData();
                showRentalsView();
                showToast("Rented: " + movie);
            }
        });

        // Return card
        VBox returnCard = createCard();

        // Get all borrowed movies with customer info
        ObservableList<String> borrowedDisplay = FXCollections.observableArrayList();
        Map<String, String> displayToMovie = new HashMap<>();
        for (Map.Entry<String, ObservableList<String>> entry : borrowedByCustomer.entrySet()) {
            for (String movie : entry.getValue()) {
                String display = movie + " → " + entry.getKey();
                borrowedDisplay.add(display);
                displayToMovie.put(display, movie);
            }
        }
        FXCollections.sort(borrowedDisplay, String.CASE_INSENSITIVE_ORDER);

        ComboBox<String> returnCombo = createComboBox(borrowedDisplay);
        returnCombo.setPromptText("Select rental to return");
        returnCombo.setPrefWidth(400);

        Button returnBtn = createPrimaryButton("Process Return");
        returnBtn.setStyle(returnBtn.getStyle().replace(COLOR_PRIMARY, COLOR_SUCCESS)
                .replace(COLOR_PRIMARY_HOVER, "#059669"));

        HBox returnRow = new HBox(12);
        returnRow.setAlignment(Pos.CENTER_LEFT);
        returnRow.getChildren().addAll(returnCombo, returnBtn);
        returnCard.getChildren().addAll(createSubtitle("Process Return"), returnRow);

        returnBtn.setOnAction(e -> {
            String selected = returnCombo.getValue();
            if (selected != null) {
                String movie = displayToMovie.get(selected);
                String customer = selected.split(" → ")[1];

                borrowedByCustomer.get(customer).remove(movie);
                ObservableList<String> returned = returnedByCustomer.computeIfAbsent(customer,
                        k -> FXCollections.observableArrayList());
                addAndSort(returned, movie);

                saveData();
                showRentalsView();
                showToast("Returned: " + movie);
            }
        });

        // History card
        VBox historyCard = createCard();
        int returnedCount = returnedByCustomer.values().stream().mapToInt(ObservableList::size).sum();

        if (returnedCount == 0) {
            VBox empty = new VBox(16);
            empty.setAlignment(Pos.CENTER);
            empty.getChildren().addAll(
                    new Label("📋") {
                        {
                            setFont(Font.font(48));
                            setTextFill(Color.web(COLOR_TEXT_MUTED));
                        }
                    },
                    createSubtitle("No returned rentals yet."));
            historyCard.getChildren().addAll(createSubtitle("History"), empty);
        } else {
            ComboBox<String> historyCombo = createComboBox(FXCollections.observableArrayList());
            historyCombo.setPromptText("Select customer");
            historyCombo.setItems(customerNames
                    .filtered(c -> !returnedByCustomer.getOrDefault(c, FXCollections.emptyObservableList()).isEmpty()));

            ComboBox<String> returnedCombo = createComboBox(FXCollections.observableArrayList());
            returnedCombo.setPromptText("Returned movies");
            returnedCombo.setDisable(true);

            historyCombo.setOnAction(e -> {
                String c = historyCombo.getValue();
                if (c != null) {
                    returnedCombo.setItems(returnedByCustomer.getOrDefault(c, FXCollections.observableArrayList()));
                    returnedCombo.setDisable(false);
                }
            });

            HBox historyRow = new HBox(12);
            historyRow.setAlignment(Pos.CENTER_LEFT);
            historyRow.getChildren().addAll(historyCombo, returnedCombo);
            historyCard.getChildren().addAll(createSubtitle("History"), historyRow);
        }

        container.getChildren().addAll(header, rentCard, returnCard, historyCard);
        mainContent.getChildren().setAll(container);
    }

    public static void main(String[] args) {
        launch(args);
    }
}