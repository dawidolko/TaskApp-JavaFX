package pl.rozowi.app.dao;

import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.TaskAssignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Object for operations on task assignments (TaskAssignment).
 */
public class TaskAssignmentDAO {

    /**
     * Inserts a new task assignment into the database.
     *
     * @param assignment TaskAssignment object containing the task and user identifiers
     * @return true if the operation succeeded, false otherwise
     */
    public boolean insertTaskAssignment(TaskAssignment assignment) {
        String sql = "INSERT INTO task_assignments (task_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignment.getTaskId());
            stmt.setInt(2, assignment.getUserId());
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
