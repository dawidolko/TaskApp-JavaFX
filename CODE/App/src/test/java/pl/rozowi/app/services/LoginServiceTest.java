package pl.rozowi.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.rozowi.app.dao.TeamMemberDAO;
import pl.rozowi.app.dao.UserDAO;
import pl.rozowi.app.models.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    @Mock
    private UserDAO userDaoMock;

    @Mock
    private TeamMemberDAO teamMemberDaoMock;

    private LoginService loginService;

    AutoCloseable closeable;

    /**
     * Initializes the mocks before each test and creates the LoginService instance
     * under test.
     */
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        loginService = new LoginService(userDaoMock, teamMemberDaoMock);
    }

    /**
     * Closes the mock context after each test to avoid memory leaks.
     */
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    /**
     * Tests successful user authentication with valid login credentials.
     * Uses a valid SHA-256 hash of the password "secret".
     */
    @Test
    void testAuthenticate_validCredentials() {
        String email = "john@domain.com";
        String rawPassword = "secret";

        User user = new User();
        user.setId(123);
        user.setEmail(email);
        user.setPassword("2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b");

        when(userDaoMock.getUserByEmail(email)).thenReturn(user);

        User result = loginService.authenticate(email, rawPassword);

        assertNotNull(result, "Użytkownik powinien zostać poprawnie zalogowany, nie może być null.");
        assertEquals(123, result.getId(), "Zwrócony użytkownik powinien mieć prawidłowe ID.");
    }

    /**
     * Tests the case where an invalid email is given (the user does not exist).
     * Expects null to be returned and the DAO method to be called.
     */
    @Test
    void testAuthenticate_invalidCredentials() {
        String email = "wrong@domain.com";
        String rawPassword = "pass";

        when(userDaoMock.getUserByEmail(email)).thenReturn(null);

        User result = loginService.authenticate(email, rawPassword);

        assertNull(result, "Dla niepoprawnych danych logowania metoda powinna zwrócić null.");
        verify(userDaoMock).getUserByEmail(email);
    }

    /**
     * Tests whether LoginService correctly fetches the user's team ID.
     */
    @Test
    void testFindTeamIdForUser() {
        when(teamMemberDaoMock.getTeamIdForUser(123)).thenReturn(10);
        int teamId = loginService.findTeamIdForUser(123);

        assertEquals(10, teamId, "Metoda powinna zwrócić poprawny ID zespołu.");
        verify(teamMemberDaoMock).getTeamIdForUser(123);
    }
}
