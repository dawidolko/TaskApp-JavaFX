package pl.rozowi.app.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.Project;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for ProjectDAO, which handles project operations in the
 * database.
 */
public class ProjectDAOTest {

    private Connection connection;
    private ProjectDAO projectDAO;

    /**
     * Sets up the test environment before each test.
     * Creates a temporary in-memory H2 database and the tables needed to test
     * project operations.
     */
    @Before
    public void setUp() throws Exception {
        DatabaseManager.setTestUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        connection = DatabaseManager.getConnection();

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE projects ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "project_name VARCHAR(255),"
                    + "description VARCHAR(1024),"
                    + "start_date DATE,"
                    + "end_date DATE,"
                    + "manager_id INT"
                    + ")");
            stmt.execute("CREATE TABLE tasks (id INT AUTO_INCREMENT PRIMARY KEY, project_id INT)");
            stmt.execute("CREATE TABLE teams (id INT AUTO_INCREMENT PRIMARY KEY, project_id INT)");
            stmt.execute("CREATE TABLE task_assignments (task_id INT)");
            stmt.execute("CREATE TABLE task_activity (task_id INT)");
            stmt.execute("CREATE TABLE task_activities (task_id INT)");
            stmt.execute("CREATE TABLE team_members (team_id INT)");
        }

        projectDAO = new ProjectDAO();
    }

    /**
     * Cleans up resources after each test.
     * Drops all tables from the database and closes the connection.
     */
    @After
    public void tearDown() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
        }
        connection.close();
        DatabaseManager.setTestUrl(null);
    }

    /**
     * Tests adding a project to the database and reading it back with
     * getProjectById.
     * Verifies that all project fields are saved and read correctly.
     */
    @Test
    public void testInsertAndGetById() throws Exception {
        Project p = new Project();
        p.setName("Test Project");
        p.setDescription("Description");
        p.setStartDate(LocalDate.of(2023, 1, 1));
        p.setEndDate(LocalDate.of(2023, 12, 31));
        p.setManagerId(100);

        assertTrue(projectDAO.insertProject(p));
        assertTrue(p.getId() > 0);

        Project fetched = projectDAO.getProjectById(p.getId());
        assertNotNull(fetched);
        assertEquals(p.getName(), fetched.getName());
        assertEquals(p.getDescription(), fetched.getDescription());
        assertEquals(p.getStartDate(), fetched.getStartDate());
        assertEquals(p.getEndDate(), fetched.getEndDate());
        assertEquals(p.getManagerId(), fetched.getManagerId());
    }

    /**
     * Tests getAllProjects, which should return all projects stored in the
     * database.
     * Adds three test projects and verifies that all of them are returned.
     */
    @Test
    public void testGetAllProjects() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Project p = new Project();
            p.setName("P" + i);
            p.setDescription("Desc" + i);
            p.setStartDate(LocalDate.of(2023, i, 1));
            p.setEndDate(LocalDate.of(2023, i, 10));
            p.setManagerId(i);
            projectDAO.insertProject(p);
        }

        List<Project> all = projectDAO.getAllProjects();
        assertEquals(3, all.size());
    }

    /**
     * Tests updating project data in the database.
     * Creates a project, changes its values, then verifies that the changes were
     * saved correctly in the database.
     */
    @Test
    public void testUpdateProject() throws Exception {
        Project p = new Project();
        p.setName("Old");
        p.setDescription("Old Desc");
        p.setStartDate(LocalDate.of(2023, 1, 1));
        p.setEndDate(LocalDate.of(2023, 6, 1));
        p.setManagerId(1);
        projectDAO.insertProject(p);

        p.setName("New");
        p.setDescription("New Desc");
        p.setStartDate(LocalDate.of(2023, 2, 1));
        p.setEndDate(LocalDate.of(2023, 7, 1));
        p.setManagerId(2);
        assertTrue(projectDAO.updateProject(p));

        Project updated = projectDAO.getProjectById(p.getId());
        assertEquals("New", updated.getName());
        assertEquals(2, updated.getManagerId());
    }

    /**
     * Tests getProjectsForManager, which should return only the projects
     * assigned to a specific manager.
     * Adds projects assigned to different managers and verifies that filtering
     * by manager ID works correctly.
     */
    @Test
    public void testGetProjectsForManager() throws Exception {
        for (int i = 0; i < 2; i++) {
            Project p = new Project();
            p.setName("M10-P" + i);
            p.setDescription("x");
            p.setStartDate(LocalDate.now());
            p.setEndDate(LocalDate.now());
            p.setManagerId(10);
            projectDAO.insertProject(p);
        }
        Project p = new Project();
        p.setName("M20");
        p.setDescription("x");
        p.setStartDate(LocalDate.now());
        p.setEndDate(LocalDate.now());
        p.setManagerId(20);
        projectDAO.insertProject(p);

        List<Project> mgr10 = projectDAO.getProjectsForManager(10);
        assertEquals(2, mgr10.size());
    }
}