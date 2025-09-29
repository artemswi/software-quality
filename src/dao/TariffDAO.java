package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TariffDAO {

    public static double getRateByCategory(String categoryName) {
        return getRate("тариф_категорія", "name_category", categoryName);
    }

    public static double getRateByDirection(String directionName) {
        return getRate("тариф_напрямок", "type", directionName);
    }

    public static double getRateByCountry(String countryName) {
        return getRate("тариф_країна", "name_country", countryName);
    }

    private static double getRate(String table, String column, String name) {
        String query = "SELECT rate FROM " + table + " WHERE " + column + " = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("rate");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}
