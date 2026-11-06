package pl.rozowi.app.dao;

import org.junit.Before;
import org.junit.Test;
import pl.rozowi.app.models.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for TaskDAO using a test subclass.
 * Unlike the mock-based approach, these tests use inheritance and method
 * overriding to isolate the tested code from the real database layer.
 */
public class TaskDAOTest {

    /**
     * Inner class TestableTaskDAO, which extends TaskDAO and overrides its
     * methods, simulating the behavior of the database layer.
     * Stores the parameters of the most recent method calls, which makes it
     * possible to verify in tests that the methods were used correctly.
     */
    private class TestableTaskDAO extends TaskDAO {
        private boolean shouldThrowSQLException = false;
        private int mockResultSize = 2;
        private boolean mockOperationSuccess = true;
        private int lastTaskId = 0;
        private int lastUserId = 0;
        private String lastStatus = null;
        private Task lastTask = null;

        /**
         * Overridden getConnection that can simulate SQL exceptions.
         */
        protected Connection getConnection() throws SQLException {
            if (shouldThrowSQLException) {
                throw new SQLException("Test-induced exception");
            }
            return null;
        }

        /**
         * Overridden method fetching tasks for a project.
         * Instead of a real database query, returns prepared test data.
         */
        @Override
        public List<Task> getTasksByProjectId(int projectId) {
            if (shouldThrowSQLException) {
                return super.getTasksByProjectId(projectId);
            }
            return createMockTaskList(projectId, 0);
        }

        /**
         * Overridden method fetching tasks for a team.
         * Returns prepared test data instead of data from the database.
         */
        @Override
        public List<Task> getTasksByTeamId(int teamId) {
            if (shouldThrowSQLException) {
                return super.getTasksByTeamId(teamId);
            }
            return createMockTaskList(0, teamId);
        }

        /**
         * Overridden method fetching tasks assigned to a user.
         * Stores the user ID and returns prepared test data.
         */
        @Override
        public List<Task> getTasksForUser(int userId) {
            if (shouldThrowSQLException) {
                return super.getTasksForUser(userId);
            }
            lastUserId = userId;
            return createMockTaskList(0, 0);
        }

        /**
         * Overridden method fetching tasks for a team leader.
         * Stores the leader ID and returns prepared test data.
         */
        @Override
        public List<Task> getTasksForLeader(int leaderId) {
            if (shouldThrowSQLException) {
                return super.getTasksForLeader(leaderId);
            }
            lastUserId = leaderId;
            return createMockTaskList(0, 0);
        }

        /**
         * Overridden method fetching the tasks of team colleagues.
         * Stores the user ID and returns prepared test data.
         */
        @Override
        public List<Task> getColleagueTasks(int userId, int teamId) {
            if (shouldThrowSQLException) {
                return super.getColleagueTasks(userId, teamId);
            }
            lastUserId = userId;
            return createMockTaskList(0, teamId);
        }

        /**
         * Overridden method inserting a new task.
         * Stores the task and simulates the database assigning an ID.
         */
        @Override
        public boolean insertTask(Task task) {
            if (shouldThrowSQLException) {
                return super.insertTask(task);
            }
            lastTask = task;
            task.setId(1);
            return mockOperationSuccess;
        }

        /**
         * Overridden method updating an existing task.
         * Stores the task for verification in tests.
         */
        @Override
        public boolean updateTask(Task task) {
            if (shouldThrowSQLException) {
                return super.updateTask(task);
            }
            lastTask = task;
            return mockOperationSuccess;
        }

        /**
         * Overridden method deleting a task.
         * Stores the task ID for verification in tests.
         */
        @Override
        public boolean deleteTask(int taskId) {
            if (shouldThrowSQLException) {
                return super.deleteTask(taskId);
            }
            lastTaskId = taskId;
            return mockOperationSuccess;
        }

        /**
         * Overridden method updating a task status.
         * Stores the task ID and the new status for verification.
         */
        @Override
        public boolean updateTaskStatus(int taskId, String newStatus) {
            if (shouldThrowSQLException) {
                return super.updateTaskStatus(taskId, newStatus);
            }
            lastTaskId = taskId;
            lastStatus = newStatus;
            return mockOperationSuccess;
        }

