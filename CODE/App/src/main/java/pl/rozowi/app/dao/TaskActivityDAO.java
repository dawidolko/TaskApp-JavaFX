package pl.rozowi.app.dao;

import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.TaskActivity;
import pl.rozowi.app.models.EnhancedTaskActivity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the task activity table.
 * Handles operations related to logging user actions on tasks, such as:
 * <ul>
 *   <li>Recording new activities</li>
 *   <li>Fetching activities by task, user, type or date</li>
 *   <li>Fetching extended activity information with additional data</li>
 *   <li>Filtering activities by multiple criteria</li>
 * </ul>
 */
public class TaskActivityDAO {

    /**
     * Inserts a new task-related activity into the database.
     *
     * @param activity TaskActivity object containing the activity details
     * @return true if the insert succeeded, false on error
     */
    public boolean insertTaskActivity(TaskActivity activity) {
        String sql = "INSERT INTO task_activities (task_id, user_id, activity_type, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, activity.getTaskId());
            stmt.setInt(2, activity.getUserId());
            stmt.setString(3, activity.getActivityType());
            stmt.setString(4, activity.getDescription());
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Gets all activities for the given task, sorted by date in descending order.
     *
     * @param taskId ID of the task to fetch activities for
     * @return list of TaskActivity objects related to the task
     */
    public List<TaskActivity> getActivitiesByTaskId(int taskId) {
        List<TaskActivity> activities = new ArrayList<>();
        String sql = "SELECT * FROM task_activities WHERE task_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TaskActivity activity = mapActivityFromResultSet(rs);
                activities.add(activity);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return activities;
    }

    /**
     * Gets all activities recorded in the system, newest first.
     *
     * @return list of all TaskActivity activities
     */
    public List<TaskActivity> getAllActivities() {
        List<TaskActivity> activities = new ArrayList<>();
        String sql = "SELECT * FROM task_activities ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                TaskActivity activity = mapActivityFromResultSet(rs);
                activities.add(activity);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return activities;
    }

    /**
     * Gets the activities performed by the given user.
     *
     * @param userId ID of the user whose activities are to be fetched
     * @return list of activities performed by the user
     */
    public List<TaskActivity> getActivitiesByUserId(int userId) {
        List<TaskActivity> activities = new ArrayList<>();
        String sql = "SELECT * FROM task_activities WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TaskActivity activity = mapActivityFromResultSet(rs);
                activities.add(activity);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return activities;
    }

    /**
     * Gets activities by type (e.g. "created", "edited", "deleted").
     *
     * @param activityType activity type
     * @return list of activities of the given type
     */
    public List<TaskActivity> getActivitiesByType(String activityType) {
        List<TaskActivity> activities = new ArrayList<>();
        String sql = "SELECT * FROM task_activities WHERE activity_type = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activityType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TaskActivity activity = mapActivityFromResultSet(rs);
                activities.add(activity);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return activities;
    }

    /**
     * Gets the activities that took place in the given time range.
     *
     * @param startDate start date of the range
     * @param endDate end date of the range
     * @return list of activities from the given date range
     */
    public List<TaskActivity> getActivitiesByDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        List<TaskActivity> activities = new ArrayList<>();
        String sql = "SELECT * FROM task_activities WHERE DATE(created_at) BETWEEN ? AND ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TaskActivity activity = mapActivityFromResultSet(rs);
                activities.add(activity);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return activities;
    }

    /**
     * Gets all activities along with additional information about users and tasks.
     *
     * @return list of activities with extended data (task name, user's first and last
     *         name)
     */
    public List<TaskActivity> getAllActivitiesWithDetails() {
        List<TaskActivity> activities = new ArrayList<>();

        String sql = "SELECT ta.*, t.title as task_title, u.name as user_name, u.last_name as user_last_name " +
                    "FROM task_activities ta " +
                    "LEFT JOIN tasks t ON ta.task_id = t.id " +
                    "LEFT JOIN users u ON ta.user_id = u.id " +
                    "ORDER BY ta.created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TaskActivity activity = new TaskActivity();
                activity.setId(rs.getInt("id"));
                activity.setTaskId(rs.getInt("task_id"));
                activity.setUserId(rs.getInt("user_id"));
                activity.setActivityType(rs.getString("activity_type"));
                activity.setDescription(rs.getString("description"));
                activity.setActivityDate(rs.getTimestamp("created_at"));
                activities.add(activity);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return activities;
    }

    /**
     * Gets activities by the given filters:
     * activity type, date range, user and task.
     *
     * @param activityType activity type (may be null)
     * @param startDate start date (may be null)
     * @param endDate end date (may be null)
     * @param userId user ID (0 means all)
     * @param taskId task ID (0 means all)
     * @return list of activities matching the filter conditions
     */
    public List<TaskActivity> getFilteredActivities(String activityType, java.sql.Date startDate,
                                                 java.sql.Date endDate, int userId, int taskId) {
        List<TaskActivity> activities = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT ta.*, t.title as task_title, u.name as user_name, u.last_name as user_last_name " +
            "FROM task_activities ta " +
            "LEFT JOIN tasks t ON ta.task_id = t.id " +
            "LEFT JOIN users u ON ta.user_id = u.id " +
            "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (activityType != null && !activityType.isEmpty()) {
            sqlBuilder.append("AND ta.activity_type = ? ");
            params.add(activityType);
        }

        if (startDate != null) {
            sqlBuilder.append("AND DATE(ta.created_at) >= ? ");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append("AND DATE(ta.created_at) <= ? ");
            params.add(endDate);
        }

        if (userId > 0) {
            sqlBuilder.append("AND ta.user_id = ? ");
            params.add(userId);
        }

