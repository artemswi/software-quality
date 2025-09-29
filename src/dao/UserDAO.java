package dao;

import db.DatabaseConnection;
import model.User;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static db.DatabaseConnection.getConnection;

public class UserDAO {
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    public static List<String> getAvailablePositionNames() {
        List<String> availablePositions = new ArrayList<>();

        String query = """
        SELECT p.name
        FROM посада p
        LEFT JOIN (
            SELECT idПосада, COUNT(*) AS active_count
            FROM працівникпосада
            WHERE termination_date IS NULL
            GROUP BY idПосада
        ) AS active ON p.id = active.idПосада
        WHERE COALESCE(active.active_count, 0) < p.number
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                availablePositions.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availablePositions;
    }


    public static User authenticate(String username, String password) {
        String query = """
        SELECT u.username, u.password, u.employee_id, pos.name AS position_name
        FROM users u
        JOIN працівник pr ON u.employee_id = pr.id
        JOIN працівникпосада pp ON pp.idПрацівник = pr.id
        JOIN посада pos ON pos.id = pp.idПосада
        WHERE u.username = ? AND u.password = ?
          AND pp.termination_date IS NULL
    """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("position_name"),
                        rs.getInt("employee_id"),
                        rs.getString("position_name")
                );
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error authenticating user", e);
        }
        return null;
    }

    public static void updateEmployeeField(String fullName, String column, String newValue) {
        String query = "UPDATE працівник SET " + column + " = ? WHERE full_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newValue);
            stmt.setString(2, fullName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<String> getAllEmployeeNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT full_name FROM працівник";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("full_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



    public static Map<String, String> getEmployeeInfoByName(String fullName) {
        Map<String, String> info = new LinkedHashMap<>();
        String query = "SELECT id, full_name, birthday, sex, contact_info, `rank` FROM працівник WHERE full_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, fullName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                info.put("ID", String.valueOf(rs.getInt("id")));
                info.put("ПІБ", rs.getString("full_name"));
                info.put("Дата народження", rs.getString("birthday"));
                info.put("Стать", rs.getString("sex"));
                info.put("Контактна інформація", rs.getString("contact_info"));
                info.put("Посада", rs.getString("rank"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }



    public static boolean insertScheduleEntry(String fullName, String day, String start, String end, String status) {
        String query = """
        INSERT INTO графікроботи (work_day, start_time, end_time, status)
        VALUES (?, ?, ?, ?)
    """;

        String linkQuery = """
        INSERT INTO працівникграфікроботи (idПрацівник, idГрафікРоботи)
        VALUES (?, ?)
    """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt1.setString(1, day);
                stmt1.setString(2, start);
                stmt1.setString(3, end);
                stmt1.setString(4, status);
                stmt1.executeUpdate();

                ResultSet keys = stmt1.getGeneratedKeys();
                if (keys.next()) {
                    int scheduleId = keys.getInt(1);

                    int employeeId = -1;
                    try (PreparedStatement idStmt = conn.prepareStatement("SELECT id FROM працівник WHERE full_name = ?")) {
                        idStmt.setString(1, fullName);
                        ResultSet rs = idStmt.executeQuery();
                        if (rs.next()) {
                            employeeId = rs.getInt("id");
                        } else {
                            conn.rollback();
                            return false;
                        }
                    }

                    try (PreparedStatement stmt2 = conn.prepareStatement(linkQuery)) {
                        stmt2.setInt(1, employeeId);
                        stmt2.setInt(2, scheduleId);
                        stmt2.executeUpdate();
                    }

                    conn.commit();
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getScheduleByFullName(String fullName) {
        StringBuilder schedule = new StringBuilder();

        String query = """
        SELECT g.work_day, g.start_time, g.end_time, g.status
        FROM працівник p
        JOIN працівникграфікроботи pg ON pg.idПрацівник = p.id
        JOIN графікроботи g ON g.id = pg.idГрафікРоботи
        WHERE p.full_name = ?
        ORDER BY g.id
    """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, fullName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String day = rs.getString("work_day");
                String start = rs.getString("start_time");
                String end = rs.getString("end_time");
                String status = rs.getString("status");

                schedule.append(String.format("• %s (%s): %s – %s%n", day, status, start, end));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Не вдалося отримати графік роботи.";
        }

        return schedule.length() > 0 ? schedule.toString() : "Графік не призначено.";
    }


    public static String getScheduleForUser(String username) {
        StringBuilder schedule = new StringBuilder();

        String query = """
            SELECT g.work_day, g.start_time, g.end_time, g.status
            FROM users u
            JOIN працівникграфікроботи pg ON pg.idПрацівник = u.employee_id
            JOIN графікроботи g ON g.id = pg.idГрафікРоботи
            WHERE u.username = ?
            ORDER BY g.id
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String day = rs.getString("work_day");
                String start = rs.getString("start_time");
                String end = rs.getString("end_time");
                String status = rs.getString("status");

                schedule.append(String.format("• %s (%s): %s – %s%n", day, status, start, end));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Не вдалося отримати графік роботи.";
        }

        return schedule.length() > 0 ? schedule.toString() : "Графік не призначено.";
    }

    public static int insertEmployee(String fullName, String birthday, String sex, String contactInfo, String rank) {
        String query = "INSERT INTO працівник (full_name, birthday, sex, contact_info, `rank`) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fullName);
            stmt.setDate(2, Date.valueOf(birthday));
            stmt.setString(3, sex);
            stmt.setString(4, contactInfo);
            stmt.setString(5, rank);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }


    public static void assignPositionToEmployee(int employeeId, String positionName) {
        String query = """
        INSERT INTO працівникпосада (hire_date, salery, experience, idПосада, idПрацівник)
        SELECT CURDATE(), p.hourly_rate, 0, p.id, ?
        FROM посада p
        WHERE p.name = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            stmt.setString(2, positionName);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String generateLogin(String fullName) {
        String base = fullName.toLowerCase().replaceAll("[^a-zа-я0-9]", "").substring(0, Math.min(6, fullName.length()));
        int randomNum = new Random().nextInt(9000) + 1000;
        return base + randomNum;
    }

    public static String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return password.toString();
    }

    public static void createUserAccount(String login, String password, int employeeId, int positionId) {
        String query = "INSERT INTO users (username, password, employee_id, position_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setInt(3, employeeId);
            stmt.setInt(4, positionId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getPositionIdByName(String positionName) {
        String query = "SELECT id FROM посада WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, positionName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}