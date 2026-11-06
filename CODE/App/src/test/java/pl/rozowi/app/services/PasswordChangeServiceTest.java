package pl.rozowi.app.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordChangeServiceTest {

    private final PasswordChangeService service = new PasswordChangeService();

    /**
     * Tests validateAndHashPassword when the password and its confirmation are
     * valid and match.
     * Checks that the result is not null and that the hashing is consistent.
     */
    @Test
    public void testValidPasswordChange() {
        String password = "Test1234!";
        String confirm = "Test1234!";
        String hashed = service.validateAndHashPassword(password, confirm);
        assertNotNull(hashed);
        assertEquals(service.hashPassword(password), hashed); 
    }

    /**
     * Tests whether the method throws an exception when the password is empty.
     * This case should be rejected with a validation message.
     */
    @Test
    public void testEmptyPasswordThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.validateAndHashPassword("", "abc")
        );
        assertEquals("Pole hasła nie może być puste!", exception.getMessage());
    }

    /**
     * Tests whether the method reacts properly to a missing password (null).
     * Null should be treated as invalid input.
     */
    @Test
    public void testNullPasswordThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.validateAndHashPassword(null, null)
        );
        assertEquals("Pole hasła nie może być puste!", exception.getMessage());
    }

    /**
     * Tests the case where the password and its confirmation are not the same.
     * The expected result is an exception with an appropriate message.
     */
    @Test
    public void testMismatchedPasswordsThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.validateAndHashPassword("password1", "password2")
        );
        assertEquals("Hasła nie są takie same!", exception.getMessage());
    }

    /**
     * Tests whether hashPassword generates the same hash for two calls with the
     * same password.
     * This shows that the hash is deterministic.
     */
    @Test
    public void testHashPasswordConsistency() {
        String pass = "abc123";
        String hash1 = service.hashPassword(pass);
        String hash2 = service.hashPassword(pass);
        assertEquals(hash1, hash2);
    }

    /**
     * Tests whether password hashing returns a non-empty, valid hash.
     * This guards against the function returning null or an empty string.
     */
    @Test
    public void testHashPasswordNotNull() {
        String pass = "secure";
        String hashed = service.hashPassword(pass);
        assertNotNull(hashed); 
        assertTrue(hashed.length() > 0); 
    }
}