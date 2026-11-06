package pl.rozowi.app.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.Settings;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Unit tests for SettingsDAO, which handles user settings operations in the
 * database.
 */
public class SettingsDAOTest {

    private Connection connection;
    private SettingsDAO settingsDAO;

    /**
     * Sets up the test environment before each test.
     * Creates a temporary in-memory H2 database and a settings table with one
     * sample record for the user with ID 1.
     */
    @Before
    public void setUp() throws Exception {
        DatabaseManager.setTestUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        connection = DatabaseManager.getConnection();

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE settings ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "user_id INT NOT NULL,"
                    + "theme VARCHAR(255),"
                    + "default_view VARCHAR(255),"
                    + "last_password_change TIMESTAMP"
                    + ")");

            stmt.execute("INSERT INTO settings (user_id, theme, default_view, last_password_change) "
                    + "VALUES (1, 'Dark', 'Dashboard', '2023-01-01 10:00:00')");
        }

        settingsDAO = new SettingsDAO();
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
     * Tests getSettingsByUserId for existing settings.
     * Should return the correct Settings object for the user with ID 1.
     */
    @Test
    public void testGetSettingsByUserId_found() {
        Settings s = settingsDAO.getSettingsByUserId(1);

        assertNotNull(s);
        assertEquals(1, s.getId());
        assertEquals(1, s.getUserId());
        assertEquals("Dark", s.getTheme());
        assertEquals("Dashboard", s.getDefaultView());
        assertNotNull(s.getLastPasswordChange());
    }

    /**
     * Tests getSettingsByUserId for non-existent settings.
     * Should return null for a user who has no saved settings.
     */
    @Test
    public void testGetSettingsByUserId_notFound() {
        Settings s = settingsDAO.getSettingsByUserId(99);

        assertNull(s);
    }

    /**
     * Tests adding new settings to the database.
     * Creates settings for the user with ID 5 and verifies that they were saved
     * correctly by reading them back.
     */
    @Test
    public void testInsertSettings_success() {
        Settings in = new Settings();
        in.setUserId(5);
        in.setTheme("Light");
        in.setDefaultView("Tasks");
        in.setLastPasswordChange(Timestamp.valueOf(LocalDateTime.now()));

        boolean ok = settingsDAO.insertSettings(in);

        assertTrue(ok);

        Settings inserted = settingsDAO.getSettingsByUserId(5);
        assertNotNull(inserted);
        assertEquals("Light", inserted.getTheme());
        assertEquals("Tasks", inserted.getDefaultView());
    }

    /**
     * Tests the reaction to adding settings for a user who already has settings
     * in the database. After a unique constraint is added on user_id, adding
     * settings again should fail.
     */
    @Test
    public void testInsertSettings_failure() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE settings ADD CONSTRAINT unique_user UNIQUE (user_id)");
        }

        Settings in1 = new Settings();
        in1.setUserId(7);
        in1.setTheme("Dark");
        boolean ok1 = settingsDAO.insertSettings(in1);
        assertTrue(ok1);

        Settings in2 = new Settings();
        in2.setUserId(7);
        in2.setTheme("Light");

        boolean ok2 = settingsDAO.insertSettings(in2);

        assertFalse(ok2);
    }

    /**
     * Tests updating existing user settings.
     * First adds settings for the user with ID 3, then updates them and verifies
     * that the changes were saved correctly.
     */
    @Test
    public void testUpdateSettings_success() {
        Settings in = new Settings();
        in.setUserId(3);
        in.setTheme("Light");
        in.setDefaultView("Tasks");
        settingsDAO.insertSettings(in);

        Settings update = new Settings();
        update.setUserId(3);
        update.setTheme("Dark");
        update.setDefaultView("Home");

        boolean ok = settingsDAO.updateSettings(update);

        assertTrue(ok);

        Settings updated = settingsDAO.getSettingsByUserId(3);
        assertNotNull(updated);
        assertEquals("Dark", updated.getTheme());
        assertEquals("Home", updated.getDefaultView());
    }

    /**
     * Tests deleting user settings from the database.
     * Adds settings for the user with ID 8, deletes them, then verifies that they
     * were actually deleted.
     */
    @Test
    public void testDeleteSettings_success() {
        Settings in = new Settings();
        in.setUserId(8);
        in.setTheme("Light");
        settingsDAO.insertSettings(in);

        boolean ok = settingsDAO.deleteSettings(8);

        assertTrue(ok);

        Settings deleted = settingsDAO.getSettingsByUserId(8);
        assertNull(deleted);
    }

    /**
     * Tests updating the user's default view.
     * Creates settings with the default view "Tasks", updates it to "Dashboard"
     * and verifies that the change was saved correctly.
     */
    @Test
    public void testUpdateDefaultView_success() {
        Settings in = new Settings();
        in.setUserId(4);
        in.setTheme("Light");
        in.setDefaultView("Tasks");
        settingsDAO.insertSettings(in);

        boolean ok = settingsDAO.updateDefaultView(4, "Dashboard");

        assertTrue(ok);

        Settings updated = settingsDAO.getSettingsByUserId(4);
        assertNotNull(updated);
        assertEquals("Dashboard", updated.getDefaultView());
    }

    /**
     * Tests updating the last password change date for a user who already has
     * a settings record. Verifies that the new date is later than or equal to the
     * date before the method call.
     */
    @Test
    public void testUpdateLastPasswordChange_existing() {
        Settings in = new Settings();
        in.setUserId(2);
        settingsDAO.insertSettings(in);

        Timestamp before = new Timestamp(System.currentTimeMillis());

        boolean ok = settingsDAO.updateLastPasswordChange(2);

        assertTrue(ok);

        Settings updated = settingsDAO.getSettingsByUserId(2);
        assertNotNull(updated);
        assertNotNull(updated.getLastPasswordChange());

        assertTrue(updated.getLastPasswordChange().after(before) ||
                updated.getLastPasswordChange().equals(before));
    }

    /**
     * Tests updating the last password change date for a user who has no settings
     * record yet.
     * The method should create a new record with the password change date.
     */
    @Test
    public void testUpdateLastPasswordChange_newRecord() {
        boolean ok = settingsDAO.updateLastPasswordChange(7);

        assertTrue(ok);

        Settings created = settingsDAO.getSettingsByUserId(7);
        assertNotNull(created);
        assertNotNull(created.getLastPasswordChange());
    }
}