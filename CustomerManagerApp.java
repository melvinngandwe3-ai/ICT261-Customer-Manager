package com.mulungushi.customermanager;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CustomerManagerApp extends Application {

    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    private final TextField nameField = new TextField();
    private final ComboBox<String> provinceBox = new ComboBox<>();
    private final TableView<Customer> table = new TableView<>();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Customer Manager");

        // 1. Form with name field and province list
        nameField.setPromptText("Enter customer name");
        nameField.setAccessibleText("Customer name");

        provinceBox.getItems().addAll(
                "Central", "Copperbelt", "Eastern", "Luapula",
                "Lusaka", "Muchinga", "Northern", "North-Western",
                "Southern", "Western"
        );
        provinceBox.setPromptText("Select province");
        provinceBox.setAccessibleText("Province");

        // 3. TableView with name and province columns
        TableColumn<Customer, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));
        nameColumn.setPrefWidth(240);

        TableColumn<Customer, String> provinceColumn = new TableColumn<>("Province");
        provinceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProvince()));
        provinceColumn.setPrefWidth(180);

        table.getColumns().addAll(nameColumn, provinceColumn);
        table.setItems(customers);
        table.setPlaceholder(new Label("No customers added yet."));
        table.setAccessibleText("Customer table");

        Button addButton = new Button("Add Customer");
        Button deleteButton = new Button("Delete Selected");

        addButton.setDefaultButton(true);
        deleteButton.setCancelButton(false);

        // 4. Validate input, then add customer
        addButton.setOnAction(event -> addCustomer());

        // 5. Confirm deletion
        deleteButton.setOnAction(event -> deleteSelectedCustomer());

        // 6. Keyboard access
        nameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addCustomer();
            }
        });

        provinceBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addCustomer();
            }
        });

        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                deleteSelectedCustomer();
            }
        });

        HBox buttons = new HBox(10, addButton, deleteButton);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Province:"), 0, 1);
        form.add(provinceBox, 1, 1);

        VBox layout = new VBox(15, form, buttons, table);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 520, 500);
        stage.setScene(scene);
        stage.show();

        nameField.requestFocus();
    }

    private void addCustomer() {
        String name = nameField.getText().trim();
        String province = provinceBox.getValue();

        // Validation
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input",
                    "Please enter the customer's name.");
            nameField.requestFocus();
            return;
        }

        if (province == null || province.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input",
                    "Please select a province.");
            provinceBox.requestFocus();
            return;
        }

        customers.add(new Customer(name, province));

        nameField.clear();
        provinceBox.getSelectionModel().clearSelection();
        nameField.requestFocus();
    }

    private void deleteSelectedCustomer() {
        Customer selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a customer to delete.");
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete " + selected.getName() + "?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete selected customer?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                customers.remove(selected);
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
