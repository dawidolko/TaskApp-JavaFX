package pl.rozowi.app.dao;

import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object handling task-related database operations.
 * Contains methods for fetching, creating, updating and deleting tasks, as well as
 * managing task assignments to users.
 */
public class TaskDAO {

    /**
     * Gets all tasks for the given project along with information about assigned users.
     *
     * @param projectId ID of the project to fetch tasks for
     * @return list of Task objects belonging to the given project
     */
    public List<Task> getTasksByProjectId(int projectId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
                    SELECT t.id, t.project_id, t.team_id, t.title, t.description, 
                           t.status, t.priority, t.start_date, t.end_date,
                           teams.team_name, u.email as assigned_email, u.id as assigned_id
                    FROM tasks t
                    LEFT JOIN teams ON t.team_id = teams.id
                    LEFT JOIN task_assignments ta ON t.id = ta.task_id
                    LEFT JOIN users u ON ta.user_id = u.id
                    WHERE t.project_id = ?
                    """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task t = new Task();
                    t.setId(rs.getInt("id"));
                    t.setProjectId(rs.getInt("project_id"));
                    t.setTeamId(rs.getInt("team_id"));
                    t.setTitle(rs.getString("title"));
                    t.setDescription(rs.getString("description"));
                    t.setStatus(rs.getString("status"));
                    t.setPriority(rs.getString("priority"));
                    t.setStartDate(rs.getString("start_date"));
                    t.setEndDate(rs.getString("end_date"));
                    t.setTeamName(rs.getString("team_name"));

                    String email = rs.getString("assigned_email");
                    if (email != null) {
                        t.setAssignedEmail(email);
                        t.setAssignedTo(rs.getInt("assigned_id"));
                    }

                    tasks.add(t);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tasks;
    }

    /**
     * Gets all tasks for the given team along with information about assigned users.
     *
     * @param teamId ID of the team to fetch tasks for
     * @return list of Task objects belonging to the given team
     */
    public List<Task> getTasksByTeamId(int teamId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
                    SELECT t.id, t.project_id, t.team_id, t.title, t.description, 
                           t.status, t.priority, t.start_date, t.end_date,
                           teams.team_name, u.email as assigned_email, u.id as assigned_id
                    FROM tasks t
                    LEFT JOIN teams ON t.team_id = teams.id
                    LEFT JOIN task_assignments ta ON t.id = ta.task_id
                    LEFT JOIN users u ON ta.user_id = u.id
                    WHERE t.team_id = ?
                    """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task t = new Task();
                    t.setId(rs.getInt("id"));
                    t.setProjectId(rs.getInt("project_id"));
                    t.setTeamId(rs.getInt("team_id"));
                    t.setTitle(rs.getString("title"));
                    t.setDescription(rs.getString("description"));
                    t.setStatus(rs.getString("status"));
                    t.setPriority(rs.getString("priority"));
                    t.setStartDate(rs.getString("start_date"));
                    t.setEndDate(rs.getString("end_date"));
                    t.setTeamName(rs.getString("team_name"));

                    String email = rs.getString("assigned_email");
                    if (email != null) {
                        t.setAssignedEmail(email);
                        t.setAssignedTo(rs.getInt("assigned_id"));
                    }

                    tasks.add(t);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tasks;
    }

