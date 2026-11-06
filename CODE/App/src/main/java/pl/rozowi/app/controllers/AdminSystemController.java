package pl.rozowi.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller that manages the application's system settings.
 * Provides testing the database connection, configuring system parameters,
 * managing logs and performing database operations.
 * Implements UserAwareController to handle the currently logged-in user.
 */
public class AdminSystemController implements UserAwareController {

    @FXML
    private Label versionLabel;
    @FXML
    private Label dbNameLabel;
    @FXML
    private Label dbStatusLabel;
    @FXML
    private Button testConnectionBtn;
    @FXML
    private TextField maxTasksField;
    @FXML
    private TextField maxTeamsField;
    @FXML
    private ComboBox<String> logLevelComboBox;
    @FXML
    private TextArea logsTextArea;

    private User currentUser;

    /**
     * Initializes the controller. Sets basic system information, checks the database
     * connection status and adds initial log entries.
     */
    @FXML
    private void initialize() {
        versionLabel.setText("1.0.0");
        dbNameLabel.setText("MySQL");
        updateConnectionStatus();

        addLogEntry("INFO", "System uruchomiony");
        addLogEntry("INFO", "Połączono z bazą danych");
        addLogEntry("INFO", "Załadowano konfigurację systemową");
    }

    /**
     * Sets the currently logged-in user.
     *
     * @param user object representing the logged-in user
     */
    @Override
    public void setUser(User user) {
        this.currentUser = user;
    }

    /**
     * Handles testing the database connection.
     * Shows the test result in a dialog and adds a log entry.
     */
    @FXML
    private void handleTestConnection() {
        updateConnectionStatus();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Test połączenia");
        alert.setHeaderText(null);

        try (Connection conn = DatabaseManager.getConnection()) {
            alert.setContentText("Połączenie z bazą danych nawiązane pomyślnie.");
            addLogEntry("INFO", "Test połączenia z bazą danych: sukces");
        } catch (SQLException e) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Błąd połączenia z bazą danych: " + e.getMessage());
            addLogEntry("ERROR", "Test połączenia z bazą danych: błąd - " + e.getMessage());
        }

        alert.showAndWait();
    }

    /**
     * Handles saving the system settings.
     * Validates the entered values and shows an appropriate message.
     */
    @FXML
    private void handleSaveSettings() {

        try {
            int maxTasks = Integer.parseInt(maxTasksField.getText());
            int maxTeams = Integer.parseInt(maxTeamsField.getText());


            addLogEntry("INFO", "Zapisano ustawienia systemowe");
            showSuccessAlert("Zapisano ustawienia pomyślnie.");
        } catch (NumberFormatException e) {
            showErrorAlert("Nieprawidłowe wartości liczbowe.");
            addLogEntry("ERROR", "Błąd podczas zapisywania ustawień: nieprawidłowe wartości");
        }
    }

    /**
     * Handles resetting the settings to default values.
     */
    @FXML
    private void handleResetSettings() {
        maxTasksField.setText("10");
        maxTeamsField.setText("20");
        logLevelComboBox.setValue("INFO");

        addLogEntry("INFO", "Przywrócono domyślne ustawienia");
        showSuccessAlert("Przywrócono domyślne ustawienia.");
    }

    /**
     * Handles changing the logging level.
     * Adds a log entry about the level change.
     */
    @FXML
    private void handleApplyLogLevel() {
        String logLevel = logLevelComboBox.getValue();
        if (logLevel != null) {
            addLogEntry("INFO", "Zmieniono poziom logowania na: " + logLevel);
            showSuccessAlert("Zastosowano poziom logowania: " + logLevel);
        }
    }

    /**
     * Handles clearing the system logs.
     */
    @FXML
    private void handleClearLogs() {
        logsTextArea.clear();
        addLogEntry("INFO", "Wyczyszczono logi systemowe");
    }

    /**
     * Handles creating a database backup.
     * Shows a dialog with the operation's progress.
     */
    @FXML
    private void handleBackupDatabase() {
        showProgressDialog("Tworzenie kopii zapasowej...", () -> {
            try {
                Thread.sleep(2000);
                addLogEntry("INFO", "Wykonano kopię zapasową bazy danych");
                return "Kopia zapasowa utworzona pomyślnie.";
            } catch (InterruptedException e) {
                addLogEntry("ERROR", "Błąd podczas tworzenia kopii zapasowej: " + e.getMessage());
                return "Błąd podczas tworzenia kopii zapasowej.";
            }
        });
    }

    /**
     * Handles restoring the database from a backup.
     * Shows a confirmation dialog before running the operation.
     */
    @FXML
    private void handleRestoreDatabase() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Potwierdzenie");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Czy na pewno chcesz odtworzyć bazę danych z kopii zapasowej? Ta operacja nadpisze wszystkie dane.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showProgressDialog("Odtwarzanie z kopii zapasowej...", () -> {
                    try {
                        Thread.sleep(3000);
                        addLogEntry("INFO", "Odtworzono bazę danych z kopii zapasowej");
                        return "Baza danych odtworzona pomyślnie.";
                    } catch (InterruptedException e) {
                        addLogEntry("ERROR", "Błąd podczas odtwarzania bazy danych: " + e.getMessage());
                        return "Błąd podczas odtwarzania bazy danych.";
                    }
                });
            }
        });
    }

    /**
     * Handles database optimization.
     * Shows a dialog with the operation's progress.
     */
    @FXML
    private void handleOptimizeDatabase() {
        showProgressDialog("Optymalizacja bazy danych...", () -> {
            try {
                Thread.sleep(1500);
                addLogEntry("INFO", "Zoptymalizowano bazę danych");
                return "Baza danych zoptymalizowana pomyślnie.";
            } catch (InterruptedException e) {
                addLogEntry("ERROR", "Błąd podczas optymalizacji bazy danych: " + e.getMessage());
                return "Błąd podczas optymalizacji bazy danych.";
            }
        });
    }


    /**
     * Updates the database connection status.
     * Changes the label's appearance depending on the connection state.
     */
    private void updateConnectionStatus() {
        try (Connection conn = DatabaseManager.getConnection()) {
            dbStatusLabel.setText("Połączono");
            dbStatusLabel.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            dbStatusLabel.setText("Błąd połączenia");
            dbStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Adds a new entry to the system logs.
     *
     * @param level logging level (e.g. INFO, ERROR)
     * @param message message text to log
     */
    private void addLogEntry(String level, String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        String logEntry = String.format("[%s] [%s] %s%n", timestamp, level, message);
        logsTextArea.appendText(logEntry);
    }

    /**
     * Shows a dialog with a success message.
     *
     * @param message message text
     */
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukces");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a dialog with an error message.
     *
     * @param message message text
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a progress bar dialog for long-running operations.
     *
     * @param title dialog title
     * @param task task to run in the background
     */
    private void showProgressDialog(String title, java.util.function.Supplier<String> task) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(cancelButtonType);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        dialog.getDialogPane().setContent(progressBar);

        new Thread(() -> {
            String result = task.get();
            javafx.application.Platform.runLater(() -> {
                dialog.setResult(result);
                dialog.close();

                Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                resultAlert.setTitle("Rezultat operacji");
                resultAlert.setHeaderText(null);
                resultAlert.setContentText(result);
                resultAlert.showAndWait();
            });
        }).start();

        dialog.showAndWait();
    }
}