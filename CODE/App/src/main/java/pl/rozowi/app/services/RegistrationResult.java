package pl.rozowi.app.services;

/**
 * Class representing the result of a user registration operation.
 * Holds whether the operation succeeded and an optional message.
 */
public class RegistrationResult {

    private final boolean success;
    private final String message;

    /**
     * Private constructor creating a registration result object.
     *
     * @param success true if registration succeeded, false otherwise
     * @param message message related to the operation result
     */
    private RegistrationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Creates a RegistrationResult for a successful registration.
     *
     * @param msg success message
     * @return RegistrationResult with the success flag set
     */
    public static RegistrationResult success(String msg) {
        return new RegistrationResult(true, msg);
    }

    /**
     * Creates a RegistrationResult for a failed registration.
     *
     * @param msg error message
     * @return RegistrationResult with the failure flag set
     */
    public static RegistrationResult fail(String msg) {
        return new RegistrationResult(false, msg);
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the message related to the registration result.
     *
     * @return success or error message
     */
    public String getMessage() {
        return message;
    }
}