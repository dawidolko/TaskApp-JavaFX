package pl.rozowi.app.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.rozowi.app.MainApplication;
import pl.rozowi.app.controllers.TaskEditController;
import pl.rozowi.app.models.Task;

import java.io.IOException;

/**
 * Displays and manages the task edit dialog.
 * The window is modal and blocks interaction with other parts of the
 * application until it is closed.
 */
public class TaskEditDialog {

    /**
     * Shows a modal dialog for editing a task.
     * Loads the view from an FXML file, sets the controller and passes it the task
     * to edit.
     * The window is shown in modal mode, blocking interaction with the rest of the
     * application.
     *
     * @param task task to edit, passed to the controller. If null, a new task
     *         will be created.
     * @return true if the window was shown and closed correctly,
     *         false if loading the FXML view failed
     * @throws IOException if loading the FXML file fails
     * @see TaskEditController
     * @see Task
     */
    public static boolean showEditDialog(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/fxml/taskEdit.fxml"));
            Parent root = loader.load();

            TaskEditController controller = loader.getController();
            controller.setTask(task);

            Stage stage = new Stage();
            stage.setTitle("Edit Task");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}