package pl.rozowi.app.util;

import pl.rozowi.app.dao.SettingsDAO;
import pl.rozowi.app.models.Settings;
import pl.rozowi.app.models.User;

/**
 * Manages the user's default view.
 * Allows saving default view preferences for a given user.
 */
public class DefaultViewManager {

    /**
     * Saves the default view for the given user.
     * If the user exists in the system, updates their settings.
     * If the user has no settings yet, creates a new record with the preferences.
     * Also updates the user object passed as a parameter.
     *
     * @param user user object for which the preferences are saved
     * @param defaultView name of the default view to save
     * @throws IllegalArgumentException if defaultView is null
     */
    public static void saveDefaultView(User user, String defaultView) {
        if (user == null) {
            return;
        }

        SettingsDAO settingsDAO = new SettingsDAO();
        Settings userSettings = settingsDAO.getSettingsByUserId(user.getId());

        if (userSettings != null) {
            userSettings.setDefaultView(defaultView);
            settingsDAO.updateSettings(userSettings);
        } else {
            Settings newSettings = new Settings();
            newSettings.setUserId(user.getId());
            newSettings.setDefaultView(defaultView);
            settingsDAO.insertSettings(newSettings);
        }

        user.setDefaultView(defaultView);
    }
}