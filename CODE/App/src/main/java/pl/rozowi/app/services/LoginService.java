package pl.rozowi.app.services;

import pl.rozowi.app.dao.TeamMemberDAO;
import pl.rozowi.app.dao.UserDAO;
import pl.rozowi.app.models.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class that handles user login to the system.
 * Provides user authentication and session management.
 */
public class LoginService {

    private final UserDAO userDAO;
    private final TeamMemberDAO teamMemberDAO;

    /**
     * Constructor creating the login service.
     *
     * @param userDAO DAO for handling users
     * @param teamMemberDAO DAO for handling team members
     */
    public LoginService(UserDAO userDAO, TeamMemberDAO teamMemberDAO) {
        this.userDAO = userDAO;
        this.teamMemberDAO = teamMemberDAO;
    }

    /**
     * Authenticates a user by email address and password.
     *
     * @param email user's email address
     * @param rawPassword password in plain text
     * @return the User object on successful authentication, null otherwise
     */
    public User authenticate(String email, String rawPassword) {
        if (email == null || email.isEmpty() || rawPassword == null) {
            return null;
        }
        String hashed = hashPassword(rawPassword);

        User user = userDAO.getUserByEmail(email);
        if (user != null && user.getPassword() != null && user.getPassword().equals(hashed)) {
            return user;
        }
        return null;
    }

    /**
     * Gets the ID of the team assigned to the user.
     *
     * @param userId user ID
     * @return team ID, or a default value if the user does not belong to any team
     */
    public int findTeamIdForUser(int userId) {
        return teamMemberDAO.getTeamIdForUser(userId);
    }

    /**
     * Finds a user by email address.
     *
     * @param email user's email address
     * @return the User object if found, null otherwise
     */
    public User findUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        return userDAO.getUserByEmail(email);
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
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}