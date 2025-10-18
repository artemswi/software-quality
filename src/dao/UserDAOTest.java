package dao;
import model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    @Test
    void testAuthenticate_ValidCredentials() {
        String username = "sydorov";
        String password = "syd123";
        User user = UserDAO.authenticate(username, password);
        assertNotNull(user, "User should not be null for valid credentials");
        assertEquals("admin", user.getRole(), "User should have the admin role");
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        String username = "invalid";
        String password = "wrongPassword";
        User user = UserDAO.authenticate(username, password);
        assertNull(user, "User should be null for invalid credentials");
    }

    @Test
    void testGenerateLogin() {
        String fullName = "Олександр Коваль";
        String login = UserDAO.generateLogin(fullName);
        System.out.println("Generated login: " + login);
        assertNotNull(login, "Generated login should not be null");
        assertTrue(login.contains("koval"), "Login should resemble the last name from the full name");
    }

    @Test
    void testCreateUserAccount() {
        String login = "testuser";
        String password = "testpassword";
        int employeeId = 1;
        int positionId = UserDAO.getPositionIdByName("admin");
        assertDoesNotThrow(() -> UserDAO.createUserAccount(login, password, employeeId, positionId),
                "Creating user account should not throw any exception");
    }
}