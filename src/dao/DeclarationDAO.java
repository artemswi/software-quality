package dao;

import db.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class DeclarationDAO {

    public static List<Integer> getDeclarationIdsForUser(int employeeId) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT id FROM декларація WHERE idПрацівник = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ids;
    }
    public static void updateDeclarationStatus(int declarationId, String newStatus) {
        String query = "UPDATE декларація SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, declarationId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Object[][] getDeclarationById(int id) {
        String query = "SELECT * FROM декларація WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Object[][]{
                        {
                                rs.getInt("id"),
                                rs.getString("operation_type"),
                                rs.getBigDecimal("customs_value"),
                                rs.getString("export_country"),
                                rs.getString("destination_country"),
                                rs.getDate("submission_date"),
                                rs.getString("transport_method"),
                                rs.getString("status"),
                                rs.getInt("idВласникТовара"),
                                rs.getInt("idПрацівник")
                        }
                };
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Object[0][];
    }

    public static String[] getColumnNames() {
        return new String[]{
                "ID","Тип операції", "Митна вартість",
                "Країна експорту", "Країна призначення", "Дата подачі",
                "Транспорт", "Статус", "ID власника", "ID працівника"
        };
    }
    public static int insertDeclaration(
            int ownerId,
            int employeeId,
            double customsValue,
            String direction,
            String fromCountry,
            String toCountry,
            String transport
    ) {
        String query = """
        INSERT INTO декларація (
            operation_type,
            customs_value,
            export_country,
            destination_country,
            submission_date,
            transport_method,
            status,
            idВласникТовара,
            idПрацівник
        ) VALUES (?, ?, ?, ?, CURDATE(), ?, 'В очікуванні', ?, ?)
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, direction);         // operation_type
            stmt.setDouble(2, customsValue);      // customs_value
            stmt.setString(3, fromCountry);       // export_country
            stmt.setString(4, toCountry);         // destination_country
            stmt.setString(5, transport);         // transport_method
            stmt.setInt(6, ownerId);              // idВласникТовара
            stmt.setInt(7, employeeId);           // idПрацівник

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
    public static boolean existsById(int id) {
        String query = "SELECT id FROM декларація WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateStatus(int id, String status) {
        String query = "UPDATE декларація SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Map<String, Object> getDeclarationBasicInfo(int id) {
        String query = """
    SELECT id, operation_type, customs_value, export_country, destination_country,
           submission_date, transport_method, status
    FROM декларація
    WHERE id = ? AND status = 'В очікуванні'
    """;


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("operation_type", rs.getString("operation_type"));
                map.put("customs_value", rs.getBigDecimal("customs_value"));
                map.put("export_country", rs.getString("export_country"));
                map.put("destination_country", rs.getString("destination_country"));
                map.put("submission_date", rs.getDate("submission_date"));
                map.put("transport_method", rs.getString("transport_method"));
                map.put("status", rs.getString("status"));
                return map;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



}
