package pl.rozowi.app.controllers;

import pl.rozowi.app.models.User;

/**
 * Interface for controllers that need access to the user object.
 */
public interface UserAwareController {

    /**
     * Sets the user object in the controller.
     * @param user user object
     */
    void setUser(User user);
}