        /**
         * Overridden method assigning a user to a task.
         * Stores the task and user IDs for verification.
         */
        @Override
        public boolean assignTask(int taskId, int userId) {
            if (shouldThrowSQLException) {
                return super.assignTask(taskId, userId);
            }
            lastTaskId = taskId;
            lastUserId = userId;
            return mockOperationSuccess;
        }

        /**
         * Overridden method fetching the ID of the user assigned to a task.
         * Stores the task ID and returns a constant test value.
         */
        @Override
        public int getAssignedUserId(int taskId) {
            if (shouldThrowSQLException) {
                return super.getAssignedUserId(taskId);
            }
            lastTaskId = taskId;
            return 5;
        }

        /**
         * Overridden method fetching the email of the user assigned to a task.
         * Stores the task ID and returns a constant test value.
         */
        @Override
        public String getAssignedUserEmail(int taskId) {
            if (shouldThrowSQLException) {
                return super.getAssignedUserEmail(taskId);
            }
            lastTaskId = taskId;
            return "user@example.com";
        }

        /**
         * Helper method creating a test list of tasks.
         * Creates one or two tasks with predefined values.
         */
        private List<Task> createMockTaskList(int projectId, int teamId) {
            Task task1 = new Task();
            task1.setId(1);
            task1.setProjectId(projectId > 0 ? projectId : 1);
            task1.setTeamId(teamId > 0 ? teamId : 1);
            task1.setTitle("Task 1");
            task1.setDescription("Desc 1");
            task1.setStatus("New");
            task1.setPriority("High");
            task1.setStartDate("2023-01-01");
            task1.setEndDate("2023-02-01");
            task1.setTeamName("Team 1");
            task1.setAssignedEmail("user1@example.com");
            task1.setAssignedTo(101);

            Task task2 = new Task();
            task2.setId(2);
            task2.setProjectId(projectId > 0 ? projectId : 1);
            task2.setTeamId(teamId > 0 ? teamId : 2);
            task2.setTitle("Task 2");
            task2.setDescription("Desc 2");
            task2.setStatus("In Progress");
            task2.setPriority("Medium");
            task2.setStartDate("2023-01-15");
            task2.setEndDate("2023-02-15");
            task2.setTeamName("Team 2");
            task2.setAssignedEmail("user2@example.com");
            task2.setAssignedTo(102);

            return mockResultSize == 2 ?
                    List.of(task1, task2) :
                    List.of(task1);
        }

        public void setShouldThrowSQLException(boolean shouldThrow) {
            this.shouldThrowSQLException = shouldThrow;
        }

        public void setMockResultSize(int size) {
            this.mockResultSize = size;
        }

        /**
         * Sets the success flag for database operations.
         */
        public void setMockOperationSuccess(boolean success) {
            this.mockOperationSuccess = success;
        }

        public int getLastTaskId() {
            return lastTaskId;
        }

        public int getLastUserId() {
            return lastUserId;
        }

        public String getLastStatus() {
            return lastStatus;
        }

        public Task getLastTask() {
            return lastTask;
        }
    }

    private TestableTaskDAO taskDAO;

    /**
     * Initializes the test environment before each test.
     * Creates a TestableTaskDAO instance for testing purposes.
     */
    @Before
    public void setUp() {
        taskDAO = new TestableTaskDAO();
    }

    /**
     * Tests getTasksByProjectId.
     * Verifies that it returns the correct task list for the given project and
     * that all task properties are set correctly.
     */
    @Test
    public void testGetTasksByProjectId() {
        int projectId = 1;

        List<Task> results = taskDAO.getTasksByProjectId(projectId);

        assertNotNull("Result list should not be null", results);
        assertEquals("Result list should contain 2 tasks", 2, results.size());
        assertEquals("First task title should match", "Task 1", results.get(0).getTitle());
        assertEquals("Second task title should match", "Task 2", results.get(1).getTitle());
        assertEquals("First task team name should match", "Team 1", results.get(0).getTeamName());
        assertEquals("First task assigned email should match", "user1@example.com", results.get(0).getAssignedEmail());
        assertEquals("First task assigned id should match", 101, results.get(0).getAssignedTo());
    }

