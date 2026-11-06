package pl.rozowi.app.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserDAO using Mockito.
 * Tests operations on users in the database, using mocks to simulate the
 * behavior of the database layer.
 */
public class UserDAOTest {

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    private MockedStatic<DatabaseManager> dbMock;
    private UserDAO userDAO;

    /**
     * Initializes the test environment before each test.
     * Creates and configures mocks for the database connection, preparedStatements
     * and resultSets. Also initializes the static mock of DatabaseManager.
     */
    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);

        dbMock = mockStatic(DatabaseManager.class);
        dbMock.when(DatabaseManager::getConnection).thenReturn(mockConnection);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        userDAO = new UserDAO();
    }

    /**
     * Cleans up resources after each test.
     * Closes the DatabaseManager static mock to avoid memory leaks.
     */
    @After
    public void tearDown() {
        dbMock.close();
    }

    /**
     * Tests getUserById for an existing user.
     * Verifies that the method correctly fetches data from the database and
     * creates a User object from it.
     */
    @Test
    public void testGetUserById_userExists() throws SQLException {
        int userId = 1;
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(userId);
        when(mockResultSet.getString("name")).thenReturn("John");
        when(mockResultSet.getString("last_name")).thenReturn("Doe");
        when(mockResultSet.getString("email")).thenReturn("john@doe.com");
        when(mockResultSet.getString("password")).thenReturn("pw");
        when(mockResultSet.getInt("role_id")).thenReturn(3);
        when(mockResultSet.getInt("group_id")).thenReturn(2);
        when(mockResultSet.getString("password_hint")).thenReturn("hint");

        User u = userDAO.getUserById(userId);

        assertNotNull(u);
        assertEquals(userId, u.getId());
        assertEquals("John", u.getName());
        assertEquals("Doe", u.getLastName());
        assertEquals("john@doe.com", u.getEmail());
        assertEquals("pw", u.getPassword());
        assertEquals(3, u.getRoleId());
        assertEquals(2, u.getGroupId());

        verify(mockPreparedStatement).setInt(1, userId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getUserById for a non-existent user.
     * Verifies that the method correctly returns null when there is no user with
     * the given ID.
     */
    @Test
    public void testGetUserById_userDoesNotExist() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        User u = userDAO.getUserById(999);
        assertNull(u);

        verify(mockPreparedStatement).setInt(1, 999);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getUserByEmail for an existing user.
     * Verifies that the method correctly fetches data from the database and
     * creates a User object from it based on the email address.
     */
    @Test
    public void testGetUserByEmail_userExists() throws SQLException {
        String email = "foo@bar.com";
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(5);
        when(mockResultSet.getString("name")).thenReturn("Foo");
        when(mockResultSet.getString("last_name")).thenReturn("Bar");
        when(mockResultSet.getString("email")).thenReturn(email);
        when(mockResultSet.getString("password")).thenReturn("pw2");
        when(mockResultSet.getInt("role_id")).thenReturn(1);
        when(mockResultSet.getInt("group_id")).thenReturn(4);
        when(mockResultSet.getString("password_hint")).thenReturn("hint");

        User u = userDAO.getUserByEmail(email);

        assertNotNull(u);
        assertEquals(email, u.getEmail());

        verify(mockPreparedStatement).setString(1, email);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getAllUsers.
     * Verifies that the method correctly fetches all users from the database and
     * creates a list of User objects from them.
     */
    @Test
    public void testGetAllUsers() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("name")).thenReturn("A","B");
        when(mockResultSet.getString("last_name")).thenReturn("X","Y");
        when(mockResultSet.getString("email")).thenReturn("a@x","b@y");
        when(mockResultSet.getString("password")).thenReturn("p1","p2");
        when(mockResultSet.getInt("role_id")).thenReturn(10,20);
        when(mockResultSet.getInt("group_id")).thenReturn(100,200);
        when(mockResultSet.getString("password_hint")).thenReturn("hint1", "hint2");

        List<User> list = userDAO.getAllUsers();

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).getId());
        assertEquals(2, list.get(1).getId());

        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests successfully adding a new user.
     * Verifies that insertUser sets the query parameters correctly and returns
     * true when the operation succeeds.
     */
    @Test
    public void testInsertUser_success() throws SQLException {
        User u = new User();
        u.setName("N");
        u.setLastName("L");
        u.setEmail("e");
        u.setPassword("p");
        u.setRoleId(7);
        u.setGroupId(8);

        boolean ok = userDAO.insertUser(u);

        assertTrue(ok);
        assertEquals(0, u.getId());

        verify(mockPreparedStatement).setString(1, "N");
        verify(mockPreparedStatement).setString(2, "L");
        verify(mockPreparedStatement).setString(3, "p");
        verify(mockPreparedStatement).setString(4, "e");
        verify(mockPreparedStatement).setInt(5, 7);
        verify(mockPreparedStatement).setInt(6, 8);
        verify(mockPreparedStatement).setString(7, null);
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests a failed attempt to add a new user.
     * Simulates an INSERT that adds no rows and verifies that the method returns
     * false in that case.
     */
    @Test
    public void testInsertUser_failure() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        User u = new User();
        boolean ok = userDAO.insertUser(u);
        assertFalse(ok);

        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests successfully updating user data, including automatically adding a
     * settings record.
     * Verifies the database transaction and checks that all parameters are set
     * correctly.
     */
    @Test
    public void testUpdateUser_success() throws SQLException {
        PreparedStatement mockUserStatement = mock(PreparedStatement.class);
        PreparedStatement mockSettingsStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(contains("UPDATE users"))).thenReturn(mockUserStatement);
        when(mockConnection.prepareStatement(contains("INSERT INTO settings"))).thenReturn(mockSettingsStatement);

        when(mockUserStatement.executeUpdate()).thenReturn(1);
        when(mockSettingsStatement.executeUpdate()).thenReturn(1);

        User u = new User();
        u.setId(55);
        u.setName("N2");
        u.setLastName("L2");
        u.setEmail("e2");
        u.setPassword("p2");
        u.setRoleId(9);
        u.setGroupId(10);
        u.setPasswordHint("hint");

        boolean ok = userDAO.updateUser(u);
        assertTrue(ok);

        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);

        verify(mockUserStatement).setString(1, "e2");
        verify(mockUserStatement).setString(2, "p2");
        verify(mockUserStatement).setString(3, "hint");
        verify(mockUserStatement).setString(4, "N2");
        verify(mockUserStatement).setString(5, "L2");
        verify(mockUserStatement).setInt(6, 9);
        verify(mockUserStatement).setInt(7, 10);
        verify(mockUserStatement).setInt(8, 55);

        verify(mockSettingsStatement).setInt(1, 55);

        verify(mockUserStatement).executeUpdate();
        verify(mockSettingsStatement).executeUpdate();
    }

    /**
     * Tests error handling when updating user data.
     * Simulates an SQLException during the update and verifies that the
     * transaction is correctly rolled back.
     */
    @Test
    public void testUpdateUser_failure() throws SQLException {
        PreparedStatement mockUserStatement = mock(PreparedStatement.class);
        PreparedStatement mockSettingsStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(contains("UPDATE users"))).thenReturn(mockUserStatement);
        when(mockConnection.prepareStatement(contains("INSERT INTO settings"))).thenReturn(mockSettingsStatement);

        when(mockUserStatement.executeUpdate()).thenThrow(new SQLException("Update failed"));

        User u = new User();
        u.setId(55);
        boolean ok = userDAO.updateUser(u);
        assertFalse(ok);

        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection, never()).commit();
        verify(mockConnection).rollback();
        verify(mockConnection).setAutoCommit(true);

        verify(mockUserStatement).executeUpdate();
    }

    /**
     * Tests successfully deleting a user and their settings.
     * Verifies the database transaction and checks that all query parameters are
     * set correctly.
     */
    @Test
    public void testDeleteUser_success() throws SQLException {
        PreparedStatement mockSettingsStatement = mock(PreparedStatement.class);
        PreparedStatement mockUserStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(contains("DELETE FROM settings"))).thenReturn(mockSettingsStatement);
        when(mockConnection.prepareStatement(contains("DELETE FROM users"))).thenReturn(mockUserStatement);

        when(mockSettingsStatement.executeUpdate()).thenReturn(1);
        when(mockUserStatement.executeUpdate()).thenReturn(1);

        boolean ok = userDAO.deleteUser(77);
        assertTrue(ok);

        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);

        verify(mockSettingsStatement).setInt(1, 77);
        verify(mockUserStatement).setInt(1, 77);

        verify(mockSettingsStatement).executeUpdate();
        verify(mockUserStatement).executeUpdate();
    }

    /**
     * Tests a failed user deletion.
     * Simulates a DELETE that removes no rows and verifies that the method returns
     * false in that case.
     */
    @Test
    public void testDeleteUser_failure() throws SQLException {
        PreparedStatement mockSettingsStatement = mock(PreparedStatement.class);
        PreparedStatement mockUserStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(contains("DELETE FROM settings"))).thenReturn(mockSettingsStatement);
        when(mockConnection.prepareStatement(contains("DELETE FROM users"))).thenReturn(mockUserStatement);

        when(mockSettingsStatement.executeUpdate()).thenReturn(1);
        when(mockUserStatement.executeUpdate()).thenReturn(0);

        boolean ok = userDAO.deleteUser(77);
        assertFalse(ok);

        verify(mockSettingsStatement).setInt(1, 77);
        verify(mockUserStatement).setInt(1, 77);

        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);
    }

    /**
     * Tests SQLException handling in getUserById.
     * Simulates an exception while executing the query and verifies that the
     * method correctly returns null in that case.
     */
    @Test
    public void testSQLExceptionHandling_inGetUserById() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("boom"));

        User u = userDAO.getUserById(1);
        assertNull(u);

        verify(mockPreparedStatement).executeQuery();
    }
}