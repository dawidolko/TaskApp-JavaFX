package pl.rozowi.app.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TeamMemberDAO using Mockito.
 * Tests operations on team members in the database, using mocks to simulate
 * the behavior of the database layer.
 */
public class TeamMemberDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DatabaseManager> databaseManagerMock;
    private AutoCloseable mocks;

    private TeamMemberDAO teamMemberDAO;

    /**
     * Initializes the test environment before each test.
     * Creates and configures mocks for the database connection, preparedStatements
     * and resultSets. Also initializes the static mock of DatabaseManager.
     */
    @Before
    public void setUp() throws SQLException {
        mocks = MockitoAnnotations.openMocks(this);

        databaseManagerMock = Mockito.mockStatic(DatabaseManager.class);
        databaseManagerMock.when(DatabaseManager::getConnection)
                .thenReturn(mockConnection);

        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery())
                .thenReturn(mockResultSet);

        teamMemberDAO = new TeamMemberDAO();
    }

    /**
     * Cleans up resources after each test.
     * Closes the DatabaseManager static mock and other mocks to avoid memory
     * leaks.
     */
    @After
    public void tearDown() throws Exception {
        databaseManagerMock.close();
        mocks.close();
    }

    /**
     * Tests getTeamIdForUser for a user who is a team member.
     * Verifies that the method correctly returns the ID of the team the user
     * belongs to.
     */
    @Test
    public void testGetTeamIdForUser_userInTeam() throws SQLException {
        int userId = 1;
        int teamId = 2;

        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(false);
        when(mockResultSet.getInt("team_id"))
                .thenReturn(teamId);

        int result = teamMemberDAO.getTeamIdForUser(userId);

        assertEquals("Team ID should match", teamId, result);
        verify(mockPreparedStatement).setInt(1, userId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getTeamIdForUser for a user who is not a member of any team.
     * Verifies that the method correctly returns 0 in that case.
     */
    @Test
    public void testGetTeamIdForUser_userNotInTeam() throws SQLException {
        int userId = 1;
        when(mockResultSet.next())
                .thenReturn(false);

        int result = teamMemberDAO.getTeamIdForUser(userId);

        assertEquals("Should return 0 when user is not in any team", 0, result);
        verify(mockPreparedStatement).setInt(1, userId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests isTeamLeader for a user who is a team leader.
     * Verifies that the method correctly returns true in this case.
     */
    @Test
    public void testIsTeamLeader_isLeader() throws SQLException {
        int teamId = 1;
        int userId = 2;

        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(false);
        when(mockResultSet.getBoolean("is_leader"))
                .thenReturn(true);

        boolean result = teamMemberDAO.isTeamLeader(teamId, userId);

        assertTrue("Should return true when user is team leader", result);
        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).setInt(2, userId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests isTeamLeader for a user who is a team member but not its leader.
     * Verifies that the method correctly returns false in this case.
     */
    @Test
    public void testIsTeamLeader_notLeader() throws SQLException {
        int teamId = 1;
        int userId = 2;

        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(false);
        when(mockResultSet.getBoolean("is_leader"))
                .thenReturn(false);

        boolean result = teamMemberDAO.isTeamLeader(teamId, userId);

        assertFalse("Should return false when user is not team leader", result);
        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).setInt(2, userId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests isTeamLeader for a user who is not a member of the given team.
     * Verifies that the method correctly returns false in this case.
     */
    @Test
    public void testIsTeamLeader_notInTeam() throws SQLException {
        int teamId = 1;
        int userId = 2;

        when(mockResultSet.next())
                .thenReturn(false);

        boolean result = teamMemberDAO.isTeamLeader(teamId, userId);

        assertFalse("Should return false when user is not in team", result);
        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).setInt(2, userId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getTeamMembers, which should return the list of users who are members
     * of the given team.
     * Verifies that data from the database is correctly converted to User objects.
     */
    @Test
    public void testGetTeamMembers() throws SQLException {
        int teamId = 1;

        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(mockResultSet.getInt("id")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getString("name")).thenReturn("John").thenReturn("Jane");
        when(mockResultSet.getString("lastName")).thenReturn("Doe").thenReturn("Smith");
        when(mockResultSet.getString("email"))
                .thenReturn("john.doe@example.com")
                .thenReturn("jane.smith@example.com");
        when(mockResultSet.getString("password"))
                .thenReturn("hash1").thenReturn("hash2");
        when(mockResultSet.getInt("roleId")).thenReturn(3).thenReturn(4);
        when(mockResultSet.getInt("groupId")).thenReturn(1).thenReturn(2);

        List<User> results = teamMemberDAO.getTeamMembers(teamId);

        assertNotNull("Result list should not be null", results);
        assertEquals("Result list should contain 2 members", 2, results.size());
        assertEquals("First member email should match",
                "john.doe@example.com", results.get(0).getEmail());
        assertEquals("Second member email should match",
                "jane.smith@example.com", results.get(1).getEmail());

        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getTeamIdsForTeamLeader, which returns the IDs of the teams led by the
     * given user.
     * Verifies that team IDs are correctly fetched from the database.
     */
    @Test
    public void testGetTeamIdsForTeamLeader() throws SQLException {
        int leaderId = 1;

        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(mockResultSet.getInt("team_id"))
                .thenReturn(1).thenReturn(2);

        List<Integer> results = teamMemberDAO.getTeamIdsForTeamLeader(leaderId);

        assertNotNull("Result list should not be null", results);
        assertEquals("Result list should contain 2 team IDs", 2, results.size());
        assertEquals("First team ID should match", 1, (int) results.get(0));
        assertEquals("Second team ID should match", 2, (int) results.get(1));

        verify(mockPreparedStatement).setInt(1, leaderId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests successfully adding a new member to a team.
     * Verifies that insertTeamMember sets the query parameters correctly and
     * returns true when the operation succeeds.
     */
    @Test
    public void testInsertTeamMember_success() throws SQLException {
        int teamId = 1;
        int userId = 2;
        boolean isLeader = true;

        when(mockPreparedStatement.executeUpdate())
                .thenReturn(1);

        boolean result = teamMemberDAO.insertTeamMember(teamId, userId, isLeader);

        assertTrue("Insert should return true on success", result);
        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).setInt(2, userId);
        verify(mockPreparedStatement).setBoolean(3, isLeader);
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests a failed attempt to add a new member to a team.
     * Simulates an INSERT that adds no rows and verifies that the method returns
     * false in that case.
     */
    @Test
    public void testInsertTeamMember_failure() throws SQLException {
        when(mockPreparedStatement.executeUpdate())
                .thenReturn(0);

        boolean result = teamMemberDAO.insertTeamMember(1, 2, true);

        assertFalse("Insert should return false on failure", result);
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests successfully updating a user's team.
     * Verifies that updateUserTeam sets the query parameters correctly and returns
     * true when the operation succeeds.
     */
    @Test
    public void testUpdateUserTeam_success() throws SQLException {
        int userId = 1;
        int teamId = 2;

        when(mockPreparedStatement.executeUpdate())
                .thenReturn(1);

        boolean result = teamMemberDAO.updateUserTeam(userId, teamId);

        assertTrue("Update should return true on success", result);
        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).setInt(2, userId);
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests successfully removing a member from a team.
     * Verifies that deleteTeamMember sets the query parameters correctly and
     * returns true when the operation succeeds.
     */
    @Test
    public void testDeleteTeamMember_success() throws SQLException {
        when(mockPreparedStatement.executeUpdate())
                .thenReturn(1);

        boolean result = teamMemberDAO.deleteTeamMember(1, 2);

        assertTrue("Delete should return true on success", result);
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).setInt(2, 2);
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests SQL exception handling in getTeamIdForUser.
     * Simulates an SQLException thrown while executing the query and verifies that
     * the method correctly returns 0.
     */
    @Test
    public void testHandleSQLException() throws SQLException {
        int userId = 1;
        when(mockPreparedStatement.executeQuery())
                .thenThrow(new SQLException("Database error"));

        int result = teamMemberDAO.getTeamIdForUser(userId);

        assertEquals("Should return 0 on SQLException", 0, result);
        verify(mockPreparedStatement).executeQuery();
    }
}