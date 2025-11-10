package ui;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class TariffUITest {

    private TariffUI tariffUI;

    @BeforeEach
    void setUp() {
        tariffUI = new TariffUI();
    }

    @Test
    void testValidTariff_EC1_MaxBoundary() {
        assertDoesNotThrow(() -> {
            boolean isValid = tariffUI.validateTariff("2.0");
            assertTrue(isValid, "Тариф '2.0' має бути валідним");
        });
    }

    @Test
    void testValidTariff_EC2_BeforeMaxBoundary() {
        assertDoesNotThrow(() -> {
            boolean isValid = tariffUI.validateTariff("1.99");
            assertTrue(isValid, "Тариф '1.99' має бути валідним");
        });
    }

    @Test
    void testValidTariff_EC4_MinBoundary() {
        assertDoesNotThrow(() -> {
            boolean isValid = tariffUI.validateTariff("1.0");
            assertTrue(isValid, "Тариф '1.0' має бути валідним");
        });
    }

    @Test
    void testValidTariff_EC5_AfterMinBoundary() {
        assertDoesNotThrow(() -> {
            boolean isValid = tariffUI.validateTariff("1.01");
            assertTrue(isValid, "Тариф '1.01' має бути валідним");
        });
    }

    @Test
    void testInvalidTariff_EC3_AfterMaxBoundary() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tariffUI.validateTariff("2.01");
        });
        assertEquals("Тариф повинен бути меншим за 2", exception.getMessage());
    }

    @Test
    void testInvalidTariff_EC6_BeforeMinBoundary() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tariffUI.validateTariff("0.99");
        });
        assertEquals("Тариф повинен бути більшим за 1", exception.getMessage());
    }

    @Test
    void testInvalidTariff_NonNumeric() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tariffUI.validateTariff("abc");
        });
        assertEquals("Тариф повинен бути числовим значенням", exception.getMessage());
    }
}