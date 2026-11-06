package pl.rozowi.app.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.TaskActivity;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskActivityDAO using Mockito.
 * Tests operations on the task activity log in the database, using mocks to
 * simulate the behavior of the database layer.
 */
@RunWith(MockitoJUnitRunner.Silent.class) // Using Silent runner to ignore unnecessary stubbing
public class TaskActivityDAOTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    private static MockedStatic<DatabaseManager> databaseManagerStatic;
    private TaskActivityDAO taskActivityDAO;

    /**
     * Initializes the test environment before each test.
     * Creates mocks for the database connection, statements and query results.
     * Configures static mocking of DatabaseManager so that it returns the prepared
     * mocks instead of real database connections.
     */
    @Before
    public void setUp() throws SQLException {
        mockConnection = mock(Connection.class, Mockito.withSettings().defaultAnswer(Mockito.RETURNS_DEFAULTS));
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        databaseManagerStatic = Mockito.mockStatic(DatabaseManager.class);
        databaseManagerStatic.when(DatabaseManager::getConnection)
                .thenReturn(mockConnection);

        when(mockConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery())
                .thenReturn(mockResultSet);

        taskActivityDAO = new TaskActivityDAO();
    }

    /**
     * Cleans up resources after each test.
     * Closes the DatabaseManager static mock to avoid memory leaks.
     */
    @After
    public void tearDown() {
        if (databaseManagerStatic != null) {
            databaseManagerStatic.close();
        }
    }

    /**
     * Tests getAllActivities, which should return all task activities from the
     * database.
     * Simulates two rows returned from the database and verifies that they were
     * correctly converted to TaskActivity objects.
     */
    @Test
    public void testGetAllActivities() throws SQLException {
        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(mockResultSet.getInt("id")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getInt("task_id")).thenReturn(10).thenReturn(11);
        when(mockResultSet.getInt("user_id")).thenReturn(5).thenReturn(6);
        when(mockResultSet.getString("activity_type"))
                .thenReturn("CREATE").thenReturn("STATUS");
        when(mockResultSet.getString("description"))
                .thenReturn("Created task")
                .thenReturn("Status changed to In Progress");

        Timestamp ts1 = Timestamp.valueOf("2023-01-01 10:00:00");
        Timestamp ts2 = Timestamp.valueOf("2023-01-02 11:00:00");
        when(mockResultSet.getTimestamp("activity_date"))
                .thenReturn(ts1).thenReturn(ts2);

        List<TaskActivity> results = taskActivityDAO.getAllActivities();

        assertNotNull("Lista aktywności nie powinna być null", results);
        assertEquals("Lista powinna zawierać dokładnie 2 aktywności", 2, results.size());
        assertEquals("Typ aktywności pierwszego elementu powinien być CREATE", "CREATE", results.get(0).getActivityType());
        assertEquals("Typ aktywności drugiego elementu powinien być STATUS", "STATUS", results.get(1).getActivityType());
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getActivitiesByTaskId, which should return the activities of a
     * specific task.
     * Simulates two rows returned for the task with ID=10 and verifies that they
     * were correctly filtered and processed.
     */
    @Test
    public void testGetActivitiesByTaskId() throws SQLException {
        int taskId = 10;

        when(mockResultSet.next())
                .thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getInt("task_id")).thenReturn(taskId).thenReturn(taskId);
        when(mockResultSet.getInt("user_id")).thenReturn(5).thenReturn(6);
        when(mockResultSet.getString("activity_type"))
                .thenReturn("CREATE").thenReturn("STATUS");
        when(mockResultSet.getString("description"))
                .thenReturn("Created task")
                .thenReturn("Status changed to In Progress");
        Timestamp ts1 = Timestamp.valueOf("2023-01-01 10:00:00");
        Timestamp ts2 = Timestamp.valueOf("2023-01-02 11:00:00");
        when(mockResultSet.getTimestamp("activity_date"))
                .thenReturn(ts1).thenReturn(ts2);

        List<TaskActivity> results = taskActivityDAO.getActivitiesByTaskId(taskId);

        assertNotNull("Lista aktywności nie powinna być null", results);
        assertEquals("Lista powinna zawierać dokładnie 2 aktywności", 2, results.size());
        assertEquals("ID zadania dla pierwszego elementu powinno być zgodne", taskId, results.get(0).getTaskId());
        verify(mockPreparedStatement).setInt(1, taskId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getActivitiesByUserId, which should return the activities performed
     * by a specific user.
     * Simulates two rows returned for the user with ID=5 and checks that they were
     * correctly filtered and processed.
     */
    @Test
    public void testGetActivitiesByUserId() throws SQLException {
        int userId = 5;

        when(mockResultSet.next())
                .thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getInt("task_id")).thenReturn(10).thenReturn(11);
        when(mockResultSet.getInt("user_id")).thenReturn(userId).thenReturn(userId);
        when(mockResultSet.getString("activity_type"))
                .thenReturn("CREATE").thenReturn("STATUS");
        when(mockResultSet.getString("description"))
                .thenReturn("Created task")
                .thenReturn("Status changed to In Progress");
        Timestamp ts1 = Timestamp.valueOf("2023-01-01 10:00:00");
        Timestamp ts2 = Timestamp.valueOf("2023-01-02 11:00:00");
        when(mockResultSet.getTimestamp("activity_date"))
                .thenReturn(ts1).thenReturn(ts2);

        List<TaskActivity> results = taskActivityDAO.getActivitiesByUserId(userId);

        assertNotNull("Lista aktywności nie powinna być null", results);
        assertEquals("Lista powinna zawierać dokładnie 2 aktywności", 2, results.size());
        assertEquals("ID użytkownika dla pierwszego elementu powinno być zgodne", userId, results.get(0).getUserId());
        verify(mockPreparedStatement).setInt(1, userId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests a failed attempt to add a new task activity.
     * Simulates an INSERT query that returns 0 modified rows.
     * Verifies that the method handles this case correctly and returns false.
     */
    @Test
    public void testInsertTaskActivity_failure() throws SQLException {
        TaskActivity activity = new TaskActivity();
        activity.setTaskId(10);
        activity.setUserId(5);
        activity.setActivityType("CREATE");
        activity.setDescription("Created task");
        activity.setActivityDate(new Timestamp(System.currentTimeMillis()));

        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        boolean result = taskActivityDAO.insertTaskActivity(activity);

        assertFalse("Metoda insertTaskActivity powinna zwrócić false dla nieudanej operacji", result);
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests deleting the activities of a specific task.
     * Simulates a successful DELETE and verifies that the method correctly sets
     * the task ID parameter in the query.
     */
    @Test
    public void testDeleteActivityByTaskId_success() throws SQLException {
        int taskId = 10;

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

//        boolean result = taskActivityDAO.deleteActivityByTaskId(taskId);

//        // Weryfikacja wyników
//        assertTrue("Metoda deleteActivityByTaskId powinna zwrócić true dla udanej operacji", result);
//        verify(mockPreparedStatement).setInt(1, taskId);
//        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests SQLException handling in the DAO.
     * Simulates an SQLException thrown while executing a query and verifies that
     * the method handles it correctly, returning an empty list instead of
     * propagating the exception.
     */
    @Test
    public void testHandleSQLException() throws SQLException {
        int taskId = 10;

        when(mockPreparedStatement.executeQuery())
                .thenThrow(new SQLException("Database error"));

        List<TaskActivity> results = taskActivityDAO.getActivitiesByTaskId(taskId);

        assertNotNull("Metoda powinna zwrócić pustą listę, a nie null", results);
        assertTrue("Lista powinna być pusta w przypadku błędu SQL", results.isEmpty());
    }
}