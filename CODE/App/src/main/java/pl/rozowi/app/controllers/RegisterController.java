package pl.rozowi.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import pl.rozowi.app.MainApplication;
import pl.rozowi.app.dao.UserDAO;
import pl.rozowi.app.models.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller that handles registering a new user in the system.
 * Provides input validation and secure creation of user accounts.
 */
public class RegisterController {

    @FXML
    private TextField firstNameField, lastNameField, emailField;
    @FXML
    private PasswordField passwordField, confirmPasswordField;
    @FXML
    private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    /**
     * Initializes the controller by setting up keyboard event handling.
     * Pressing Enter in any form field triggers a registration attempt.
     */
    public void initialize() {
        addEnterKeyHandlers();
    }

    /**
     * Handles the Enter key press event in the form fields.
     */
    private void addEnterKeyHandlers() {
        firstNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleRegister();
        });
        lastNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleRegister();
        });
        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleRegister();
        });
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleRegister();
        });
        confirmPasswordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleRegister();
        });
    }

    /**
     * Handles the new user registration process.
     * Validates the data and creates a new user account.
     */
    @FXML
    private void handleRegister() {
        errorLabel.setText("");

        if (!isCapitalized(firstNameField.getText())) {
            showError("Imię musi zaczynać się od wielkiej litery!");
            return;
        }
        if (!isCapitalized(lastNameField.getText())) {
            showError("Nazwisko musi zaczynać się od wielkiej litery!");
            return;
        }

        if (!isValidEmail(emailField.getText())) {
            showError("Email musi zawierać znak '@' z co najmniej dwoma znakami przed i po nim!");
            return;
        }

        if (!hasSpecialChar(passwordField.getText())) {
            showError("Hasło musi zawierać przynajmniej jeden znak specjalny!");
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Hasła nie są takie same!");
            return;
        }

        User newUser = new User();
        newUser.setName(firstNameField.getText());
        newUser.setLastName(lastNameField.getText());
        newUser.setEmail(emailField.getText());
        newUser.setPassword(hashPassword(passwordField.getText()));
        newUser.setRoleId(4);   
        newUser.setGroupId(1); 
        newUser.setPasswordHint("");

        boolean inserted = userDAO.insertUser(newUser);
        if (inserted) {
            showSuccessAlert("Rejestracja udana!", "Konto zostało utworzone pomyślnie.");
            try {
                MainApplication.switchScene("/fxml/login.fxml", "TaskApp - Panel logowania");
            } catch (IOException e) {
                e.printStackTrace();
                showError("Błąd podczas przechodzenia do panelu logowania.");
            }
        } else {
            showError("Rejestracja nie powiodła się! Sprawdź, czy email nie jest już zajęty.");
        }
    }

    /**
     * Redirects the user back to the login screen.
     * @throws IOException if loading the login view fails
     */
    @FXML
    private void goBack() throws IOException {
        MainApplication.switchScene("/fxml/login.fxml", "TaskApp - Panel logowania");
    }

    /**
     * Shows an error message in the user interface.
     * @param message error message text
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }

    /**
     * Shows a dialog with a success message.
     * @param title dialog title
     * @param message message text
     */
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hashes the given password with SHA-256.
     * @param password password to hash
     * @return the hashed password as a hex string
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks whether the text starts with an uppercase letter.
     * @param text text to check
     * @return true if the text starts with an uppercase letter, false otherwise
     */
    private boolean isCapitalized(String text) {
        if (text == null || text.isEmpty()) return false;
        return Character.isUpperCase(text.charAt(0));
    }

    /**
     * Validates the email address format.
     * @param email email address to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String regex = "^.{2,}@.{2,}$";
        return email.matches(regex);
    }

    /**
     * Checks whether the password contains at least one special character.
     * @param password password to check
     * @return true if the password contains a special character, false otherwise
     */
    private boolean hasSpecialChar(String password) {
        if (password == null) return false;
        Pattern pattern = Pattern.compile(".*[^A-Za-z0-9].*");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}