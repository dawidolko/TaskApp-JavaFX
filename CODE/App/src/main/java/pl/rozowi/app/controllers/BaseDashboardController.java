package pl.rozowi.app.controllers;

import javafx.scene.Scene;
import pl.rozowi.app.models.User;
import pl.rozowi.app.util.ThemeManager;

/**
 * Base controller for all dashboards.
 * Contains shared functions such as user and theme management.
 */
public abstract class BaseDashboardController {

    protected User currentUser;

    /**
     * Sets the controller's user and applies their theme.
     * @param user user object
     */
    public void setUser(User user) {
        this.currentUser = user;

        applyUserTheme();

        onUserSet(user);
    }

    /**
     * Called after the user is set.
     * @param user user object
     */
    protected abstract void onUserSet(User user);

    /**
     * Applies the user's theme to the current scene.
     */
    protected void applyUserTheme() {
        if (currentUser != null && getScene() != null) {
            ThemeManager.applyTheme(getScene(), currentUser);
        }
    }

    /**
     * Returns the controller's current scene.
     * @return the scene object, or null if the scene is not available yet
     */
    protected abstract Scene getScene();
}