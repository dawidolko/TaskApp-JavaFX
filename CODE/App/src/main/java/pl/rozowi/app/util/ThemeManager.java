package pl.rozowi.app.util;

import javafx.scene.Scene;
import pl.rozowi.app.dao.SettingsDAO;
import pl.rozowi.app.models.Settings;
import pl.rozowi.app.models.User;

/**
 * Manages application themes (skins).
 * Allows applying, changing and saving themes for users.
 * Supports two basic themes: Light (default) and Dark.
 */
public class ThemeManager {

    private static final String LIGHT_THEME_PATH = "/css/lightTheme.css";
    private static final String DARK_THEME_PATH = "/css/darkTheme.css";

    private static final String DEFAULT_THEME = "Light";

    /**
     * Applies the appropriate theme to the given scene based on user preferences.
     * If the user is null or has no theme set, applies the default theme.
     * Clears all existing styles before applying the new theme.
     *
     * @param scene scene to apply the theme to
     * @param user user whose theme preferences should be used (may be null)
     */
    public static void applyTheme(Scene scene, User user) {
        if (scene == null) {
            return;
        }

        scene.getStylesheets().clear();

        String themeName = (user != null && user.getTheme() != null) ? user.getTheme() : DEFAULT_THEME;

        if ("Dark".equalsIgnoreCase(themeName)) {
            scene.getStylesheets().add(ThemeManager.class.getResource(DARK_THEME_PATH).toExternalForm());
        } else {
            scene.getStylesheets().add(ThemeManager.class.getResource(LIGHT_THEME_PATH).toExternalForm());
        }
    }

    /**
     * Changes the theme of the given scene to the specified one.
     * Clears all existing styles before applying the new theme.
     *
     * @param scene scene to apply the theme to
     * @param themeName name of the theme to apply ("Dark", or anything else for
     *         the light theme)
     */
    public static void changeTheme(Scene scene, String themeName) {
        if (scene == null) {
            return;
        }
        scene.getStylesheets().clear();

        if ("Dark".equalsIgnoreCase(themeName)) {
            scene.getStylesheets().add(ThemeManager.class.getResource(DARK_THEME_PATH).toExternalForm());
        } else {
            scene.getStylesheets().add(ThemeManager.class.getResource(LIGHT_THEME_PATH).toExternalForm());
        }
    }

    /**
     * Saves the user's theme preference in the database.
     * If the user already has settings, updates them.
     * If not, creates a new record with the preferences.
     * Also updates the user object passed as a parameter.
     *
     * @param user user whose preferences should be saved
     * @param themeName name of the theme to save
     */
    public static void saveThemeToDatabase(User user, String themeName) {
        if (user == null) {
            return;
        }

        SettingsDAO settingsDAO = new SettingsDAO();
        Settings userSettings = settingsDAO.getSettingsByUserId(user.getId());

        if (userSettings != null) {
            userSettings.setTheme(themeName);
            settingsDAO.updateSettings(userSettings);
        } else {
            Settings newSettings = new Settings();
            newSettings.setUserId(user.getId());
            newSettings.setTheme(themeName);
            settingsDAO.insertSettings(newSettings);
        }

        user.setTheme(themeName);
    }
}