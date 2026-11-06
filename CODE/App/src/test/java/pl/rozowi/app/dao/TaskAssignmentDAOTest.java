package pl.rozowi.app.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.TaskAssignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskAssignmentDAO using Mockito.
 * Tests operations for assigning users to tasks, using mocks to simulate the
 * behavior of the database layer.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskAssignmentDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    private TaskAssignmentDAO dao;
    private MockedStatic<DatabaseManager> mockedDatabaseManager;

    /**
     * Initializes the test environment before each test.
     * Creates mocks for the database connection and statements.
     * Configures static mocking of DatabaseManager so that it returns the prepared
     * mocks instead of real database connections.
     */
    @Before
    public void setUp() throws SQLException {
        dao = new TaskAssignmentDAO();

        mockedDatabaseManager = mockStatic(DatabaseManager.class);
        mockedDatabaseManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    /**
     * Cleans up resources after each test.
     * Closes the DatabaseManager static mock to avoid memory leaks.
     */
    @After
    public void tearDown() {
        if (mockedDatabaseManager != null) {
            mockedDatabaseManager.close();
        }
    }

    /**
     * Tests successfully assigning a user to a task.
     * Simulates a successful INSERT with one modified row.
     * Verifies that the method sets the query parameters correctly and returns
     * true.
     */
    @Test
    public void testInsertTaskAssignment_success() throws SQLException {
        TaskAssignment assignment = new TaskAssignment();
        assignment.setTaskId(10);
        assignment.setUserId(20);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = dao.insertTaskAssignment(assignment);

        assertTrue("Should return true when affected > 0", result);

        verify(mockPreparedStatement).setInt(1, 10);
        verify(mockPreparedStatement).setInt(2, 20);
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests the case where an INSERT does not modify any rows.
     * Simulates a query that returns 0 as the number of modified rows.
     * Verifies that the method sets the query parameters correctly and returns
     * false.
     */
    @Test
    public void testInsertTaskAssignment_noRowsAffected() throws SQLException {
        TaskAssignment assignment = new TaskAssignment();
        assignment.setTaskId(5);
        assignment.setUserId(6);

        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        boolean result = dao.insertTaskAssignment(assignment);

        assertFalse("Should return false when affected == 0", result);

        verify(mockPreparedStatement).setInt(1, 5);
        verify(mockPreparedStatement).setInt(2, 6);
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests SQLException handling in the DAO.
     * Simulates an SQLException thrown while preparing a query and verifies that
     * the method handles it correctly, returning false instead of propagating the
     * exception.
     */
    @Test
    public void testInsertTaskAssignment_sqlException() throws SQLException {
        TaskAssignment assignment = new TaskAssignment();
        assignment.setTaskId(1);
        assignment.setUserId(2);

        reset(mockConnection);

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        boolean result = dao.insertTaskAssignment(assignment);

        assertFalse("Should return false on SQLException", result);
    }
}