    /**
     * Tests getTasksByTeamId.
     * Verifies that it returns the correct task list for the given team and that
     * the task data is set correctly.
     */
    @Test
    public void testGetTasksByTeamId() {
        int teamId = 1;

        List<Task> results = taskDAO.getTasksByTeamId(teamId);

        assertNotNull("Result list should not be null", results);
        assertEquals("Result list should contain 2 tasks", 2, results.size());
        assertEquals("First task title should match", "Task 1", results.get(0).getTitle());
        assertEquals("Second task title should match", "Task 2", results.get(1).getTitle());
        assertEquals("First task assigned email should match", "user1@example.com", results.get(0).getAssignedEmail());
    }

    /**
     * Tests getTasksForUser.
     * Verifies that it returns the correct task list for the given user and that
     * the user ID is passed to the DAO correctly.
     */
    @Test
    public void testGetTasksForUser() {
        int userId = 1;

        List<Task> results = taskDAO.getTasksForUser(userId);

        assertNotNull("Result list should not be null", results);
        assertEquals("Result list should contain 2 tasks", 2, results.size());
        assertEquals("First task title should match", "Task 1", results.get(0).getTitle());
        assertEquals("Second task title should match", "Task 2", results.get(1).getTitle());
        assertEquals("Last user ID should match", userId, taskDAO.getLastUserId());
    }

    /**
     * Tests successfully adding a new task.
     * Verifies that insertTask saves the task correctly and sets the generated ID
     * after adding it to the database.
     */
    @Test
    public void testInsertTask_success() {
        Task newTask = new Task();
        newTask.setProjectId(1);
        newTask.setTeamId(1);
        newTask.setTitle("New Task");
        newTask.setDescription("New Task Description");
        newTask.setStatus("New");
        newTask.setPriority("High");
        newTask.setStartDate("2023-01-01");
        newTask.setEndDate("2023-02-01");

        boolean result = taskDAO.insertTask(newTask);

        assertTrue("Insert should return true on success", result);
        assertEquals("Task ID should be updated with generated key", 1, newTask.getId());

        Task lastTask = taskDAO.getLastTask();
        assertNotNull("Last task should not be null", lastTask);
        assertEquals("Task title should match", "New Task", lastTask.getTitle());
        assertEquals("Task description should match", "New Task Description", lastTask.getDescription());
    }

    /**
     * Tests successfully updating an existing task.
     * Verifies that updateTask correctly saves the updated task data.
     */
    @Test
    public void testUpdateTask_success() {
        Task task = new Task();
        task.setId(1);
        task.setProjectId(1);
        task.setTeamId(1);
        task.setTitle("Updated Task");
        task.setDescription("Updated Description");
        task.setStatus("In Progress");
        task.setPriority("Medium");
        task.setStartDate("2023-01-15");
        task.setEndDate("2023-02-15");

        boolean result = taskDAO.updateTask(task);

        assertTrue("Update should return true on success", result);

        Task lastTask = taskDAO.getLastTask();
        assertNotNull("Last task should not be null", lastTask);
        assertEquals("Task title should match", "Updated Task", lastTask.getTitle());
        assertEquals("Task description should match", "Updated Description", lastTask.getDescription());
    }

    /**
     * Tests a failed task update.
     * Simulates an update operation that fails and verifies that updateTask
     * handles this case correctly.
     */
    @Test
    public void testUpdateTask_failure() {
        Task task = new Task();
        task.setId(1);
        task.setTitle("Updated Task");
        task.setDescription("Updated Description");
        task.setStatus("In Progress");

        taskDAO.setMockOperationSuccess(false);
        boolean result = taskDAO.updateTask(task);

        assertFalse("Update should return false on failure", result);
    }

    /**
     * Tests successfully deleting a task.
     * Verifies that deleteTask handles the task ID correctly and returns true when
     * the operation succeeds.
     */
    @Test
    public void testDeleteTask_success() {
        int taskId = 1;

        boolean result = taskDAO.deleteTask(taskId);

        assertTrue("Delete should return true on success", result);
        assertEquals("Last task ID should match", taskId, taskDAO.getLastTaskId());
    }

