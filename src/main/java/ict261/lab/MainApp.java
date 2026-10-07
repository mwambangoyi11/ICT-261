package ict261.lab;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * ICT261 Lecture 3 - Customer Manager classroom lab.
 *
 * 1. Form with a name field and province list.
 * 2. Customer model + ObservableList<Customer>.
 * 3. TableView with name and province columns.
 * 4. Validate input, then add the customer.
 * 5. Confirm deletion of a selected customer.
 * 6. Keyboard access (mnemonics, Enter = Add, Delete key, tab order).
 */
public class MainApp extends Application {

    private static final String[] PROVINCES = {
            "Central", "Copperbelt", "Eastern", "Luapula", "Lusaka",
            "Muchinga", "Northern", "North-Western", "Southern", "Western"
    };

    // Step 2: the data list that backs the table
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    private TextField nameField;
    private ComboBox<String> provinceBox;
    private Label errorLabel;
    private Label countLabel;
    private TableView<Customer> table;
    private Button addButton;
    private Button deleteButton;

    @Override
    public void start(Stage stage) {
        // ---------- Step 1: form ----------
        Label nameLabel = new Label("_Name:");
        nameField = new TextField();
        nameField.setPromptText("Enter customer name");
        nameLabel.setLabelFor(nameField);
        nameLabel.setMnemonicParsing(true);

        Label provinceLabel = new Label("_Province:");
        provinceBox = new ComboBox<>(FXCollections.observableArrayList(PROVINCES));
        provinceBox.setPromptText("Select province");
        provinceBox.setMaxWidth(Double.MAX_VALUE);
        provinceLabel.setLabelFor(provinceBox);
        provinceLabel.setMnemonicParsing(true);

        addButton = new Button("_Add Customer");
        addButton.setDefaultButton(true); // Enter key triggers Add
        addButton.setOnAction(e -> addCustomer());

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #b00020;");
        errorLabel.setWrapText(true);
        errorLabel.setAccessibleText("Validation message");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(nameLabel, 0, 0);
        form.add(nameField, 1, 0);
        form.add(provinceLabel, 0, 1);
        form.add(provinceBox, 1, 1);
        form.add(addButton, 1, 2);
        form.add(errorLabel, 1, 3);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(provinceBox, Priority.ALWAYS);

        // ---------- Step 3: TableView ----------
        table = new TableView<>(customers);
        table.setPlaceholder(new Label("No customers yet."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());

        TableColumn<Customer, String> provinceCol = new TableColumn<>("Province");
        provinceCol.setCellValueFactory(c -> c.getValue().provinceProperty());

        table.getColumns().add(nameCol);
        table.getColumns().add(provinceCol);
        VBox.setVgrow(table, Priority.ALWAYS);

        // ---------- Step 5: delete ----------
        deleteButton = new Button("_Delete Selected");
        deleteButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.setOnAction(e -> deleteSelected());

        // Step 6: Delete key on the table also removes the selected row (with confirmation)
        table.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE && !deleteButton.isDisabled()) {
                deleteSelected();
            }
        });

        countLabel = new Label();
        countLabel.textProperty().bind(
                javafx.beans.binding.Bindings.size(customers).asString("Customers: %d"));

        HBox bottomBar = new HBox(15, deleteButton, countLabel);
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        // Clear error as soon as the user edits the form again
        nameField.textProperty().addListener((obs, o, n) -> errorLabel.setText(""));
        provinceBox.valueProperty().addListener((obs, o, n) -> errorLabel.setText(""));

        VBox root = new VBox(15, new Label("Customer Manager"), form, table, bottomBar);
        root.setPadding(new Insets(20));
        ((Label) root.getChildren().get(0)).setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Scene scene = new Scene(root, 520, 560);

        // Accelerator: Ctrl+N focuses the name field
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN),
                () -> nameField.requestFocus());

        stage.setTitle("Customer Manager");
        stage.setScene(scene);
        stage.show();
        nameField.requestFocus();
    }

    // ---------- Step 4: validate then add ----------
    private void addCustomer() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String province = provinceBox.getValue();

        if (name.isEmpty()) {
            showError("Name is required.", nameField);
            return;
        }
        if (name.length() < 2) {
            showError("Name must be at least 2 characters.", nameField);
            return;
        }
        if (!name.matches("[\\p{L} .'-]+")) {
            showError("Name may only contain letters, spaces, apostrophes, dots and hyphens.", nameField);
            return;
        }
        if (province == null) {
            showError("Please select a province.", provinceBox);
            return;
        }
        boolean duplicate = customers.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name) && c.getProvince().equals(province));
        if (duplicate) {
            showError("This customer already exists in " + province + ".", nameField);
            return;
        }

        customers.add(new Customer(name, province));
        nameField.clear();
        provinceBox.getSelectionModel().clearSelection();
        errorLabel.setText("");
        nameField.requestFocus();
    }

    private void showError(String message, Control toFocus) {
        errorLabel.setText(message);
        toFocus.requestFocus();
    }

    // ---------- Step 5: confirm deletion ----------
    private void deleteSelected() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm deletion");
        confirm.setHeaderText("Delete customer?");
        confirm.setContentText("Are you sure you want to delete " + selected + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            customers.remove(selected);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
