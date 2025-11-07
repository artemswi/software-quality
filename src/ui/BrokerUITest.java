package ui;

import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

public class BrokerUITest {

    private BrokerUI brokerUI;

    @BeforeEach
    void setUp() {
        brokerUI = new BrokerUI("testUsername", 1);
    }

    @Test
    void testValidQuantityInput() {
        JTextField quantityField = new JTextField();
        quantityField.setText("50");
        String inputValue = quantityField.getText();
        assertDoesNotThrow(() -> Integer.parseInt(inputValue));
    }

    @Test
    void testInvalidQuantityInput_Empty() {
        JTextField quantityField = new JTextField();
        quantityField.setText("");

        String inputValue = quantityField.getText();
        Exception exception = assertThrows(NumberFormatException.class, () -> Integer.parseInt(inputValue));
        assertNotNull(exception, "Виняток має бути викликано для порожнього введення");
    }

    @Test
    void testInvalidQuantityInput_Negative() {
        JTextField quantityField = new JTextField();
        quantityField.setText("-5");
        String inputValue = quantityField.getText();
        assertDoesNotThrow(() -> Integer.parseInt(inputValue));
    }

    @Test
    void testInvalidQuantityInput_NonNumeric() {
        JTextField quantityField = new JTextField();
        quantityField.setText("abc");

        String inputValue = quantityField.getText();
        Exception exception = assertThrows(NumberFormatException.class, () -> Integer.parseInt(inputValue));
        assertNotNull(exception, "Виняток має бути викликано для текстового значення");
    }

    @Test
    void testInvalidQuantityInput_LargeDecimalValue() {
        JTextField quantityField = new JTextField();
        quantityField.setText("100.12");

        String inputValue = quantityField.getText();
        Exception exception = assertThrows(NumberFormatException.class, () -> Integer.parseInt(inputValue));
        assertNotNull(exception, "Виняток має бути викликано для дробового числа, яке не може бути перетворено на ціле число");
    }

    @AfterEach
    void tearDown() {
        brokerUI.dispose();
    }
}