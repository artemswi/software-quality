package ui;

import dao.UserDAO;
import model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;

public class LoginFrameTest {

    @Test
    void testEmptyFieldsValidation() {
        LoginFrame loginFrame = new LoginFrame();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        usernameField.setText("");
        passwordField.setText("");

        boolean isEmpty = loginFrame.isInputFieldEmpty(usernameField.getText(), new String(passwordField.getPassword()));
        assertTrue(isEmpty, "Validation should detect empty fields");
    }

    @Test
    void testSuccessfulLogin() {
        String username = "sydorov";
        String password = "syd123";

        User user = UserDAO.authenticate(username, password);
        assertNotNull(user, "User should not be null for valid credentials");
        assertEquals("admin", user.getRole(), "The role of logged in user should be admin");
    }

    @Test
    void testInvalidLogin() {
        String username = "wrongUser";
        String password = "wrongPassword";

        User user = UserDAO.authenticate(username, password);

        assertNull(user, "User should be null for invalid credentials");
    }
}

