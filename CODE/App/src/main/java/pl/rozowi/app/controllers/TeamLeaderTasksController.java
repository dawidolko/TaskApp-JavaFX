package pl.rozowi.app.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import pl.rozowi.app.dao.TaskDAO;
import pl.rozowi.app.models.Task;
import pl.rozowi.app.util.Session;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller through which a leader manages team tasks.
 * Allows displaying, filtering, adding, deleting and viewing task details.
 */
public class TeamLeaderTasksController {

    @FXML
    private TableView<Task> tasksTable;
    @FXML
    private TableColumn<Task, Number> colId;
    @FXML
    private TableColumn<Task, String> colTitle;
    @FXML
    private TableColumn<Task, String> colDescription;
    @FXML
    private TableColumn<Task, String> colStatus;
    @FXML
    private TableColumn<Task, String> colPriority;
    @FXML
    private TableColumn<Task, String> colStart;
    @FXML
    private TableColumn<Task, String> colEnd;
    @FXML
    private TableColumn<Task, String> colTeam;
    @FXML
    private TableColumn<Task, String> colAssignedTo;
    @FXML
    private TextField filterField;

    private final TaskDAO taskDAO = new TaskDAO();
    private ObservableList<Task> allTasks = FXCollections.observableArrayList();

    /**
     * Initializes the controller.
     * Configures the task table, sets the row factory and the listener for the filter
     * field.
     */
    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colTitle.setCellValueFactory(c -> c.getValue().titleProperty());
        colStatus.setCellValueFactory(c -> c.getValue().statusProperty());
        colPriority.setCellValueFactory(c -> c.getValue().priorityProperty());
        colEnd.setCellValueFactory(c -> c.getValue().endDateProperty());
        colTeam.setCellValueFactory(c -> c.getValue().teamNameProperty());
        colAssignedTo.setCellValueFactory(c -> c.getValue().assignedEmailProperty());

        tasksTable.setItems(allTasks);
        tasksTable.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2 && !row.isEmpty()) {
                    openDetails(row.getItem());
                }
            });
            return row;
        });

        filterField.textProperty().addListener((obs, o, n) -> applyFilter(n));
        loadTasks();
    }

    /**
     * Loads the tasks assigned to the teams managed by the current leader.
     */
    @FXML
    private void loadTasks() {
        List<Task> list = taskDAO.getTasksForLeader(Session.currentUserId);
        allTasks.setAll(list);
    }

    /**
     * Filters the tasks in the table by the entered text.
     * @param text filter text (searched in the title, status and assigned user)
     */
    private void applyFilter(String text) {
        if (text == null || text.isBlank()) {
            tasksTable.setItems(allTasks);
            return;
        }
        String lower = text.toLowerCase();
        ObservableList<Task> filtered = allTasks.filtered(t ->
                t.getTitle().toLowerCase().contains(lower) ||
                        t.getStatus().toLowerCase().contains(lower) ||
                        t.getAssignedEmail().toLowerCase().contains(lower)
        );
        tasksTable.setItems(filtered);
    }

    /**
     * Opens a dialog with the details of the selected task.
     * @param task task whose details are to be displayed
     */
    private void openDetails(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/teamleader/taskDetails.fxml"));
            Parent root = loader.load();
            TaskDetailsController ctrl = loader.getController();
            ctrl.setTask(task);
            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setScene(new Scene(root));
            st.setTitle("Szczegóły zadania");
            st.showAndWait();
            loadTasks();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handles deleting the selected task.
     * Shows a confirmation dialog before deletion.
     */
    @FXML
    private void handleDeleteTask() {
        Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "Wybierz zadanie", "Wybierz zadanie do usunięcia");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Potwierdzenie usunięcia");
        confirmDialog.setHeaderText("Czy na pewno chcesz usunąć zadanie?");
        confirmDialog.setContentText("Zadanie: " + selectedTask.getTitle() + "\n\nTa operacja jest nieodwracalna i usunie również wszystkie przypisania i aktywności zadania.");

        ButtonType anulujButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmDialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, anulujButtonType);

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = taskDAO.deleteTask(selectedTask.getId());

                if (deleted) {
                    loadTasks();
                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zadanie zostało pomyślnie usunięte z systemu");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się usunąć zadania z bazy danych");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd usuwania", "Wystąpił błąd podczas usuwania zadania: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Shows a message dialog.
     * @param type message type (ERROR, WARNING, INFORMATION, CONFIRMATION)
     * @param title dialog title
     * @param message message text
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (type == Alert.AlertType.CONFIRMATION) {
            ButtonType anulujButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getDialogPane().getButtonTypes().setAll(ButtonType.OK, anulujButtonType);
        }

        alert.showAndWait();
    }

    /**
     * Opens the dialog for creating a new task.
     * Refreshes the task list after the dialog closes.
     */
    @FXML
    private void handleAddTask() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/teamleader/taskCreate.fxml"));
            Parent root = loader.load();
            TaskCreateController ctrl = loader.getController();

            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setScene(new Scene(root));
            st.setTitle("Nowe zadanie");
            st.showAndWait();

            loadTasks();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}