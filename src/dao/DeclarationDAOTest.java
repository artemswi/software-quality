package dao;
import org.junit.jupiter.api.*;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
class DeclarationDAOTest {

    @Test
    void testInsertDeclaration() {
        int ownerId = 1;
        int employeeId = 1;
        double customsValue = 1500.50;
        String direction = "Експорт";
        String fromCountry = "Україна";
        String toCountry = "США";
        String transport = "Автомобіль";

        int newDeclarationId = DeclarationDAO.insertDeclaration(
                ownerId, employeeId, customsValue, direction, fromCountry, toCountry, transport
        );
        assertTrue(newDeclarationId > 0, "Declaration ID should be greater than 0");
    }

    @Test
    void testUpdateDeclarationStatus() {
        int declarationId = 11;
        String newStatus = "Прийнято";
        DeclarationDAO.updateDeclarationStatus(declarationId, newStatus);
        Map<String, Object> declarationData = DeclarationDAO.getDeclarationBasicInfo(declarationId);
        assertNotNull(declarationData, "Declaration data should exist.");
        assertEquals(newStatus, declarationData.get("status").toString(), "Status should be updated correctly");
    }
}