package pl.rozowi.app.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableRow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.rozowi.app.MainApplication;
import pl.rozowi.app.models.User;
import pl.rozowi.app.util.ThemeManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Main panel controller for a regular system user.
 * Extends BaseDashboardController, tailoring it to a standard user's needs.
 *
 * <p>Provides the following functionality:</p>
 * <ul>
 *   <li>Switching between task views (own and all)</li>
 *   <li>Access to user settings</li>
 *   <li>Logging out of the system</li>
 *   <li>Basic task search</li>
 * </ul>
 */
public class UserDashboardController extends BaseDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private ImageView logoImageView;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;

    @FXML
    private Button myTasksButton;
    @FXML
    private Button allTasksButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;

    private static final String ACTIVE_BUTTON_STYLE = "sidebar-button-active";

    /**
     * Initializes the controller.
     * Called automatically after the FXML file is loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the active button style in the sidebar.
     *
     * @param activeButton button to mark as active
     */
    private void setActiveButton(Button activeButton) {
        myTasksButton.getStyleClass().remove(ACTIVE_BUTTON_STYLE);
        allTasksButton.getStyleClass().remove(ACTIVE_BUTTON_STYLE);
        settingsButton.getStyleClass().remove(ACTIVE_BUTTON_STYLE);

        if (activeButton != null) {
            activeButton.getStyleClass().add(ACTIVE_BUTTON_STYLE);
        }
    }

    /**
     * Called after the user is set.
     * Initializes the panel based on the user's default view.
     *
     * @param user object of the user logged in to the system
     */
    @Override
    protected void onUserSet(User user) {
        welcomeLabel.setText("Witaj, " + user.getName());

        try {
            String def = user.getDefaultView();
            if (def != null) {
                switch (def) {
                    case "Moje zadania":
                        goToMyTasks();
                        break;
                    case "Zadania":
                        goToAllTasks();
                        break;
                    case "Ustawienia":
                        goToSettings();
                        break;
                    default:
                        goToMyTasks();
                        break;
                }
            } else {
                goToMyTasks();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the panel's main scene.
     *
     * @return the scene object, or null if the main panel has not been loaded
     */
    @Override
    protected Scene getScene() {
        return mainPane != null ? mainPane.getScene() : null;
    }

    /**
     * Goes to the view of the user's own tasks.
     *
     * @throws IOException if loading the view fails
     */
    @FXML
    private void goToMyTasks() throws IOException {
        setActiveButton(myTasksButton);
        loadView("/fxml/user/myTasks.fxml");
    }

    /**
     * Goes to the view of all tasks available to the user.
     *
     * @throws IOException if loading the view fails
     */
    @FXML
    private void goToAllTasks() throws IOException {
        setActiveButton(allTasksButton);
        loadView("/fxml/user/tasks.fxml");
    }

    /**
     * Goes to the user settings view.
     *
     * @throws IOException if loading the view fails
     */
    @FXML
    private void goToSettings() throws IOException {
        setActiveButton(settingsButton);
        loadView("/fxml/user/settings.fxml");
    }

    /**
     * Logs the user out and redirects to the login screen.
     *
     * @throws IOException if loading the login view fails
     */
    @FXML
    private void logout() throws IOException {
        MainApplication.setCurrentUser(null);
        MainApplication.switchScene("/fxml/login.fxml", "TaskApp - Logowanie");
    }

    /**
     * Loads a view into the main panel.
     *
     * @param fxmlPath path to the FXML file defining the view
     * @throws IOException if loading the view fails
     */
    private void loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent view = loader.load();

        Object controller = loader.getController();
        if (controller instanceof SettingsController) {
            ((SettingsController) controller).setUser(currentUser);
        } else if (controller instanceof UserAwareController) {
            ((UserAwareController) controller).setUser(currentUser);
        }

        mainPane.getChildren().setAll(view);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);

        Platform.runLater(() -> {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.sizeToScene();
        });
    }
}