        if (taskId > 0) {
            sqlBuilder.append("AND ta.task_id = ? ");
            params.add(taskId);
        }

        sqlBuilder.append("ORDER BY ta.created_at DESC");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TaskActivity activity = new TaskActivity();
                    activity.setId(rs.getInt("id"));
                    activity.setTaskId(rs.getInt("task_id"));
                    activity.setUserId(rs.getInt("user_id"));
                    activity.setActivityType(rs.getString("activity_type"));
                    activity.setDescription(rs.getString("description"));
                    activity.setActivityDate(rs.getTimestamp("created_at"));

                    activities.add(activity);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return activities;
    }

    /**
     * Gets the extended version of activities with additional user and task data.
     * Used mainly for displaying information in the user interface.
     *
     * @return list of extended EnhancedTaskActivity activities
     */
    public List<EnhancedTaskActivity> getEnhancedActivities() {
        List<EnhancedTaskActivity> enhancedActivities = new ArrayList<>();

        String sql = "SELECT ta.*, t.title as task_title, u.name as user_name, u.last_name as user_last_name, u.email as user_email " +
                     "FROM task_activities ta " +
                     "LEFT JOIN tasks t ON ta.task_id = t.id " +
                     "LEFT JOIN users u ON ta.user_id = u.id " +
                     "ORDER BY ta.created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                EnhancedTaskActivity activity = new EnhancedTaskActivity();

                activity.setId(rs.getInt("id"));
                activity.setTaskId(rs.getInt("task_id"));
                activity.setUserId(rs.getInt("user_id"));
                activity.setActivityType(rs.getString("activity_type"));
                activity.setDescription(rs.getString("description"));
                activity.setActivityDate(rs.getTimestamp("created_at"));

                activity.setTaskTitle(rs.getString("task_title"));
                activity.setUserName(rs.getString("user_name"));
                activity.setUserLastName(rs.getString("user_last_name"));
                activity.setUserEmail(rs.getString("user_email"));

                enhancedActivities.add(activity);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return enhancedActivities;
    }

    /**
     * Gets filtered extended activities based on:
     * type, date range, user, task and search phrase.
     *
     * @param activityType activity type (may be null)
     * @param startDate start date (may be null)
     * @param endDate end date (may be null)
     * @param userId user ID (0 means all)
     * @param taskId task ID (0 means all)
     * @param searchText phrase to search for (e.g. description, task name, email)
     * @return list of extended activities matching the filter conditions
     */
    public List<EnhancedTaskActivity> getFilteredEnhancedActivities(
            String activityType,
            java.sql.Date startDate,
            java.sql.Date endDate,
            int userId,
            int taskId,
            String searchText) {

        List<EnhancedTaskActivity> enhancedActivities = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT ta.*, t.title as task_title, u.name as user_name, u.last_name as user_last_name, u.email as user_email " +
            "FROM task_activities ta " +
            "LEFT JOIN tasks t ON ta.task_id = t.id " +
            "LEFT JOIN users u ON ta.user_id = u.id " +
            "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (activityType != null && !activityType.isEmpty() && !activityType.equals("Wszystkie")) {
            sqlBuilder.append("AND ta.activity_type = ? ");
            params.add(activityType);
        }

        if (startDate != null) {
            sqlBuilder.append("AND DATE(ta.created_at) >= ? ");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append("AND DATE(ta.created_at) <= ? ");
            params.add(endDate);
        }

        if (userId > 0) {
            sqlBuilder.append("AND ta.user_id = ? ");
            params.add(userId);
        }

        if (taskId > 0) {
            sqlBuilder.append("AND ta.task_id = ? ");
            params.add(taskId);
        }

        if (searchText != null && !searchText.isEmpty()) {
            sqlBuilder.append("AND (LOWER(ta.description) LIKE ? OR LOWER(t.title) LIKE ? " +
                             "OR LOWER(u.name) LIKE ? OR LOWER(u.last_name) LIKE ? OR LOWER(u.email) LIKE ?) ");
            String searchPattern = "%" + searchText.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        sqlBuilder.append("ORDER BY ta.created_at DESC");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EnhancedTaskActivity activity = new EnhancedTaskActivity();

                    activity.setId(rs.getInt("id"));
                    activity.setTaskId(rs.getInt("task_id"));
                    activity.setUserId(rs.getInt("user_id"));
                    activity.setActivityType(rs.getString("activity_type"));
                    activity.setDescription(rs.getString("description"));
                    activity.setActivityDate(rs.getTimestamp("created_at"));

                    activity.setTaskTitle(rs.getString("task_title"));
                    activity.setUserName(rs.getString("user_name"));
                    activity.setUserLastName(rs.getString("user_last_name"));
                    activity.setUserEmail(rs.getString("user_email"));

                    enhancedActivities.add(activity);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return enhancedActivities;
    }

    /**
     * Helper method mapping data from a ResultSet to a TaskActivity object.
     *
     * @param rs ResultSet object containing data from the database
     * @return TaskActivity object filled with data from the ResultSet
     * @throws SQLException if reading data from the ResultSet fails
     */
    private TaskActivity mapActivityFromResultSet(ResultSet rs) throws SQLException {
        TaskActivity activity = new TaskActivity();
        activity.setId(rs.getInt("id"));
        activity.setTaskId(rs.getInt("task_id"));
        activity.setUserId(rs.getInt("user_id"));
        activity.setActivityType(rs.getString("activity_type"));
        activity.setDescription(rs.getString("description"));
        activity.setActivityDate(rs.getTimestamp("created_at"));
        return activity;
    }
}