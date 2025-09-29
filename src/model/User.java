package model;

public class User {
    private String username;
    private String password;
    private String role;
    private int employeeId;
    private String positionName;

    public User(String username, String password,String role, int employeeId, String positionName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
        this.positionName = positionName;
    }

    public int getEmployeeId() {
        return employeeId;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

}
