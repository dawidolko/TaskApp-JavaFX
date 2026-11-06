package pl.rozowi.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.rozowi.app.MainApplication;
import pl.rozowi.app.dao.TaskDAO;
import pl.rozowi.app.dao.TeamMemberDAO;
import pl.rozowi.app.dao.UserDAO;
import pl.rozowi.app.dao.ProjectDAO;
import pl.rozowi.app.dao.TeamDAO;
import pl.rozowi.app.models.Task;
import pl.rozowi.app.models.User;
import pl.rozowi.app.util.TaskEditDialog;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller that displays and edits task details.
 * Allows browsing task information and modifying its status and assignment.
 */
public class TaskDetailsController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label projectIdLabel;
    @FXML
    private Label teamIdLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label endDateLabel;

    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private ComboBox<User> assigneeComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button editButton; 

    private Task task;
    private final TaskDAO taskDAO = new TaskDAO();
    private final TeamMemberDAO teamMemberDAO = new TeamMemberDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final TeamDAO teamDAO = new TeamDAO();


    /**
     * Sets the task to display and initializes the details view.
     * @param task task object to display
     */
    public void setTask(Task task) {
        this.task = task;
        displayTaskDetails();
    }

    /**
     * Shows the task details in the UI controls.
     * Loads the project and team names and sets the available status and assignment
     * options. Adjusts the visibility and availability of controls depending on the
     * user's role.
     */
    private void displayTaskDetails() {
        titleLabel.setText(task.getTitle());
        descriptionLabel.setText(task.getDescription());
        startDateLabel.setText(task.getStartDate());
        endDateLabel.setText(task.getEndDate());

        String projectName = projectDAO.getProjectNameById(task.getProjectId());
        String teamName = teamDAO.getTeamNameById(task.getTeamId());

        projectIdLabel.setText(projectName);
        teamIdLabel.setText(teamName);


        statusComboBox.getItems().setAll("Nowe", "W toku", "Zakończone");
        statusComboBox.setValue(task.getStatus());

        User current = MainApplication.getCurrentUser();
        boolean isLeader = current.getRoleId() == 3;
        boolean isEmployee = current.getRoleId() == 4;
        boolean isManager = current.getRoleId() == 2;
        boolean isAdmin = current.getRoleId() == 1;

        statusComboBox.setDisable(!(isLeader || isEmployee || isManager || isAdmin));

        if (editButton != null) {
            editButton.setVisible(isAdmin || isManager);
        }

        if (assigneeComboBox != null) {
            try {
                List<User> members = teamMemberDAO.getTeamMembers(task.getTeamId());
                assigneeComboBox.getItems().setAll(members);

                int assignedUserId = task.getAssignedTo();
                if (assignedUserId > 0) {
                    for (User u : members) {
                        if (u.getId() == assignedUserId) {
                            assigneeComboBox.setValue(u);
                            break;
                        }
                    }
                }

                assigneeComboBox.setDisable(!(isLeader || isManager || isAdmin));

                assigneeComboBox.setButtonCell(new ListCell<User>() {
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName() + " " + item.getLastName() + " (" + item.getEmail() + ")");
                        }
                    }
                });

                assigneeComboBox.setCellFactory(param -> new ListCell<User>() {
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName() + " " + item.getLastName() + " (" + item.getEmail() + ")");
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("Error loading team members: " + e.getMessage());
                e.printStackTrace();
            }
        }

        saveButton.setVisible(isLeader || isEmployee || isManager || isAdmin);
    }

    /**
     * Handles saving changes to the task.
     * Updates the task status and user assignment depending on permissions.
     */

    @FXML
    private void handleSave() {
        User current = MainApplication.getCurrentUser();
        int role = current.getRoleId();

        if (role != 3 && role != 4 && role != 2 && role != 1) {
            closeWindow();
            return;
        }

        boolean okStatus = true, okAssign = true;

        String newStatus = statusComboBox.getValue();
        if (newStatus != null && !newStatus.equals(task.getStatus())) {
            okStatus = taskDAO.updateTaskStatus(task.getId(), newStatus);
            if (okStatus) {
                task.setStatus(newStatus);
            }
        }

        if ((role == 3 || role == 2 || role == 1) && assigneeComboBox != null) {
            User newAssignee = assigneeComboBox.getValue();
            if (newAssignee != null &&
                (task.getAssignedTo() != newAssignee.getId() || task.getAssignedTo() == 0)) {
                okAssign = taskDAO.assignTask(task.getId(), newAssignee.getId());
                if (okAssign) {
                    task.setAssignedTo(newAssignee.getId());
                    task.setAssignedEmail(newAssignee.getEmail());
                }
            }
        }

        if (okStatus && okAssign) {
            showInfo("Changes saved successfully");
            closeWindow();
        } else {
            showError("Failed to save changes to the task.");
        }
    }

    /**
     * Handles editing all task properties.
     * Opens the task edit dialog for users with the appropriate permissions.
     */
    @FXML
    private void handleEdit() {
        User current = MainApplication.getCurrentUser();
        if (current.getRoleId() != 1 && current.getRoleId() != 2) {
            showInfo("You don't have permission to edit all task properties");
            return;
        }

        boolean success = TaskEditDialog.showEditDialog(task);

        if (success) {
            Task updatedTask = null;
            try {
                List<Task> tasks = null;
                if (task.getProjectId() > 0) {
                    tasks = taskDAO.getTasksByProjectId(task.getProjectId());
                } else if (task.getTeamId() > 0) {
                    tasks = taskDAO.getTasksByTeamId(task.getTeamId());
                }

                if (tasks != null) {
                    for (Task t : tasks) {
                        if (t.getId() == task.getId()) {
                            updatedTask = t;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error refreshing task: " + e.getMessage());
            }

            if (updatedTask != null) {
                this.task = updatedTask;
                displayTaskDetails();
            }

            closeWindow();
        }
    }

    /**
     * Handles cancelling changes and closing the window.
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows an information dialog.
     * @param message message text to display
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error dialog.
     * @param message error text to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