    /**
     * Gets the tasks for teams in which the given user is the leader.
     *
     * @param leaderId ID of the user who is the team leader
     * @return list of Task objects for teams where the user is the leader
     */
    public List<Task> getTasksForLeader(int leaderId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
                    SELECT t.id, t.project_id, t.team_id, t.title, t.description, 
                           t.status, t.priority, t.start_date, t.end_date,
                           teams.team_name, u.email as assigned_email, u.id as assigned_id
                    FROM tasks t
                    JOIN team_members tm ON t.team_id = tm.team_id
                    JOIN teams ON t.team_id = teams.id
                    LEFT JOIN task_assignments ta ON t.id = ta.task_id
                    LEFT JOIN users u ON ta.user_id = u.id
                    WHERE tm.user_id = ? AND tm.is_leader = TRUE
                    ORDER BY t.id
                    """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, leaderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setProjectId(rs.getInt("project_id"));
                    task.setTeamId(rs.getInt("team_id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setStatus(rs.getString("status"));
                    task.setPriority(rs.getString("priority"));
                    task.setStartDate(rs.getString("start_date"));
                    task.setEndDate(rs.getString("end_date"));
                    task.setTeamName(rs.getString("team_name"));

                    String email = rs.getString("assigned_email");
                    if (email != null) {
                        task.setAssignedEmail(email);
                        task.setAssignedTo(rs.getInt("assigned_id"));
                    }

                    tasks.add(task);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tasks;
    }

    /**
     * Adds a new task to the database.
     *
     * @param task Task object containing the data to insert
     * @return true if the operation succeeded, false otherwise
     */
    public boolean insertTask(Task task) {
        String sql =
                "INSERT INTO tasks (project_id, team_id, title, description, status, priority, start_date, end_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, task.getProjectId());
            stmt.setInt(2, task.getTeamId());
            stmt.setString(3, task.getTitle());
            stmt.setString(4, task.getDescription());
            stmt.setString(5, task.getStatus());
            stmt.setString(6, task.getPriority());
            stmt.setString(7, task.getStartDate());
            stmt.setString(8, task.getEndDate());

            int affected = stmt.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    task.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a task's status.
     *
     * @param taskId ID of the task to update
     * @param newStatus new task status
     * @return true if the operation succeeded, false otherwise
     */
    public boolean updateTaskStatus(int taskId, String newStatus) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, taskId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the tasks assigned to the given user.
     *
     * @param userId ID of the user to fetch tasks for
     * @return list of Task objects assigned to the user
     */
    public List<Task> getTasksForUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
                    SELECT t.id, t.project_id, t.team_id, t.title, t.description, 
                           t.status, t.priority, t.start_date, t.end_date,
                           teams.team_name
                    FROM tasks t
                    JOIN task_assignments ta ON t.id = ta.task_id
                    LEFT JOIN teams ON t.team_id = teams.id
                    WHERE ta.user_id = ?
                    """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setProjectId(rs.getInt("project_id"));
                task.setTeamId(rs.getInt("team_id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setStartDate(rs.getString("start_date"));
                task.setEndDate(rs.getString("end_date"));
                task.setTeamName(rs.getString("team_name"));

                task.setAssignedTo(userId);

                tasks.add(task);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tasks;
    }

    /**
     * Gets a team's name by its ID.
     *
     * @param teamId team ID
     * @return team name, or null if not found
     */
    private String getTeamNameById(int teamId) {
        String sql = "SELECT team_name FROM teams WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("team_name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the tasks of colleagues in the same team.
     *
     * @param userId ID of the user excluded from the results
     * @param teamId ID of the team to fetch tasks for
     * @return list of Task objects assigned to other team members
     */
    public List<Task> getColleagueTasks(int userId, int teamId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
                    SELECT t.id, t.project_id, t.team_id, t.title, t.description, 
                           t.status, t.priority, t.start_date, t.end_date,
                           teams.team_name, u.email as assigned_email, u.id as assigned_id
                    FROM tasks t
                    JOIN task_assignments ta ON t.id = ta.task_id
                    LEFT JOIN teams ON t.team_id = teams.id
                    LEFT JOIN users u ON ta.user_id = u.id
                    WHERE t.team_id = ? AND ta.user_id != ?
                    """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setProjectId(rs.getInt("project_id"));
                task.setTeamId(rs.getInt("team_id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setStartDate(rs.getString("start_date"));
                task.setEndDate(rs.getString("end_date"));
                task.setTeamName(rs.getString("team_name"));

                String email = rs.getString("assigned_email");
                if (email != null) {
                    task.setAssignedEmail(email);
                    task.setAssignedTo(rs.getInt("assigned_id"));
                }

                tasks.add(task);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tasks;
    }

    /**
     * Updates all task fields in the database.
     *
     * @param task Task object containing the updated data
     * @return true if the operation succeeded, false otherwise
     */
    public boolean updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ?, priority = ?, " +
                     "start_date = ?, end_date = ?, team_id = ?, project_id = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus());
            stmt.setString(4, task.getPriority());
            stmt.setString(5, task.getStartDate());
            stmt.setString(6, task.getEndDate());
            stmt.setInt(7, task.getTeamId());
            stmt.setInt(8, task.getProjectId());
            stmt.setInt(9, task.getId());

            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Gets the ID of the user assigned to a task.
     *
     * @param taskId task ID
     * @return ID of the assigned user, or 0 if there is no assignment
     */
    public int getAssignedUserId(int taskId) {
        String sql = "SELECT user_id FROM task_assignments WHERE task_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets the email of the user assigned to a task.
     *
     * @param taskId task ID
     * @return email of the assigned user, or an empty string if there is no assignment
     */
    public String getAssignedUserEmail(int taskId) {
        String sql = "SELECT u.email FROM task_assignments ta " +
                     "JOIN users u ON ta.user_id = u.id " +
                     "WHERE ta.task_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * Assigns a task to a user by updating the join table.
     *
     * @param taskId ID of the task to assign
     * @param userId ID of the user the task is assigned to
     * @return true if the operation succeeded, false otherwise
     */
    public boolean assignTask(int taskId, int userId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement delStmt = conn.prepareStatement(
                        "DELETE FROM task_assignments WHERE task_id = ?")) {
                    delStmt.setInt(1, taskId);
                    delStmt.executeUpdate();
                }

                try (PreparedStatement insStmt = conn.prepareStatement(
                        "INSERT INTO task_assignments (task_id, user_id) VALUES (?, ?)")) {
                    insStmt.setInt(1, taskId);
                    insStmt.setInt(2, userId);
                    insStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a task and all related records from the database.
     *
     * @param taskId ID of the task to delete
     * @return true if the operation succeeded, false otherwise
     */
    public boolean deleteTask(int taskId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM task_assignments WHERE task_id = ?")) {
                    stmt.setInt(1, taskId);
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM task_activities WHERE task_id = ?")) {
                    stmt.setInt(1, taskId);
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM tasks WHERE id = ?")) {
                    stmt.setInt(1, taskId);
                    int affected = stmt.executeUpdate();

                    conn.commit();
                    return affected > 0;
                }
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}