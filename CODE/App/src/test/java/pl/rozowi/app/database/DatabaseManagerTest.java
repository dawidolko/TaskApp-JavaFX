package pl.rozowi.app.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    /**
     * Tests whether a database connection can be established.
     * The connection should not be null and should be open.
     */
    @Test
    void testGetConnection() {
        try (Connection connection = DatabaseManager.getConnection()) {
            assertNotNull(connection, "Połączenie z bazą danych nie powinno być null");
            assertFalse(connection.isClosed(), "Połączenie nie powinno być zamknięte");
        } catch (SQLException e) {
            fail("Nie udało się uzyskać połączenia z bazą: " + e.getMessage());
        }
    }

    /**
     * Tests whether a specific (expected) database/schema exists.
     * This test assumes that the schema `it_task_management` exists; if it does
     * not, the test fails.
     */
    @Test
    void testSchemaExists_ExistingSchema() {
        boolean exists = DatabaseManager.schemaExists("it_task_management");
        assertTrue(exists, "Oczekiwano, że schema it_task_management istnieje w bazie");
    }

    /**
     * Tests whether a non-existent schema is correctly recognized as missing.
     * The name `some_random_schema_123` should be unique for the test to be
     * reliable.
     */
    @Test
    void testSchemaExists_NonExistingSchema() {
        boolean exists = DatabaseManager.schemaExists("some_random_schema_123");
        assertFalse(exists, "Oczekiwano, że schema some_random_schema_123 nie istnieje w bazie");
    }
}