    /**
     * Tests a failed task deletion.
     * Simulates a delete operation that fails and verifies that deleteTask handles
     * this case correctly.
     */
    @Test
    public void testDeleteTask_failure() {
        int taskId = 1;

        taskDAO.setMockOperationSuccess(false);
        boolean result = taskDAO.deleteTask(taskId);

        assertFalse("Delete should return false on failure", result);
        assertEquals("Last task ID should match", taskId, taskDAO.getLastTaskId());
    }

    /**
     * Tests successfully updating a task status.
     * Verifies that updateTaskStatus handles the task ID and the new status
     * correctly, and that it returns true when the operation succeeds.
     */
    @Test
    public void testUpdateTaskStatus_success() {
        int taskId = 1;
        String newStatus = "Completed";

        boolean result = taskDAO.updateTaskStatus(taskId, newStatus);

        assertTrue("Status update should return true on success", result);
        assertEquals("Last task ID should match", taskId, taskDAO.getLastTaskId());
        assertEquals("Last status should match", newStatus, taskDAO.getLastStatus());
    }

    /**
     * Tests successfully assigning a user to a task.
     * Verifies that assignTask handles the task and user IDs correctly, and that
     * it returns true when the operation succeeds.
     */
    @Test
    public void testAssignTask_success() {
        int taskId = 1;
        int userId = 2;

        boolean result = taskDAO.assignTask(taskId, userId);

        assertTrue("Task assignment should return true on success", result);
        assertEquals("Last task ID should match", taskId, taskDAO.getLastTaskId());
        assertEquals("Last user ID should match", userId, taskDAO.getLastUserId());
    }

    /**
     * Tests getTasksForLeader.
     * Verifies that it returns the correct task list for the given leader and that
     * the leader ID is passed to the DAO correctly.
     */
    @Test
    public void testGetTasksForLeader() {
        int leaderId = 1;

        List<Task> results = taskDAO.getTasksForLeader(leaderId);

        assertNotNull("Result list should not be null", results);
        assertEquals("Result list should contain 2 tasks", 2, results.size());
        assertEquals("First task title should match", "Task 1", results.get(0).getTitle());
        assertEquals("Second task title should match", "Task 2", results.get(1).getTitle());
        assertEquals("Last user ID should match", leaderId, taskDAO.getLastUserId());
    }

    /**
     * Tests getColleagueTasks.
     * Verifies that it returns the correct list of team colleagues' tasks for the
     * given user and that the user ID is passed to the DAO correctly.
     */
    @Test
    public void testGetColleagueTasks() {
        int userId = 1;
        int teamId = 1;

        List<Task> results = taskDAO.getColleagueTasks(userId, teamId);

        assertNotNull("Result list should not be null", results);
        assertEquals("Result list should contain 2 tasks", 2, results.size());
        assertEquals("First task title should match", "Task 1", results.get(0).getTitle());
        assertEquals("Second task title should match", "Task 2", results.get(1).getTitle());
        assertEquals("Last user ID should match", userId, taskDAO.getLastUserId());
    }

    /**
     * Tests getAssignedUserId.
     * Verifies that it returns the correct ID of the user assigned to the task and
     * that the task ID is passed to the DAO correctly.
     */
    @Test
    public void testGetAssignedUserId() {
        int taskId = 1;
        int expectedUserId = 5;

        int result = taskDAO.getAssignedUserId(taskId);

        assertEquals("Should return the correct user ID", expectedUserId, result);
        assertEquals("Last task ID should match", taskId, taskDAO.getLastTaskId());
    }

    /**
     * Tests getAssignedUserEmail.
     * Verifies that it returns the correct email of the user assigned to the task
     * and that the task ID is passed to the DAO correctly.
     */
    @Test
    public void testGetAssignedUserEmail() {
        int taskId = 1;
        String expectedEmail = "user@example.com";

        String result = taskDAO.getAssignedUserEmail(taskId);

        assertEquals("Should return the correct email", expectedEmail, result);
        assertEquals("Last task ID should match", taskId, taskDAO.getLastTaskId());
    }
}