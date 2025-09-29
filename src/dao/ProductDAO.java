package dao;

import db.DatabaseConnection;
import java.sql.*;

public class ProductDAO {
    public static int findCategoryIdByName(String categoryName) {
        String query = "SELECT id FROM категоріятовара WHERE category = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Category not found: " + categoryName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving category ID", e);
        }
    }

    public static void insertProduct(String name, String type, String origin, int quantity, double price, double weight, int declarationId) {
        String query = """
            INSERT INTO товар (name, category_id, origin_country, quantity, unit_price, unit_weight, idДекларації)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int categoryId = findCategoryIdByName(type);

            stmt.setString(1, name);
            stmt.setInt(2, categoryId);
            stmt.setString(3, origin);
            stmt.setInt(4, quantity);
            stmt.setDouble(5, price);
            stmt.setDouble(6, weight);
            stmt.setInt(7, declarationId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}