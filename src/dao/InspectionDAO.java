package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class InspectionDAO {

    public static void saveInspection(int declarationId, int employeeId, String inspectionStatus) {
        String query = "INSERT INTO оглядтовара (idПрацівника, статус_огляду, idДекларації, дата) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            stmt.setString(2, inspectionStatus);
            stmt.setInt(3, declarationId);
            stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
