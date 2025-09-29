package dao;

import db.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ReferenceDataDAO {

    private static final Map<String, TableInfo> TABLE_MAP = Map.of(
            "країни", new TableInfo("тариф_країна", "name_country"),
            "категорії", new TableInfo("тариф_категорія", "name_category"),
            "напрямки", new TableInfo("тариф_напрямок", "type")
    );

    public static double getRateForItem(String type, String name) {
        TableInfo info = TABLE_MAP.get(type.toLowerCase());
        if (info == null) {
            System.out.println("Невідомий тип елемента: " + type);
            return 0.0;
        }

        String sql = "SELECT rate FROM " + info.table + " WHERE " + info.column + " = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("rate");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static void updateRate(String type, String name, double newRate) {
        TableInfo info = TABLE_MAP.get(type.toLowerCase());
        if (info == null) {
            System.out.println("Невідомий тип тарифу: " + type);
            return;
        }

        String sql = "UPDATE " + info.table + " SET rate = ? WHERE " + info.column + " = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newRate);
            stmt.setString(2, name);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("Жоден запис не оновлено. Перевірте назву.");
            } else {
                System.out.println("Тариф оновлено успішно.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getItemsByType(String type) {
        TableInfo info = TABLE_MAP.get(type.toLowerCase());
        if (info == null) {
            System.out.println("Невідомий тип: " + type);
            return Collections.emptyList();
        }
        return getSimpleColumn(info.table, info.column);
    }

    private static List<String> getSimpleColumn(String table, String column) {
        List<String> list = new ArrayList<>();
        String query = "SELECT " + column + " FROM " + table;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString(column));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static class TableInfo {
        String table;
        String column;

        TableInfo(String table, String column) {
            this.table = table;
            this.column = column;
        }
    }
}
