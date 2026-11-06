package pl.rozowi.app.dao;

import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.Team;
import pl.rozowi.app.models.User;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TeamDAO using Mockito.
 * Tests operations on teams in the database, using mocks to simulate the
 * behavior of the database layer.
 */
public class TeamDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private Statement mockStatement;

    private static MockedStatic<DatabaseManager> mockStaticDatabaseManager;

    private TeamDAO teamDAO;

    /**
     * Initializes static mocks before all tests run.
     * Creates a static mock of DatabaseManager that is used in all tests.
     */
    @BeforeClass
    public static void initStaticMock() throws SQLException {
        mockStaticDatabaseManager = mockStatic(DatabaseManager.class);
    }

    /**
     * Cleans up static mocks after all tests finish.
     * Closes the DatabaseManager static mock to avoid memory leaks.
     */
    @AfterClass
    public static void closeStaticMock() {
        mockStaticDatabaseManager.close();
    }

    /**
     * Initializes the test environment before each test.
     * Initializes the mocks, configures their behavior and creates an instance of
     * the tested TeamDAO class.
     */
    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);

        mockStaticDatabaseManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        teamDAO = new TeamDAO();
    }

    /**
     * Tests getAllTeams.
     * Simulates two teams returned from the database and verifies that they were
     * correctly converted to Team objects.
     */
    @Test
    public void testGetAllTeams() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("team_name")).thenReturn("Team A", "Team B");
        when(mockResultSet.getInt("project_id")).thenReturn(1, 2);

        List<Team> results = teamDAO.getAllTeams();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(1, results.get(0).getId());
        assertEquals(2, results.get(1).getId());
        assertEquals("Team A", results.get(0).getTeamName());
        assertEquals("Team B", results.get(1).getTeamName());

        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getTeamById for an existing team.
     * Simulates finding a team with the given ID and verifies that it was
     * correctly converted to a Team object.
     */
    @Test
    public void testGetTeamById_teamExists() throws SQLException {
        int teamId = 1;
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(teamId);
        when(mockResultSet.getString("team_name")).thenReturn("Team A");
        when(mockResultSet.getInt("project_id")).thenReturn(1);

        Team result = teamDAO.getTeamById(teamId);

        assertNotNull(result);
        assertEquals(teamId, result.getId());
        assertEquals("Team A", result.getTeamName());
        assertEquals(1, result.getProjectId());

        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getTeamById for a non-existent team.
     * Simulates no team with the given ID and verifies that the method correctly
     * returns null in that case.
     */
    @Test
    public void testGetTeamById_teamDoesNotExist() throws SQLException {
        int teamId = 999;
        when(mockResultSet.next()).thenReturn(false);

        Team result = teamDAO.getTeamById(teamId);

        assertNull(result);

        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getTeamNameById for an existing team.
     * Simulates finding the name of the team with the given ID and verifies that
     * it was returned correctly.
     */
    @Test
    public void testGetTeamNameById() throws SQLException {
        int teamId = 1;
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("team_name")).thenReturn("Team A");

        String result = teamDAO.getTeamNameById(teamId);

        assertEquals("Team A", result);

        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getTeamNameById for a non-existent team.
     * Simulates no team with the given ID and verifies that the method correctly
     * returns an en dash symbol in that case.
     */
    @Test
    public void testGetTeamNameById_teamDoesNotExist() throws SQLException {
        int teamId = 999;
        when(mockResultSet.next()).thenReturn(false);

        String result = teamDAO.getTeamNameById(teamId);

        assertEquals("–", result);

        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests insertTeam on success.
     * Simulates successfully adding a team to the database, including ID
     * generation, and verifies that the operation is correct.
     */
    @Test
    public void testInsertTeam_success() throws SQLException {
        Team newTeam = new Team();
        newTeam.setTeamName("Team A");
        newTeam.setProjectId(1);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        boolean result = teamDAO.insertTeam(newTeam);

        assertTrue(result);
        assertEquals(1, newTeam.getId());

        verify(mockPreparedStatement).setString(1, newTeam.getTeamName());
        verify(mockPreparedStatement).setInt(2, newTeam.getProjectId());
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).getGeneratedKeys();
    }

    /**
     * Tests updateTeam on success.
     * Simulates a successful update of team data in the database and verifies
     * the passed parameters.
     */
    @Test
    public void testUpdateTeam_success() throws SQLException {
        Team team = new Team();
        team.setId(1);
        team.setTeamName("Updated");
        team.setProjectId(2);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = teamDAO.updateTeam(team);

        assertTrue(result);

        verify(mockPreparedStatement).setString(1, team.getTeamName());
        verify(mockPreparedStatement).setInt(2, team.getProjectId());
        verify(mockPreparedStatement).setInt(3, team.getId());
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests getTeamMembers.
     * Simulates fetching team members from the database and verifies that they
     * are correctly converted to User objects.
     */
    @Test
    public void testGetTeamMembers() throws SQLException {
        int teamId = 1;
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("name")).thenReturn("John", "Jane");
        when(mockResultSet.getString("last_name")).thenReturn("Doe", "Smith");
        when(mockResultSet.getString("email")).thenReturn("john@example.com", "jane@example.com");
        when(mockResultSet.getInt("role_id")).thenReturn(3, 4);
        when(mockResultSet.getBoolean("is_leader")).thenReturn(true, false);

        List<User> results = teamDAO.getTeamMembers(teamId);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("John", results.get(0).getName());
        assertEquals("Jane", results.get(1).getName());
        assertEquals("Doe", results.get(0).getLastName());
        assertEquals("Smith", results.get(1).getLastName());
        assertEquals("john@example.com", results.get(0).getEmail());
        assertEquals("jane@example.com", results.get(1).getEmail());
        assertEquals(3, results.get(0).getRoleId());
        assertEquals(4, results.get(1).getRoleId());

        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests insertTeamMember on success.
     * Simulates successfully adding a member to a team and verifies that the
     * query parameters were set correctly.
     */
    @Test
    public void testInsertTeamMember_success() throws SQLException {
        int teamId = 1;
        int userId = 1;
        boolean isLeader = true;

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = teamDAO.insertTeamMember(teamId, userId, isLeader);

        assertTrue(result);

        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).setInt(2, userId);
        verify(mockPreparedStatement).setBoolean(3, isLeader);
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests deleteTeamMember on success.
     * Simulates successfully removing a member from a team and verifies that the
     * query parameters were set correctly.
     */
    @Test
    public void testDeleteTeamMember_success() throws SQLException {
        int teamId = 1;
        int userId = 1;

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = teamDAO.deleteTeamMember(teamId, userId);

        assertTrue(result);

        verify(mockPreparedStatement).setInt(1, teamId);
        verify(mockPreparedStatement).setInt(2, userId);
        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests getTeamsForUser.
     * Simulates fetching the teams the given user belongs to and verifies that
     * they are correctly converted to Team objects.
     */
    @Test
    public void testGetTeamsForUser() throws SQLException {
        int userId = 1;
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("team_name")).thenReturn("Team A", "Team B");
        when(mockResultSet.getInt("project_id")).thenReturn(1, 2);

        List<Team> results = teamDAO.getTeamsForUser(userId);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(1, results.get(0).getId());
        assertEquals(2, results.get(1).getId());
        assertEquals("Team A", results.get(0).getTeamName());
        assertEquals("Team B", results.get(1).getTeamName());

        verify(mockPreparedStatement).setInt(1, userId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getTeamsForManager.
     * Simulates fetching teams assigned to projects managed by the given manager
     * and verifies that they are correctly converted to Team objects.
     */
    @Test
    public void testGetTeamsForManager() throws SQLException {
        int managerId = 1;
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("team_name")).thenReturn("Team A", "Team B");
        when(mockResultSet.getInt("project_id")).thenReturn(1, 2);

        List<Team> results = teamDAO.getTeamsForManager(managerId);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(1, results.get(0).getId());
        assertEquals(2, results.get(1).getId());
        assertEquals("Team A", results.get(0).getTeamName());
        assertEquals("Team B", results.get(1).getTeamName());

        verify(mockPreparedStatement).setInt(1, managerId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getTeamsByLeaderId.
     * Simulates fetching the IDs of teams led by the given user and verifies the
     * returned data.
     */
    @Test
    public void testGetTeamsByLeaderId() throws SQLException {
        int leaderId = 1;
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);

        List<Integer> results = teamDAO.getTeamsByLeaderId(leaderId);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(Integer.valueOf(1), results.get(0));
        assertEquals(Integer.valueOf(2), results.get(1));

        verify(mockPreparedStatement).setInt(1, leaderId);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests getTeamsByLeaderIdAsList.
     * Simulates fetching full team objects for teams led by the given user and
     * verifies that the data is correctly converted to Team objects.
     */
    @Test
    public void testGetTeamsByLeaderIdAsList() throws SQLException {
        int leaderId = 1;
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("team_name")).thenReturn("Team A", "Team B");
        when(mockResultSet.getInt("project_id")).thenReturn(1, 2);

        List<Team> results = teamDAO.getTeamsByLeaderIdAsList(leaderId);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(1, results.get(0).getId());
        assertEquals(2, results.get(1).getId());
        assertEquals("Team A", results.get(0).getTeamName());
        assertEquals("Team B", results.get(1).getTeamName());

        verify(mockPreparedStatement).setInt(1, leaderId);
        verify(mockPreparedStatement).executeQuery();
    }
}