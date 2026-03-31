import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Optional;

// Launches the JavaFX Donation Management System GUI.
// This class builds the interface and connects the buttons,
// table, form, and status messages to the donation system.

public class Main extends Application {
    private final DonationManager donationManager = new DonationManager();
    private final TableView<Donation> tableView = new TableView<>();
    private final Label statusLabel = new Label("Ready.");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    private TextField idField;
    private TextField nameField;
    private TextField emailField;
    private DatePicker datePicker;
    private TextField amountField;
    private ComboBox<String> fundComboBox;
    private ComboBox<String> paymentComboBox;

    // Builds and displays the main JavaFX window.
    // This method sets up the main layout sections of the app.
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #f4f8fb;");

        VBox header = buildHeader();
        root.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 15, 0));

        // Set up the donation table and place it in the center area.
        setupTable();

        Label tableSectionLabel = new Label("Donation Records");
        tableSectionLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1f4e79;"
        );

        VBox tableSection = new VBox(10, tableSectionLabel, tableView);
        tableSection.setPadding(new Insets(12));
        tableSection.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #d6e0ea;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        root.setCenter(tableSection);
        BorderPane.setMargin(tableSection, new Insets(0, 15, 0, 0));

        // Build the donation input form and place it on the right side.
        VBox formPane = buildFormPane();
        root.setRight(formPane);
        BorderPane.setMargin(formPane, new Insets(0, 0, 0, 5));

        // Build the bottom button bar.
        HBox buttonBar = buildButtonBar(stage);

        statusLabel.setStyle(
                "-fx-text-fill: #1f4e79;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 3 0 0 2;"
        );

        VBox bottomPane = new VBox(12, buttonBar, statusLabel);
        bottomPane.setPadding(new Insets(15, 0, 0, 0));
        root.setBottom(bottomPane);

        // Make table row selection automatically fill the form.
        setupTableSelectionListener();

        Scene scene = new Scene(root, 1380, 740);
        stage.setTitle("DMS JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    // Builds the title area at the top of the application.
    private VBox buildHeader() {
        Label title = new Label("💙 Donation Management System");
        title.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1f4e79;" +
                        "-fx-padding: 0 0 2 0;"
        );

        Label subtitle = new Label("Manage donations, update records, and calculate totals by fund.");
        subtitle.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #5f6f7f;" +
                        "-fx-padding: 0 0 2 2;"
        );

        VBox header = new VBox(4, title, subtitle);
        header.setPadding(new Insets(0, 0, 5, 2));
        return header;
    }

    // Configures the table columns and binds the table
    // to the observable list of donation records.
    private void setupTable() {
        TableColumn<Donation, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getDonationId()));

        TableColumn<Donation, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getDonorName()));

        TableColumn<Donation, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getDonorEmail()));

        TableColumn<Donation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getDonationDate().toString()));

        TableColumn<Donation, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getAmount()));

        // Format the amount column as currency.
        amountCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(amount));
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        TableColumn<Donation, String> fundCol = new TableColumn<>("Fund");
        fundCol.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getFund()));

        TableColumn<Donation, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getPaymentMethod()));

        tableView.getColumns().clear();
        tableView.getColumns().addAll(idCol, nameCol, emailCol, dateCol, amountCol, fundCol, paymentCol);
        tableView.setItems(donationManager.getDonations());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.setPrefHeight(500);
        tableView.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #c9d6e3;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-table-cell-border-color: #d9e3ec;"
        );

        // Apply alternating row colors and a selection highlight.
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Donation item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #cfe2f3;");
                } else if (getIndex() % 2 == 0) {
                    setStyle("-fx-background-color: #ffffff;");
                } else {
                    setStyle("-fx-background-color: #f8fbfe;");
                }
            }
        });
    }

    // Builds the donation entry form on the right side of the window.
    private VBox buildFormPane() {
        Label formTitle = new Label("Donation Form");
        formTitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1f4e79;"
        );

        idField = new TextField();
        idField.setPromptText("6-digit ID");

        nameField = new TextField();
        nameField.setPromptText("Full name");

        emailField = new TextField();
        emailField.setPromptText("name@email.com");

        datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        amountField = new TextField();
        amountField.setPromptText("0.00");

        fundComboBox = new ComboBox<>();
        fundComboBox.getItems().addAll("General", "Missions", "Building", "Youth");
        fundComboBox.setValue("General");

        paymentComboBox = new ComboBox<>();
        paymentComboBox.getItems().addAll("Cash", "Check", "Card");
        paymentComboBox.setValue("Cash");

        // Apply consistent styling to the input controls.
        styleInputControl(idField);
        styleInputControl(nameField);
        styleInputControl(emailField);
        styleInputControl(amountField);
        styleInputControl(datePicker);
        styleInputControl(fundComboBox);
        styleInputControl(paymentComboBox);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(12);

        formGrid.add(styledFormLabel("Donation ID:"), 0, 0);
        formGrid.add(idField, 1, 0);

        formGrid.add(styledFormLabel("Donor Name:"), 0, 1);
        formGrid.add(nameField, 1, 1);

        formGrid.add(styledFormLabel("Donor Email:"), 0, 2);
        formGrid.add(emailField, 1, 2);

        formGrid.add(styledFormLabel("Donation Date:"), 0, 3);
        formGrid.add(datePicker, 1, 3);

        formGrid.add(styledFormLabel("Amount:"), 0, 4);
        formGrid.add(amountField, 1, 4);

        formGrid.add(styledFormLabel("Fund:"), 0, 5);
        formGrid.add(fundComboBox, 1, 5);

        formGrid.add(styledFormLabel("Payment Method:"), 0, 6);
        formGrid.add(paymentComboBox, 1, 6);

        VBox formPane = new VBox(15, formTitle, formGrid);
        formPane.setPadding(new Insets(20));
        formPane.setPrefWidth(360);
        formPane.setStyle(
                "-fx-background-color: #eef4fa;" +
                        "-fx-border-color: #d6e0ea;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        return formPane;
    }

    // Creates a styled label for each field name in the form.
    private Label styledFormLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1f4e79;"
        );
        return label;
    }

    // Applies shared styling to all form input controls.
    private void styleInputControl(Control control) {
        control.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #b8c7d9;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;"
        );
        control.setPrefWidth(190);
    }

    // Builds the main action buttons shown at the bottom of the window.
    private HBox buildButtonBar(Stage stage) {
        Button loadFileButton = new Button("📂 Load File");
        Button addDonationButton = new Button("➕ Add Donation");
        Button updateDonationButton = new Button("✏️ Update Donation");
        Button deleteDonationButton = new Button("🗑 Delete Donation");
        Button totalsButton = new Button("📊 View Totals");
        Button clearFormButton = new Button("🧹 Clear Form");
        Button exitButton = new Button("❌ Exit");

        // Apply color styling to each button.
        stylePrimaryButton(loadFileButton, "#d9edf7");
        stylePrimaryButton(addDonationButton, "#d4edda");
        stylePrimaryButton(updateDonationButton, "#fff3cd");
        stylePrimaryButton(deleteDonationButton, "#f8d7da");
        stylePrimaryButton(totalsButton, "#e6dcf5");
        stylePrimaryButton(clearFormButton, "#f0f0f0");
        stylePrimaryButton(exitButton, "#f5d6d6");

        // Connect button actions to their methods.
        loadFileButton.setOnAction(e -> loadDonationsFromFile(stage));
        addDonationButton.setOnAction(e -> addDonation());
        updateDonationButton.setOnAction(e -> updateSelectedDonation());
        deleteDonationButton.setOnAction(e -> deleteSelectedDonation());
        totalsButton.setOnAction(e -> showTotals());
        clearFormButton.setOnAction(e -> clearForm());
        exitButton.setOnAction(e -> stage.close());

        HBox buttonBar = new HBox(
                10,
                loadFileButton,
                addDonationButton,
                updateDonationButton,
                deleteDonationButton,
                totalsButton,
                clearFormButton,
                exitButton
        );
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        buttonBar.setPadding(new Insets(2, 0, 0, 0));
        return buttonBar;
    }

    // Applies shared styling to action buttons.
    private void stylePrimaryButton(Button button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: #1f1f1f;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 8 14 8 14;"
        );
        button.setPrefHeight(38);
    }

    // When a user selects a row in the table,
    // copy that donation's data into the form fields.
    private void setupTableSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selected) -> {
            if (selected != null) {
                idField.setText(selected.getDonationId());
                nameField.setText(selected.getDonorName());
                emailField.setText(selected.getDonorEmail());
                datePicker.setValue(selected.getDonationDate());
                amountField.setText(String.valueOf(selected.getAmount()));
                fundComboBox.setValue(selected.getFund());
                paymentComboBox.setValue(selected.getPaymentMethod());
            }
        });
    }

    // Opens a file chooser and loads donation data from a selected text file.
    private void loadDonationsFromFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Donation File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {
            statusLabel.setText("File load cancelled.");
            return;
        }

        int loadedCount = donationManager.loadFromFile(file.getAbsolutePath());
        statusLabel.setText(loadedCount + " donation(s) loaded from file.");
    }

    // Reads the form, validates the data, and adds a new donation.
    private void addDonation() {
        Donation donation = buildDonationFromForm();
        if (donation == null) {
            return;
        }

        if (!donationManager.addDonation(donation)) {
            showError("Add Failed", "Donation could not be added. ID may already exist.");
            statusLabel.setText("Could not add donation.");
            return;
        }

        statusLabel.setText("Donation added successfully.");
        clearFormFieldsOnly();
    }

    // Updates the currently selected donation using the values in the form.
    private void updateSelectedDonation() {
        Donation selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            statusLabel.setText("Please select a donation to update.");
            showError("No Selection", "Please select a donation from the table before updating.");
            return;
        }

        Donation updatedDonation = buildDonationFromForm();
        if (updatedDonation == null) {
            return;
        }

        boolean updated = donationManager.updateDonation(selected.getDonationId(), updatedDonation);
        if (updated) {
            tableView.refresh();
            statusLabel.setText("Donation updated successfully.");
            clearForm();
        } else {
            showError("Update Failed", "Donation could not be updated.");
            statusLabel.setText("Could not update donation.");
        }
    }

    // Deletes the selected donation after the user confirms the action.
    private void deleteSelectedDonation() {
        Donation selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            statusLabel.setText("Please select a donation to delete.");
            showError("No Selection", "Please select a donation from the table before deleting.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Donation Record");
        confirmAlert.setContentText("Are you sure you want to delete donation ID " + selected.getDonationId() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            statusLabel.setText("Delete cancelled.");
            return;
        }

        boolean removed = donationManager.deleteDonation(selected.getDonationId());
        if (removed) {
            statusLabel.setText("Donation deleted successfully.");
            clearForm();
        } else {
            statusLabel.setText("Donation could not be deleted.");
            showError("Delete Failed", "Donation could not be deleted.");
        }
    }

    // Builds a Donation object from the form fields.
    // Returns null if validation fails.
    private Donation buildDonationFromForm() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            LocalDate date = datePicker.getValue();
            double amount = Double.parseDouble(amountField.getText().trim());
            String fund = fundComboBox.getValue();
            String payment = paymentComboBox.getValue();

            Donation donation = new Donation(id, name, email, date, amount, fund, payment);

            String error = DonationValidator.validateDonation(donation);
            if (error != null) {
                showError("Invalid Input", error);
                statusLabel.setText("Please correct the form.");
                return null;
            }

            return donation;
        } catch (NumberFormatException ex) {
            showError("Invalid Input", "Amount must be a valid number.");
            statusLabel.setText("Please correct the amount field.");
            return null;
        } catch (Exception ex) {
            showError("Invalid Input", "Please fill in all fields correctly.");
            statusLabel.setText("Please correct the form.");
            return null;
        }
    }

    // Clears the form and removes the current table selection.
    private void clearForm() {
        clearFormFieldsOnly();
        tableView.getSelectionModel().clearSelection();
        statusLabel.setText("Form cleared.");
    }

    // Clears only the field values and resets defaults.
    private void clearFormFieldsOnly() {
        idField.clear();
        nameField.clear();
        emailField.clear();
        datePicker.setValue(LocalDate.now());
        amountField.clear();
        fundComboBox.setValue("General");
        paymentComboBox.setValue("Cash");
    }

    // Shows total donations by fund and the overall grand total.
    private void showTotals() {
        double general = donationManager.getTotalByFund("General");
        double missions = donationManager.getTotalByFund("Missions");
        double building = donationManager.getTotalByFund("Building");
        double youth = donationManager.getTotalByFund("Youth");
        double grandTotal = donationManager.getGrandTotal();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Donation Totals");
        alert.setHeaderText("Totals by Fund");
        alert.setContentText(
                "General: " + currencyFormat.format(general) + "\n" +
                        "Missions: " + currencyFormat.format(missions) + "\n" +
                        "Building: " + currencyFormat.format(building) + "\n" +
                        "Youth: " + currencyFormat.format(youth) + "\n\n" +
                        "Grand Total: " + currencyFormat.format(grandTotal)
        );
        alert.showAndWait();
    }

    // Displays an error alert to the user.
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Launches the JavaFX application.
    public static void main(String[] args) {
        launch();
    }
}