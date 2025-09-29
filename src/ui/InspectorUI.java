package ui;

import dao.DeclarationDAO;
import dao.UserDAO;
import dao.InspectionDAO;
import dao.OwnerDAO;
import javax.swing.table.DefaultTableModel;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Map;

public class InspectorUI extends JFrame {
    private final String currentUsername;
    private final int currentEmployeeId;

    private final JPanel contentPanel;
    private final JPanel schedulePanel;
    private final JLabel scheduleLabel;
    private JPanel inspectionPanel;

    private boolean isScheduleVisible = false;
    private boolean isInspectionVisible = false;

    public InspectorUI(String username, int employeeId) {
        this.currentUsername = username;
        this.currentEmployeeId = employeeId;

        setUndecorated(true);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(44, 62, 80), getWidth(), getHeight(), new Color(33, 47, 60));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        add(mainPanel);

        JLabel title = new JLabel("Меню Інспектора", SwingConstants.CENTER);
        title.setFont(new Font("Roboto", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(33, 47, 60));
        sidePanel.setLayout(new GridLayout(3, 1, 20, 20));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.add(sidePanel, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        schedulePanel = new JPanel();
        schedulePanel.setBackground(new Color(52, 73, 94));
        schedulePanel.setVisible(false);
        schedulePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        scheduleLabel = new JLabel();
        schedulePanel.add(scheduleLabel);

        sidePanel.add(createButton("Графік роботи", this::toggleSchedule));
        sidePanel.add(createButton("Проведення огляду", this::startInspection));
        sidePanel.add(createButton1(() -> System.exit(0)));


        setVisible(true);
    }
    private JButton createButton1(Runnable action) {
        JButton button = new JButton("Вийти");
        button.setFont(new Font("Roboto", Font.BOLD, 26));
        button.setBackground(new Color(231, 76, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(192, 57, 43), 2));
        button.setPreferredSize(new Dimension(300, 60));

        button.addActionListener(e -> action.run());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(192, 57, 43));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(231, 76, 60));
            }
        });

        return button;
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 24));
        button.setBackground(new Color(46, 204, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 60));
        button.setBorder(BorderFactory.createLineBorder(new Color(39, 174, 96), 2));

        button.addActionListener(e -> action.run());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(39, 174, 96));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(46, 204, 113));
            }
        });

        return button;
    }

    private void hideAllPanels() {
        if (schedulePanel != null) schedulePanel.setVisible(false);
        if (inspectionPanel != null) inspectionPanel.setVisible(false);
        isScheduleVisible = false;
        isInspectionVisible = false;
    }

    private void toggleSchedule() {
        if (!isScheduleVisible) {
            hideAllPanels();
            String scheduleText = UserDAO.getScheduleForUser(currentUsername);

            if (scheduleText.isEmpty()) {
                scheduleText = "Графік роботи не доступний";
            }

            scheduleLabel.setText("<html><div style='color:white;font-size:16px;'>"
                    + scheduleText.replace("\n", "<br>")
                    + "</div></html>");
            schedulePanel.removeAll();
            schedulePanel.add(scheduleLabel);
            contentPanel.add(schedulePanel, BorderLayout.CENTER);
            schedulePanel.setVisible(true);
            contentPanel.revalidate();
            contentPanel.repaint();
            isScheduleVisible = true;
        } else {
            hideAllPanels();
        }
    }

    private void startInspection() {
        hideAllPanels();

        inspectionPanel = createInspectionPanel();

        JPanel topPanel = createTopPanel();
        inspectionPanel.add(topPanel);

        JPanel resultPanel = createResultPanel();
        inspectionPanel.add(resultPanel);

        contentPanel.add(inspectionPanel, BorderLayout.CENTER);
        inspectionPanel.setVisible(true);
        contentPanel.revalidate();
        contentPanel.repaint();
        isInspectionVisible = true;
    }


    private JPanel createInspectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JPanel createTopPanel() {
        JLabel idLabel = createStyledLabel("Введіть ID декларації:", 18);

        JTextField idField = new JTextField(20);
        Dimension size = new Dimension(250, 36);
        idField.setFont(new Font("Roboto", Font.PLAIN, 16));
        idField.setPreferredSize(size);
        idField.setMaximumSize(size);
        idField.setMinimumSize(size);

        JButton checkButton = createStyledButton("Перевірити", new Color(52, 152, 219));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(44, 62, 80));
        topPanel.add(idLabel);
        topPanel.add(idField);
        topPanel.add(checkButton);

        checkButton.addActionListener(e -> onCheckDeclaration(idField));
        return topPanel;
    }

    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel();
        resultPanel.setBackground(new Color(44, 62, 80));
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        return resultPanel;
    }


    private void onCheckDeclaration(JTextField idField) {
        JPanel resultPanel = (JPanel) inspectionPanel.getComponent(1);
        resultPanel.removeAll();

        try {
            int id = Integer.parseInt(idField.getText());
            Map<String, Object> declaration = DeclarationDAO.getDeclarationBasicInfo(id);
            if (declaration == null) {
                JOptionPane.showMessageDialog(this, "Декларацію не знайдено або не в очікуванні.",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            addDeclarationTable(resultPanel, declaration);
            addOwnerTable(resultPanel, id);
            addStatusControls(resultPanel, id);

            resultPanel.revalidate();
            resultPanel.repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введіть коректний ID.", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDeclarationTable(JPanel resultPanel, Map<String, Object> declaration) {
        JLabel declLabel = createSectionLabel("Декларація:");
        resultPanel.add(declLabel);

        String[] cols = {"ID", "Тип операції", "Митна вартість", "Країна експорту",
                "Країна призначення", "Дата подачі", "Транспорт", "Статус"};

        Object[][] data = {{
                declaration.get("id"),
                declaration.get("operation_type"),
                declaration.get("customs_value"),
                declaration.get("export_country"),
                declaration.get("destination_country"),
                declaration.get("submission_date"),
                declaration.get("transport_method"),
                declaration.get("status")
        }};

        JTable table = createStyledTable(data, cols);
        JScrollPane scrollPane = wrapTableInScroll(table, 850);
        resultPanel.add(Box.createVerticalStrut(10));
        resultPanel.add(scrollPane);
    }

    private void addOwnerTable(JPanel resultPanel, int id) {
        Map<String, Object> owner = OwnerDAO.getOwnerInfoByDeclarationId(id);
        if (owner == null) return;

        JLabel ownerLabel = createSectionLabel("Власник:");
        resultPanel.add(ownerLabel);

        String[] cols = {"ПІБ", "Контакт", "РНОКПП"};
        Object[][] data = {{
                owner.get("full_name"),
                owner.get("contact_info"),
                owner.get("РНОКПП")
        }};

        JTable table = createStyledTable(data, cols);
        JScrollPane scrollPane = wrapTableInScroll(table, 750);
        resultPanel.add(Box.createVerticalStrut(10));
        resultPanel.add(scrollPane);
    }

    private void addStatusControls(JPanel resultPanel, int id) {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusPanel.setBackground(new Color(44, 62, 80));

        JLabel statusLabel = createStyledLabel("Оберіть статус:", 18);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{
                "Потребує передекларування", "Дозволено", "Відхилено"});
        statusBox.setFont(new Font("Roboto", Font.PLAIN, 16));
        statusBox.setPreferredSize(new Dimension(220, 32));

        statusPanel.add(statusLabel);
        statusPanel.add(statusBox);
        resultPanel.add(Box.createVerticalStrut(10));
        resultPanel.add(statusPanel);

        JButton saveButton = createStyledButton("Зберегти результат", new Color(39, 174, 96));
        saveButton.addActionListener(ev -> onSaveInspection(id, (String) statusBox.getSelectedItem()));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        buttonPanel.setBackground(new Color(44, 62, 80));
        buttonPanel.add(saveButton);
        resultPanel.add(buttonPanel);
    }


    private void onSaveInspection(int id, String status) {
        String finalStatus = status.equals("Дозволено") ? "Опрацьовано" : "Скасовано";
        InspectionDAO.saveInspection(id, currentEmployeeId, status);
        DeclarationDAO.updateDeclarationStatus(id, finalStatus);
        JOptionPane.showMessageDialog(this, "Результат огляду збережено.");
        hideAllPanels();
    }


    private JLabel createStyledLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Roboto", Font.PLAIN, size));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Roboto", Font.BOLD, 22));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 3, 0));
        return label;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        return button;
    }

    private JTable createStyledTable(Object[][] data, String[] cols) {
        JTable table = new JTable(new DefaultTableModel(data, cols)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public int getRowCount() {
                return 1;
            }
        };

        table.setFont(new Font("Roboto", Font.PLAIN, 18));
        table.setRowHeight(28);
        table.setBackground(new Color(52, 73, 94));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.WHITE);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Roboto", Font.BOLD, 18));
        header.setForeground(Color.WHITE);
        header.setBackground(new Color(41, 128, 185));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        return table;
    }

    private JScrollPane wrapTableInScroll(JTable table, int width) {
        JScrollPane scrollPane = new JScrollPane(table) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(44, 62, 80));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        scrollPane.getViewport().setBackground(new Color(44, 62, 80));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(width,
                table.getRowHeight() + table.getTableHeader().getPreferredSize().height));
        return scrollPane;
    }

    private JTableHeader createCustomTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Roboto", Font.BOLD, 18));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        return header;
    }

}
