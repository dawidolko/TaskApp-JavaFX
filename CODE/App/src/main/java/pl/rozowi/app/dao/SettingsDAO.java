package pl.rozowi.app.dao;

import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.Settings;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) for the settings table.
 * Provides database operations related to user settings, including:
 * <ul>
 *   <li>Fetching user settings</li>
 *   <li>Updating and inserting settings</li>
 *   <li>Managing password change timestamps</li>
 *   <li>Handling theme and view preferences</li>
 * </ul>
 */
public class SettingsDAO {

    private static final Logger LOGGER = Logger.getLogger(SettingsDAO.class.getName());

    /**
     * Gets the settings for the given user from the database.
     *
     * @param userId ID of the user whose settings are to be fetched
     * @return Settings object with all the user's preferences if found,
     *         null if no settings exist for the user
     */
    public Settings getSettingsByUserId(int userId) {
        String sql = "SELECT id, user_id, theme, default_view, last_password_change FROM settings WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Settings settings = new Settings();
                    settings.setId(rs.getInt("id"));
                    settings.setUserId(rs.getInt("user_id"));
                    settings.setTheme(rs.getString("theme"));
                    settings.setDefaultView(rs.getString("default_view"));
                    settings.setLastPasswordChange(rs.getTimestamp("last_password_change"));
                    return settings;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving settings for user ID: " + userId, ex);
        }

        return null;
    }

    /**
     * Updates the user's last password change timestamp.
     * Creates a new settings record if none exists for the user.
     *
     * @param userId ID of the user whose timestamp should be updated
     * @return true if the operation succeeded, false on error
     */
    public boolean updateLastPasswordChange(int userId) {
        Settings existingSettings = getSettingsByUserId(userId);

        if (existingSettings != null) {
            String sql = "UPDATE settings SET last_password_change = CURRENT_TIMESTAMP WHERE user_id = ?";

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                int affected = stmt.executeUpdate();
                return affected > 0;
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error updating last password change for user ID: " + userId, ex);
                return false;
            }
        } else {
            String sql = "INSERT INTO settings (user_id, last_password_change) VALUES (?, CURRENT_TIMESTAMP)";

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                int affected = stmt.executeUpdate();
                return affected > 0;
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error creating settings with last password change for user ID: " + userId, ex);
                return false;
            }
        }
    }

    /**
     * Inserts a new settings record for the user with all available preferences.
     * Handles null values for optional fields correctly.
     *
     * @param settings Settings object with all the user's preferences to insert
     * @return true if the insert succeeded, false on error
     */
    public boolean insertSettings(Settings settings) {
        String sql = "INSERT INTO settings (user_id, theme, default_view, last_password_change) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, settings.getUserId());

            if (settings.getTheme() != null) {
                stmt.setString(2, settings.getTheme());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }

            if (settings.getDefaultView() != null) {
                stmt.setString(3, settings.getDefaultView());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            if (settings.getLastPasswordChange() != null) {
                stmt.setTimestamp(4, settings.getLastPasswordChange());
            } else {
                stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            }

            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error inserting settings for user ID: " + settings.getUserId(), ex);
            return false;
        }
    }

    /**
     * Updates the user's existing settings.
     *
     * @param settings Settings object with the updated values
     * @return true if the update succeeded, false on error
     */
    public boolean updateSettings(Settings settings) {
        String sql = "UPDATE settings SET theme = ?, default_view = ? WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (settings.getTheme() != null) {
                stmt.setString(1, settings.getTheme());
            } else {
                stmt.setNull(1, Types.VARCHAR);
            }

            if (settings.getDefaultView() != null) {
                stmt.setString(2, settings.getDefaultView());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }

            stmt.setInt(3, settings.getUserId());

            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating settings for user ID: " + settings.getUserId(), ex);
            return false;
        }
    }

    /**
     * Deletes the settings of the given user.
     *
     * @param userId ID of the user whose settings are to be deleted
     * @return true if the deletion succeeded, false on error
     */
    public boolean deleteSettings(int userId) {
        String sql = "DELETE FROM settings WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting settings for user ID: " + userId, ex);
            return false;
        }
    }

    /**
     * Updates the user's default view.
     *
     * @param userId user ID
     * @param defaultView name of the default view to set
     * @return true if the update succeeded, false on error
     */
    public boolean updateDefaultView(int userId, String defaultView) {
        String sql = "UPDATE settings SET default_view = ? WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, defaultView);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException ex) {
            Logger.getLogger(SettingsDAO.class.getName())
                    .log(Level.SEVERE, "Błąd aktualizacji widoku domyślnego", ex);
            return false;
        }
    }
}