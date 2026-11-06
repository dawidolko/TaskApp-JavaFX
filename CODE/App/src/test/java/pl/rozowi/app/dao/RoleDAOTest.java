package pl.rozowi.app.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.Role;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for RoleDAO, which handles user role operations in the
 * database.
 */
public class RoleDAOTest {

    private Connection connection;
    private RoleDAO roleDAO;

    /**
     * Sets up the test environment before each test.
     * Creates a temporary in-memory H2 database and a roles table with two sample
     * roles: Admin and User.
     */
    @Before
    public void setUp() throws Exception {
        DatabaseManager.setTestUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        connection = DatabaseManager.getConnection();

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE roles ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "role_name VARCHAR(255),"
                    + "permissions VARCHAR(1024)"
                    + ")");

            stmt.execute("INSERT INTO roles (role_name, permissions) VALUES ('Admin', 'ALL')");
            stmt.execute("INSERT INTO roles (role_name, permissions) VALUES ('User', 'READ')");
        }

        roleDAO = new RoleDAO();
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
     * Tests getAllRoles, which should return all roles from the database.
     * Verifies that all role fields are read correctly, including IDs, names and
     * permissions.
     */
    @Test
    public void testGetAllRoles() {
        List<Role> roles = roleDAO.getAllRoles();

        assertNotNull(roles);
        assertEquals(2, roles.size());

        Role first = roles.get(0);
        assertEquals(1, first.getId());
        assertEquals("Admin", first.getRoleName());
        assertEquals("ALL", first.getPermissions());

        Role second = roles.get(1);
        assertEquals(2, second.getId());
        assertEquals("User", second.getRoleName());
        assertEquals("READ", second.getPermissions());
    }

    /**
     * Tests getRoleByName for an existing role.
     * Should return the correct Role object for the name "Admin".
     */
    @Test
    public void testGetRoleByName_found() {
        Role role = roleDAO.getRoleByName("Admin");

        assertNotNull(role);
        assertEquals(1, role.getId());
        assertEquals("Admin", role.getRoleName());
    }

    /**
     * Tests getRoleByName for a non-existent role.
     * Should return null when no role with the given name exists.
     */
    @Test
    public void testGetRoleByName_notFound() {
        Role role = roleDAO.getRoleByName("DoesNotExist");

        assertNull(role);
    }

    /**
     * Tests adding a new role to the database.
     * Creates the role "Guest", adds it to the database and verifies that it was
     * saved correctly by reading it back by name.
     */
    @Test
    public void testInsertRole_success() {
        Role r = new Role();
        r.setRoleName("Guest");
        r.setPermissions("GUEST_PERMS");

        boolean inserted = roleDAO.insertRole(r);

        assertTrue(inserted);

        Role insertedRole = roleDAO.getRoleByName("Guest");
        assertNotNull(insertedRole);
        assertEquals("Guest", insertedRole.getRoleName());
    }

    /**
     * Tests the reaction to adding a role whose name already exists.
     * First adds the role "Guest" directly to the database, adding a unique
     * constraint, then tries to add another role with the same name through
     * RoleDAO.
     * insertRole should return false, signaling that the operation failed.
     */
    @Test
    public void testInsertRole_failure() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE roles ADD CONSTRAINT unique_role_name UNIQUE (role_name)");
            stmt.execute("INSERT INTO roles (role_name) VALUES ('Guest')");
        }

        Role r = new Role();
        r.setRoleName("Guest");

        boolean inserted = roleDAO.insertRole(r);

        assertFalse(inserted);
    }
}