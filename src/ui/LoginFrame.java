package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;

    public LoginFrame() {
        setTitle("Система митниці - Вхід");
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(33, 47, 60), 0, getHeight(), new Color(44, 62, 80));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBackground(new Color(33, 47, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Вхід до системи митниці", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel userLabel = new JLabel("Логін:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Roboto", Font.PLAIN, 28));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(userLabel, gbc);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Roboto", Font.PLAIN, 28));
        usernameField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        usernameField.setPreferredSize(new Dimension(300, 50));
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Пароль:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Roboto", Font.PLAIN, 28));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Roboto", Font.PLAIN, 28));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        passwordField.setPreferredSize(new Dimension(300, 50));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        showPasswordCheckBox = new JCheckBox("Показати пароль");
        showPasswordCheckBox.setFont(new Font("Roboto", Font.PLAIN, 18));
        showPasswordCheckBox.setForeground(Color.WHITE);
        showPasswordCheckBox.setBackground(new Color(33, 47, 60));
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(showPasswordCheckBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton loginButton = ButtonFactory.createStyledButton(
                "Увійти",
                UIConstants.PRIMARY_COLOR,
                UIConstants.HOVER_COLOR,
                null,
                this::login
        );
        buttonPanel.add(loginButton);

        JButton exitButton = ButtonFactory.createStyledButton(
                "Вихід",
                UIConstants.ERROR_COLOR,
                null,
                null,
                this::exit
        );
        buttonPanel.add(exitButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(new Color(33, 47, 60));

        JLabel topLabel = new JLabel("Проєкт: Митниця", SwingConstants.CENTER);
        topLabel.setFont(new Font("Roboto", Font.BOLD, 56));
        topLabel.setForeground(Color.WHITE);
        topLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        containerPanel.add(topLabel, BorderLayout.NORTH);

        JLabel bottomLabel = new JLabel("Виконав: Черниш Артем АС232", SwingConstants.RIGHT);
        bottomLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        bottomLabel.setForeground(Color.WHITE);
        bottomLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 20));
        containerPanel.add(bottomLabel, BorderLayout.SOUTH);

        containerPanel.add(mainPanel, BorderLayout.CENTER);
        add(containerPanel);

        setVisible(true);
    }

    private boolean isInputFieldEmpty(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, 
                "Логін і пароль не можуть бути порожніми.", 
                "Помилка", 
                JOptionPane.ERROR_MESSAGE
            );
            return true;
        }
        return false;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (isInputFieldEmpty(username, password)) {
            return;
        }

        User user = UserDAO.authenticate(username, password);
        if (user != null) {
            openUIForRole(user.getRole(), user);
            setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Невірний логін або пароль.", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exit() {
        int confirm = JOptionPane.showConfirmDialog(this, "Ви дійсно хочете вийти?", "Підтвердження", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void openUIForRole(String role, User user) {
        switch (role.toLowerCase()) {
            case "admin" -> new AdminUI(user.getUsername(), user.getEmployeeId()).setVisible(true);
            case "broker" -> new BrokerUI(user.getUsername(), user.getEmployeeId()).setVisible(true);
            case "inspector" -> new InspectorUI(user.getUsername(), user.getEmployeeId()).setVisible(true);
            default -> JOptionPane.showMessageDialog(this, "Невідома роль.", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }
}