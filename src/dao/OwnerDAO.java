package dao;

import db.DatabaseConnection;
import model.Owner;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class OwnerDAO {
    public static Map<String, Object> findOwnerDataByRNOKPP(String rnokpp) {
        String query = "SELECT id, full_name, contact_info FROM власниктовара WHERE РНОКПП = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, rnokpp);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> ownerData = new HashMap<>();
                ownerData.put("id", rs.getInt("id"));
                ownerData.put("full_name", rs.getString("full_name"));
                ownerData.put("contact_info", rs.getString("contact_info"));
                return ownerData;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Owner findOwnerByRNOKPP(String rnokpp) {
        Map<String, Object> data = findOwnerDataByRNOKPP(rnokpp);
        if (data != null) {
            return new Owner((String) data.get("full_name"), (String) data.get("contact_info"));
        }
        return null;
    }
    public static int findOwnerIdByRNOKPP(String rnokpp) {
        String query = "SELECT id FROM власниктовара WHERE РНОКПП = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, rnokpp);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static int insertNewOwner(String rnokpp, String fullName, String contact) {
        String query = "INSERT INTO власниктовара (РНОКПП, full_name, contact_info) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, rnokpp);
            stmt.setString(2, fullName);
            stmt.setString(3, contact);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static Map<String, Object> getOwnerInfoByDeclarationId(int declarationId) {
        String query = """
        SELECT o.full_name, o.contact_info, o.РНОКПП
        FROM власниктовара o
        JOIN декларація d ON d.idВласникТовара = o.id
        WHERE d.id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, declarationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("full_name", rs.getString("full_name"));
                map.put("contact_info", rs.getString("contact_info"));
                map.put("РНОКПП", rs.getString("РНОКПП"));
                return map;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}