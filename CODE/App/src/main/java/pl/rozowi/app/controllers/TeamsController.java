package pl.rozowi.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller that manages displaying and searching teams in the system.
 * Allows browsing the team list with basic information and searching teams by name.
 */
public class TeamsController {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<?> teamsTable;
    @FXML
    private TableColumn<?, ?> colTeamId;
    @FXML
    private TableColumn<?, ?> colTeamName;
    @FXML
    private TableColumn<?, ?> colMembersCount;

    /**
     * Handles searching teams.
     * Reads the text from the search field and prints it to the console (in the current
     * version). In the future it should filter the team list in the table.
     */
    @FXML
    private void handleSearch() {
        String filter = searchField.getText();
        System.out.println("Szukaj zespołu: " + filter);
    }
}
