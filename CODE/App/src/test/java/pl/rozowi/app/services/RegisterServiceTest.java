package pl.rozowi.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.rozowi.app.dao.UserDAO;
import pl.rozowi.app.models.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterServiceTest {

    @Mock
    private UserDAO userDaoMock;

    private RegisterService registerService;

    /**
     * Initializes the mocks and creates the registration service instance before
     * each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        registerService = new RegisterService(userDaoMock);
    }

    /**
     * Tests successful user registration with valid data.
     */
    @Test
    void testRegister_success() {
        String firstName = "Adam";
        String lastName = "Kowalski";
        String email = "ab@cd";
        String password = "pass@123";
        String confirm = "pass@123";

        when(userDaoMock.insertUser(any(User.class))).thenReturn(true);

        RegistrationResult result = registerService.register(firstName, lastName, email, password, confirm);

        assertTrue(result.isSuccess(), "Rejestracja powinna się udać przy poprawnych danych");
        verify(userDaoMock, times(1)).insertUser(any(User.class));
    }

    /**
     * Validation test: the first name should start with a capital letter.
     */
    @Test
    void testRegister_failWrongCaseForFirstName() {
        String firstName = "adam";
        String lastName = "Kowalski";
        String email = "ab@cd";
        String password = "pass@123";
        String confirm = "pass@123";

        RegistrationResult result = registerService.register(firstName, lastName, email, password, confirm);

        assertFalse(result.isSuccess());
        assertEquals("Imię musi zaczynać się od wielkiej litery!", result.getMessage());
        verify(userDaoMock, never()).insertUser(any(User.class));
    }

    /**
     * Email address validation test: must contain an @ sign and at least 2
     * characters before and after it.
     */
    @Test
    void testRegister_failEmailRegex() {
        String firstName = "Adam";
        String lastName = "Kowalski";
        String email = "a@";
        String password = "xyz@";
        String confirm = "xyz@";

        RegistrationResult result = registerService.register(firstName, lastName, email, password, confirm);

        assertFalse(result.isSuccess());
        assertEquals("Email musi zawierać znak '@' z co najmniej dwoma znakami przed i po nim!", result.getMessage());
        verify(userDaoMock, never()).insertUser(any(User.class));
    }

    /**
     * Tests mismatched passwords - the user should not be registered.
     */
    @Test
    void testRegister_failPasswordsDifferent() {
        String firstName = "Adam";
        String lastName = "Kowalski";
        String email = "ab@cd";
        String password = "pass@123";
        String confirm = "pass@999";

        RegistrationResult result = registerService.register(firstName, lastName, email, password, confirm);

        assertFalse(result.isSuccess());
        assertEquals("Hasła nie są takie same!", result.getMessage());
        verify(userDaoMock, never()).insertUser(any(User.class));
    }
}
