package pl.rozowi.app.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class that handles changing a user's password.
 * Provides validation of the new password and its hashing.
 */
public class PasswordChangeService {

    /**
     * Validates and hashes the user's new password.
     *
     * @param newPassword new password
     * @param confirmPassword confirmation of the new password
     * @return the hashed password as a hex string
     * @throws IllegalArgumentException if the passwords are empty, do not match or do
     *         not meet the requirements
     */
    public String validateAndHashPassword(String newPassword, String confirmPassword) throws IllegalArgumentException {
        if (newPassword == null || confirmPassword == null ||
                newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Pole hasła nie może być puste!");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Hasła nie są takie same!");
        }

        return hashPassword(newPassword);
    }

    /**
     * Hashes the given password with SHA-256.
     *
     * @param password password to hash
     * @return the hashed password as a hex string
     * @throws RuntimeException if the hashing algorithm is not available
     */
    public String hashPassword(String password) {
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
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }
}