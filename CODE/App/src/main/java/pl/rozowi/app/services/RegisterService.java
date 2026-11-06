package pl.rozowi.app.services;

import pl.rozowi.app.dao.UserDAO;
import pl.rozowi.app.models.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class that handles registering new users in the system.
 * Provides input validation, creation of new user accounts and returning the results
 * of the registration operation.
 */
public class RegisterService {

    private final UserDAO userDAO;

    /**
     * Constructor initializing the registration service.
     *
     * @param userDAO DAO object for communicating with the user data layer
     */
    public RegisterService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new user in the system after validating the data.
     *
     * @param firstName user's first name
     * @param lastName user's last name
     * @param email user's email address
     * @param password user's password
     * @param confirmPassword password confirmation
     * @return RegistrationResult object containing the operation's status and message
     */
    public RegistrationResult register(String firstName, String lastName, String email,
                                       String password, String confirmPassword) {
        if (!isCapitalized(firstName)) {
            return RegistrationResult.fail("Imię musi zaczynać się od wielkiej litery!");
        }
        if (!isCapitalized(lastName)) {
            return RegistrationResult.fail("Nazwisko musi zaczynać się od wielkiej litery!");
        }
        if (!isValidEmail(email)) {
            return RegistrationResult.fail("Email musi zawierać znak '@' z co najmniej dwoma znakami przed i po nim!");
        }
        if (!hasSpecialChar(password)) {
            return RegistrationResult.fail("Hasło musi zawierać przynajmniej jeden znak specjalny!");
        }
        if (!password.equals(confirmPassword)) {
            return RegistrationResult.fail("Hasła nie są takie same!");
        }

        User newUser = new User();
        newUser.setName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(hashPassword(password));
        newUser.setRoleId(3);
        newUser.setGroupId(1);
        newUser.setPasswordHint("");

        boolean inserted = userDAO.insertUser(newUser);
        if (!inserted) {
            return RegistrationResult.fail("Rejestracja nie powiodła się! Sprawdź, czy email nie jest już zajęty.");
        }

        return RegistrationResult.success("Rejestracja udana!");
    }

    /**
     * Checks whether the text starts with an uppercase letter.
     *
     * @param text text to check
     * @return true if the text starts with an uppercase letter, false otherwise
     */
    private boolean isCapitalized(String text) {
        if (text == null || text.isEmpty()) return false;
        return Character.isUpperCase(text.charAt(0));
    }

    /**
     * Validates the email address format.
     *
     * @param email email address to validate
     * @return true if the email is in a valid format, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String regex = "^.{2,}@.{2,}$";
        return email.matches(regex);
    }

    /**
     * Checks whether the password contains at least one special character.
     *
     * @param password password to check
     * @return true if the password contains a special character, false otherwise
     */
    private boolean hasSpecialChar(String password) {
        if (password == null) return false;
        return password.matches(".*[^A-Za-z0-9].*");
    }

    /**
     * Hashes the given password with SHA-256.
     *
     * @param password password to hash
     * @return the hashed password as a hex string, or null on error
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
}