package pl.rozowi.app.services;

import pl.rozowi.app.dao.TaskActivityDAO;
import pl.rozowi.app.models.TaskActivity;
import pl.rozowi.app.util.Session;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Class that logs activity in the system.
 * Provides methods for recording various types of events related to tasks, users,
 * teams and other system actions.
 */
public class ActivityService {

    private static final TaskActivityDAO activityDAO = new TaskActivityDAO();

    /**
     * Logs the creation of a new task.
     *
     * @param taskId ID of the created task
     * @param taskTitle task title
     * @param assignedUserId ID of the user the task is assigned to
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logTaskCreation(int taskId, String taskTitle, int assignedUserId) {
        TaskActivity activity = createActivity(taskId, "CREATE");
        activity.setDescription("Utworzono zadanie \"" + taskTitle + "\" i przypisano do użytkownika ID: " + assignedUserId);
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs a task status change.
     *
     * @param taskId task ID
     * @param taskTitle task title
     * @param oldStatus previous status
     * @param newStatus new status
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logStatusChange(int taskId, String taskTitle, String oldStatus, String newStatus) {
        TaskActivity activity = createActivity(taskId, "STATUS");
        activity.setDescription("Zmieniono status zadania \"" + taskTitle + "\" z \"" + oldStatus + "\" na \"" + newStatus + "\"");
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs a change of a task's user assignment.
     *
     * @param taskId task ID
     * @param taskTitle task title
     * @param oldUserId ID of the previous user (0 if none)
     * @param newUserId ID of the new user
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logAssignment(int taskId, String taskTitle, int oldUserId, int newUserId) {
        TaskActivity activity = createActivity(taskId, "ASSIGN");
        String desc = oldUserId > 0
                ? "Zmieniono przypisanie zadania \"" + taskTitle + "\" z użytkownika ID: " + oldUserId + " na użytkownika ID: " + newUserId
                : "Przypisano zadanie \"" + taskTitle + "\" do użytkownika ID: " + newUserId;
        activity.setDescription(desc);
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs an update of a task field.
     *
     * @param taskId task ID
     * @param taskTitle task title
     * @param fieldName name of the updated field
     * @param oldValue previous value
     * @param newValue new value
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logTaskUpdate(int taskId, String taskTitle, String fieldName, String oldValue, String newValue) {
        TaskActivity activity = createActivity(taskId, "UPDATE");
        activity.setDescription("Zaktualizowano pole \"" + fieldName + "\" zadania \"" + taskTitle + "\" z \"" + oldValue + "\" na \"" + newValue + "\"");
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs the addition of a comment to a task.
     *
     * @param taskId task ID
     * @param taskTitle task title
     * @param comment comment text
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logTaskComment(int taskId, String taskTitle, String comment) {
        TaskActivity activity = createActivity(taskId, "COMMENT");
        activity.setDescription("Dodano komentarz do zadania \"" + taskTitle + "\": " + comment);
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs a custom task-related activity.
     *
     * @param taskId task ID
     * @param activityType activity type
     * @param description activity description
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logCustomActivity(int taskId, String activityType, String description) {
        TaskActivity activity = createActivity(taskId, activityType);
        activity.setDescription(description);
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Creates an activity object with basic data.
     *
     * @param taskId task ID
     * @param activityType activity type
     * @return TaskActivity object with the basic data filled in
     */
    private static TaskActivity createActivity(int taskId, String activityType) {
        TaskActivity activity = new TaskActivity();
        activity.setTaskId(taskId);
        activity.setUserId(Session.currentUserId);
        activity.setActivityType(activityType);
        activity.setActivityDate(Timestamp.from(Instant.now()));
        return activity;
    }

