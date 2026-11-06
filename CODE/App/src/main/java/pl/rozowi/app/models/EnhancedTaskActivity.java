package pl.rozowi.app.models;

import java.sql.Timestamp;

/**
 * Extended version of the TaskActivity class containing the additional information
 * needed to display activities efficiently in the user interface.
 */
public class EnhancedTaskActivity extends TaskActivity {

    private String taskTitle;
    private String userName;
    private String userLastName;
    private String userEmail;

    public EnhancedTaskActivity(TaskActivity baseActivity) {
        this.setId(baseActivity.getId());
        this.setTaskId(baseActivity.getTaskId());
        this.setUserId(baseActivity.getUserId());
        this.setActivityType(baseActivity.getActivityType());
        this.setDescription(baseActivity.getDescription());
        this.setActivityDate(baseActivity.getActivityDate());
    }

    public EnhancedTaskActivity() {
    }


    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Returns the user's full name.
     * @return String in the format "First Last"
     */
    public String getFullUserName() {
        if (userName != null && userLastName != null) {
            return userName + " " + userLastName;
        } else if (userName != null) {
            return userName;
        } else if (userLastName != null) {
            return userLastName;
        } else {
            return "Nieznany użytkownik (ID: " + getUserId() + ")";
        }
    }

    /**
     * Returns user information in a display-friendly form.
     * @return String in the format "First Last (email)"
     */
    public String getUserDisplayString() {
        String name = getFullUserName();
        if (userEmail != null && !userEmail.isEmpty()) {
            return name + " (" + userEmail + ")";
        } else {
            return name;
        }
    }

    /**
     * Returns the task title, or a message that there is no task.
     * @return task title, or a no-task message
     */
    public String getTaskTitleOrDefault() {
        if (getTaskId() == 0) {
            return "Zdarzenie systemowe";
        }

        if (taskTitle != null && !taskTitle.isEmpty()) {
            return taskTitle;
        } else {
            return "Zadanie ID: " + getTaskId();
        }
    }

    /**
     * Converts the activity type to a more user-friendly name.
     * @return activity type name in Polish
     */
    public String getActivityTypeDisplayName() {
        String type = getActivityType();
        if (type == null) return "Nieznany";

        switch (type) {
            case "CREATE":
                return "Utworzenie";
            case "UPDATE":
                return "Aktualizacja";
            case "STATUS":
                return "Zmiana statusu";
            case "ASSIGN":
                return "Przypisanie";
            case "COMMENT":
                return "Komentarz";
            case "PASSWORD":
                return "Zmiana hasła";
            case "LOGIN":
                return "Logowanie";
            case "LOGOUT":
                return "Wylogowanie";
            case "USER_MANAGEMENT":
                return "Zarządzanie użytkownikami";
            case "TEAM_MANAGEMENT":
                return "Zarządzanie zespołami";
            default:
                return type;
        }
    }

    /**
     * Formats the activity date for display.
     * @return the formatted date and time
     */
    public String getFormattedDate() {
        Timestamp date = getActivityDate();
        if (date == null) return "";

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * Determines whether the activity is a system event or task-related.
     * @return true for system events, false for task-related activities
     */
    public boolean isSystemEvent() {
        return getTaskId() == 0;
    }

    /**
     * Returns the additional CSS classes appropriate for the given activity type,
     * for use in UI styling.
     * @return String with CSS classes
     */
    public String getActivityCssClasses() {
        String type = getActivityType();
        if (type == null) return "activity-unknown";

        switch (type.toUpperCase()) {
            case "CREATE":
                return "activity-create";
            case "UPDATE":
                return "activity-update";
            case "STATUS":
                return "activity-status";
            case "ASSIGN":
                return "activity-assign";
            case "COMMENT":
                return "activity-comment";
            case "PASSWORD":
                return "activity-password";
            case "LOGIN":
                return "activity-login";
            case "LOGOUT":
                return "activity-logout";
            case "USER_MANAGEMENT":
                return "activity-user-management";
            case "TEAM_MANAGEMENT":
                return "activity-team-management";
            default:
                return "activity-other";
        }
    }
}