    /**
     * Logs a user password change.
     *
     * @param userId user ID
     * @param changedByAdmin true if the password was changed by an administrator
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logPasswordChange(int userId, boolean changedByAdmin) {
        TaskActivity activity = createActivity(0, "PASSWORD");
        String description;

        if (changedByAdmin) {
            description = "Administrator zresetował hasło dla użytkownika ID: " + userId;
        } else {
            description = "Użytkownik ID: " + userId + " zmienił swoje hasło";
        }

        activity.setUserId(userId);
        activity.setDescription(description);
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs a custom system activity.
     *
     * @param userId ID of the user performing the action
     * @param activityType activity type
     * @param description activity description
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logSystemActivity(int userId, String activityType, String description) {
        TaskActivity activity = createActivity(0, activityType);
        activity.setUserId(userId);
        activity.setDescription(description);
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs a user signing in to the system.
     *
     * @param userId user ID
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logLogin(int userId) {
        TaskActivity activity = createActivity(0, "LOGIN");
        activity.setUserId(userId);
        activity.setDescription("Użytkownik ID: " + userId + " zalogował się do systemu");
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs a user signing out of the system.
     *
     * @param userId user ID
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logLogout(int userId) {
        TaskActivity activity = createActivity(0, "LOGOUT");
        activity.setUserId(userId);
        activity.setDescription("Użytkownik ID: " + userId + " wylogował się z systemu");
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs a user management action performed by an administrator.
     *
     * @param adminId ID of the administrator performing the action
     * @param targetUserId ID of the user the action was performed on
     * @param action action type (e.g. "create", "update", "delete")
     * @param details action details
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logUserManagement(int adminId, int targetUserId, String action, String details) {
        TaskActivity activity = createActivity(0, "USER_MANAGEMENT");
        activity.setUserId(adminId);
        activity.setDescription("Administrator ID: " + adminId + " wykonał akcję '" + action +
                "' na użytkowniku ID: " + targetUserId + ". " + details);
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs a team management action.
     *
     * @param userId ID of the user performing the action
     * @param teamId team ID
     * @param action action type (e.g. "create", "update", "delete")
     * @param details action details
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logTeamManagement(int userId, int teamId, String action, String details) {
        TaskActivity activity = createActivity(0, "TEAM_MANAGEMENT");
        activity.setUserId(userId);
        activity.setDescription("Użytkownik ID: " + userId + " wykonał akcję '" + action +
                "' na zespole ID: " + teamId + ". " + details);
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs report generation.
     *
     * @param userId ID of the user generating the report
     * @param reportType report type
     * @param details report details
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logReportGeneration(int userId, String reportType, String details) {
        TaskActivity activity = createActivity(0, "REPORT");
        activity.setUserId(userId);
        activity.setDescription("Użytkownik ID: " + userId + " wygenerował raport typu '" +
                reportType + "'. " + details);
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs a system configuration change.
     *
     * @param adminId ID of the administrator making the change
     * @param setting setting name
     * @param oldValue previous value
     * @param newValue new value
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logConfigChange(int adminId, String setting, String oldValue, String newValue) {
        TaskActivity activity = createActivity(0, "CONFIG");
        activity.setUserId(adminId);
        activity.setDescription("Administrator ID: " + adminId + " zmienił ustawienie '" +
                setting + "' z '" + oldValue + "' na '" + newValue + "'");
        return activityDAO.insertTaskActivity(activity);
    }

    /**
     * Logs a system error.
     *
     * @param userId ID of the user related to the error (0 if system)
     * @param errorType error type
     * @param message error message
     * @param stackTrace error stack trace (may be null)
     * @return true if the logging operation succeeded, false otherwise
     */
    public static boolean logSystemError(int userId, String errorType, String message, String stackTrace) {
        TaskActivity activity = createActivity(0, "ERROR");
        activity.setUserId(userId);
        activity.setDescription("Błąd typu '" + errorType + "': " + message +
                (stackTrace != null && !stackTrace.isEmpty() ? "\nStack trace: " + stackTrace : ""));
        return activityDAO.insertTaskActivity(activity);
    }
}