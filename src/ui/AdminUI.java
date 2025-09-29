package ui;

import dao.UserDAO;
import dao.ReferenceDataDAO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUI extends JFrame {
    private JPanel contentPanel;
    private boolean isScheduleVisible = false;
    private boolean isTariffsVisible = false;
    private boolean isEmployeesVisible = false;
    private JPanel schedulePanel;
    private JLabel scheduleLabel;
    private JPanel tariffsPanel;
    private JPanel employeesPanel;
    private int currentEmployeeId;
    private String currentUsername;
    private String currentVisibleList = null;
    private JPanel staffPanel;
    private Map<String, Integer> employeeNameToId = new HashMap<>();
    private JPanel currentContentPanel;
    private boolean isSchedulePanelVisible = false;
    private boolean isEditEmployeeVisible = false;
    private boolean isAddEmployeeVisible = false;

    private JPanel mainTariffsPanel;

    public AdminUI(String username, int employeeId) {
        setTitle("Меню адміністратора");
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
                GradientPaint gradient = new GradientPaint(0, 0, new Color(33, 47, 60), getWidth(), getHeight(), new Color(44, 62, 80));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        add(mainPanel);

        JLabel titleLabel = new JLabel("Меню Адміністратора", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(33, 47, 60));
        sidePanel.setLayout(new GridLayout(4, 1, 20, 20));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.add(sidePanel, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        schedulePanel = new JPanel();
        schedulePanel.setBackground(new Color(52, 73, 94));
        schedulePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        scheduleLabel = new JLabel();
        scheduleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scheduleLabel.setForeground(Color.WHITE);
        scheduleLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        schedulePanel.add(scheduleLabel);

        tariffsPanel = createSimplePanel("Тарифи");
        employeesPanel = createSimplePanel("Працівники");
        sidePanel.add(createButton("Графік роботи", this::toggleSchedule));
        sidePanel.add(createButton("Тарифи", this::toggleTariffs));
        sidePanel.add(createButton("Працівники", this::toggleEmployees));
        JButton exitButton = getJButton();
        sidePanel.add(exitButton);
        setVisible(true);
    }

    private static JButton getJButton() {
        JButton exitButton = new JButton("Вийти");
        exitButton.setFont(new Font("Roboto", Font.BOLD, 24));
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.setBorder(BorderFactory.createLineBorder(new Color(192, 57, 43), 2));
        exitButton.setPreferredSize(new Dimension(300, 60));
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(192, 57, 43));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(231, 76, 60));
            }
        });
        return exitButton;
    }

    private void toggleSchedule() {
        if (!isScheduleVisible) {
            hideAllPanels();

            String scheduleText = UserDAO.getScheduleForUser(currentUsername);

            if (scheduleText.isEmpty()) {
                scheduleText = "Графік роботи не доступний";
            }

            scheduleLabel.setText("");
            schedulePanel.removeAll();
            schedulePanel.add(scheduleLabel);

            scheduleLabel.setText("<html><div style='color:white;font-size:16px;'>"
                    + scheduleText.replace("\n", "<br>")
                    + "</div></html>");

            schedulePanel.revalidate();
            schedulePanel.repaint();

            contentPanel.add(schedulePanel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();

            schedulePanel.setVisible(true);
            isScheduleVisible = true;
        } else {
            hideAllPanels();
        }
    }

    private void toggleTariffs() {
        if (!isTariffsVisible) {
            hideAllPanels();

            if (mainTariffsPanel == null) {
                mainTariffsPanel = new JPanel(new BorderLayout());
                mainTariffsPanel.setOpaque(false);

                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
                buttonsPanel.setBackground(new Color(44, 62, 80));

                JButton countriesButton = createLargeStyledButton("Країни", () ->
                        updateTariffsContent("Країни", ReferenceDataDAO.getItemsByType("Країни"))
                );

                JButton productTypesButton = createLargeStyledButton("Категорії", () ->
                        updateTariffsContent("Категорії", ReferenceDataDAO.getItemsByType("Категорії"))
                );

                JButton directionsButton = createLargeStyledButton("Напрямки", () ->
                        updateTariffsContent("Напрямки", ReferenceDataDAO.getItemsByType("Напрямки"))
                );

                buttonsPanel.add(countriesButton);
                buttonsPanel.add(productTypesButton);
                buttonsPanel.add(directionsButton);

                mainTariffsPanel.add(buttonsPanel, BorderLayout.NORTH);

                JPanel dynamicContentPanel = new JPanel(new BorderLayout());
                dynamicContentPanel.setOpaque(false);
                mainTariffsPanel.add(dynamicContentPanel, BorderLayout.CENTER);
            }
            JPanel dynamicContentPanel = (JPanel) mainTariffsPanel.getComponent(1);
            dynamicContentPanel.removeAll();
            currentVisibleList = null;
            contentPanel.add(mainTariffsPanel, BorderLayout.CENTER);
            isTariffsVisible = true;
            refresh();
        } else {
            hideAllPanels();
        }
    }

    private void updateTariffsContent(String tableType, List<String> items) {
        JPanel dynamicContentPanel = (JPanel) mainTariffsPanel.getComponent(1);
        dynamicContentPanel.removeAll();
        showListWithEditableRate(dynamicContentPanel, tableType, items);
        dynamicContentPanel.revalidate();
        dynamicContentPanel.repaint();
    }


    private void showListWithEditableRate(JPanel parentPanel, String tableType, List<String> items) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.3);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(1);
        splitPane.setBackground(new Color(52, 73, 94));
        splitPane.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 1));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        items.forEach(listModel::addElement);
        JList<String> itemsList = new JList<>(listModel);
        itemsList.setFont(new Font("Roboto", Font.PLAIN, 18));
        itemsList.setForeground(Color.WHITE);
        itemsList.setBackground(new Color(44, 62, 80));
        itemsList.setSelectionBackground(new Color(39, 174, 96));
        itemsList.setSelectionForeground(Color.WHITE);

        JScrollPane listScrollPane = new JScrollPane(itemsList);
        listScrollPane.setPreferredSize(new Dimension(300, 400));
        listScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        listScrollPane.setBackground(new Color(52, 73, 94));
        listScrollPane.getViewport().setBackground(new Color(52, 73, 94));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(52, 73, 94));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JTable rateTable = new JTable();
        rateTable.setFont(new Font("Roboto", Font.PLAIN, 18));
        rateTable.setRowHeight(32);
        rateTable.setEnabled(false);
        rateTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 18));
        rateTable.getTableHeader().setBackground(new Color(33, 47, 60));
        rateTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane tableScrollPane = new JScrollPane(rateTable);
        tableScrollPane.setPreferredSize(new Dimension(450, 65));
        tableScrollPane.setBackground(new Color(52, 73, 94));
        tableScrollPane.getViewport().setBackground(new Color(52, 73, 94));
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        rightPanel.add(tableScrollPane);
        rightPanel.add(Box.createVerticalStrut(30));
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setOpaque(false);

        JLabel rateLabel = new JLabel("Новий тариф:");
        rateLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        rateLabel.setForeground(Color.WHITE);

        JTextField rateInput = new JTextField(10);
        rateInput.setFont(new Font("Roboto", Font.PLAIN, 18));
        rateInput.setPreferredSize(new Dimension(100, 36));

        JButton saveButton = new JButton("Зберегти");
        saveButton.setFont(new Font("Roboto", Font.BOLD, 18));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);

        inputPanel.add(rateLabel);
        inputPanel.add(rateInput);
        inputPanel.add(Box.createHorizontalStrut(10));
        inputPanel.add(saveButton);
        rightPanel.add(inputPanel);

        itemsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedItem = itemsList.getSelectedValue();
                if (selectedItem != null) {
                    double rate = ReferenceDataDAO.getRateForItem(tableType.toLowerCase(), selectedItem);
                    Object[][] tableData = {{selectedItem, rate}};
                    String[] columnNames = {"Назва", "Тариф"};
                    rateTable.setModel(new javax.swing.table.DefaultTableModel(tableData, columnNames));
                }
            }
        });

        saveButton.addActionListener(e -> {
            String selectedItem = itemsList.getSelectedValue();
            if (selectedItem == null) {
                JOptionPane.showMessageDialog(this, "Оберіть елемент зі списку!", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double newRate = Double.parseDouble(rateInput.getText().trim());
                ReferenceDataDAO.updateRate(tableType.toLowerCase(), selectedItem, newRate);
                JOptionPane.showMessageDialog(this, "Тариф оновлено!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
                rateTable.setValueAt(newRate, 0, 1);
                rateInput.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введіть коректне число!", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        });

        splitPane.setLeftComponent(listScrollPane);
        splitPane.setRightComponent(rightPanel);

        parentPanel.setLayout(new BorderLayout());
        parentPanel.add(splitPane, BorderLayout.CENTER);
    }




    private JButton createLargeStyledButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 22));
        button.setPreferredSize(new Dimension(300, 70));
        button.setBackground(new Color(46, 204, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(39, 174, 96), 2));

        button.addActionListener(e -> action.run());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(39, 174, 96));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(46, 204, 113));
            }
        });

        return button;
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
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

    private JPanel createSimplePanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Roboto", Font.BOLD, 36));
        label.setForeground(Color.WHITE);
        panel.add(label);
        return panel;
    }
    private void hideAllPanels() {
        contentPanel.removeAll();
        schedulePanel.setVisible(false);
        tariffsPanel.setVisible(false);
        employeesPanel.setVisible(false);
        isScheduleVisible = false;
        isTariffsVisible = false;
        isEmployeesVisible = false;
        currentVisibleList = null;
        refresh();
    }

    private void toggleEmployees() {
        if (!isEmployeesVisible) {
            hideAllPanels();
            employeesPanel.removeAll();
            employeesPanel.setLayout(new BorderLayout());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            buttonPanel.setBackground(new Color(52, 73, 94));

            JButton scheduleBtn = createButton("Редагування графіка", this::showEditSchedulePanel);
            JButton editEmployeeBtn = createButton("Редагування працівника", this::showEditEmployeePanel);
            JButton addEmployeeBtn = createButton("Новий працівник", this::showAddEmployeePanel);

            buttonPanel.add(scheduleBtn);
            buttonPanel.add(editEmployeeBtn);
            buttonPanel.add(addEmployeeBtn);

            employeesPanel.add(buttonPanel, BorderLayout.NORTH);

            currentContentPanel = new JPanel();
            currentContentPanel.setBackground(new Color(44, 62, 80));
            employeesPanel.add(currentContentPanel, BorderLayout.CENTER);

            contentPanel.add(employeesPanel, BorderLayout.CENTER);
            employeesPanel.setVisible(true);
            isEmployeesVisible = true;
            refresh();
        } else {
            hideAllPanels();
            isEmployeesVisible = false;
        }
    }


    private void showEditSchedulePanel() {
        if (isSchedulePanelVisible) {
            employeesPanel.remove(currentContentPanel);
            employeesPanel.revalidate();
            employeesPanel.repaint();
            currentContentPanel = new JPanel(); // порожня панель
            currentContentPanel.setBackground(new Color(44, 62, 80));
            employeesPanel.add(currentContentPanel, BorderLayout.CENTER);
            isSchedulePanelVisible = false;
            return;
        }

        employeesPanel.remove(currentContentPanel);
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(new Color(52, 73, 94));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        JLabel title = new JLabel("Перегляд графіка працівників");
        title.setFont(new Font("Roboto", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<String> employeeNames = UserDAO.getAllEmployeeNames();
        JComboBox<String> nameBox = new JComboBox<>(employeeNames.toArray(new String[0]));
        nameBox.setFont(new Font("Roboto", Font.PLAIN, 18));
        nameBox.setMaximumSize(new Dimension(300, 36));
        nameBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel scheduleLabel = new JLabel("Оберіть працівника, щоб побачити його графік.");
        scheduleLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        scheduleLabel.setForeground(Color.WHITE);
        scheduleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameBox.addActionListener(e -> {
            String selectedEmployee = (String) nameBox.getSelectedItem();
            if (selectedEmployee != null) {
                String scheduleText = UserDAO.getScheduleByFullName(selectedEmployee);
                if (scheduleText == null || scheduleText.isEmpty()) {
                    scheduleText = "Графік роботи не доступний";
                }
                scheduleLabel.setText("<html><div style='color:white;font-size:14px;'>"
                        + scheduleText.replace("\n", "<br>") + "</div></html>");
            }
        });

        JTextField dateField = new JTextField();
        dateField.setMaximumSize(new Dimension(300, 36));
        dateField.setFont(new Font("Roboto", Font.PLAIN, 16));
        dateField.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateField.setToolTipText("Формат: 2025-05-20");

        JTextField startField = new JTextField();
        startField.setMaximumSize(new Dimension(300, 36));
        startField.setFont(new Font("Roboto", Font.PLAIN, 16));
        startField.setAlignmentX(Component.LEFT_ALIGNMENT);
        startField.setToolTipText("Формат: 09:00");

        JTextField endField = new JTextField();
        endField.setMaximumSize(new Dimension(300, 36));
        endField.setFont(new Font("Roboto", Font.PLAIN, 16));
        endField.setAlignmentX(Component.LEFT_ALIGNMENT);
        endField.setToolTipText("Формат: 17:00");

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Активний", "Вихідний", "Резерв"});
        statusBox.setMaximumSize(new Dimension(300, 36));
        statusBox.setFont(new Font("Roboto", Font.PLAIN, 16));
        statusBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveButton = new JButton("Зберегти");
        saveButton.setFont(new Font("Roboto", Font.BOLD, 18));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        saveButton.addActionListener(e -> {
            String name = (String) nameBox.getSelectedItem();
            String date = dateField.getText().trim();
            String start = startField.getText().trim();
            String end = endField.getText().trim();
            String status = (String) statusBox.getSelectedItem();

            if (name == null || date.isEmpty() || start.isEmpty() || end.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Будь ласка, заповніть усі поля.");
                return;
            }

            boolean success = UserDAO.insertScheduleEntry(name, date, start, end, status);
            if (success) {
                JOptionPane.showMessageDialog(null, "Зміну додано для " + name);
                nameBox.setSelectedItem(name); // оновити графік
            } else {
                JOptionPane.showMessageDialog(null, "Помилка при збереженні зміни.");
            }
        });

        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(15));
        wrapper.add(nameBox);
        wrapper.add(Box.createVerticalStrut(20));
        wrapper.add(scheduleLabel);
        wrapper.add(Box.createVerticalStrut(30));

        JLabel dateLabel = new JLabel("Дата зміни:");
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        wrapper.add(dateLabel);
        wrapper.add(dateField);
        wrapper.add(Box.createVerticalStrut(10));

        JLabel startLabel = new JLabel("Час початку:");
        startLabel.setForeground(Color.WHITE);
        startLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        wrapper.add(startLabel);
        wrapper.add(startField);
        wrapper.add(Box.createVerticalStrut(10));

        JLabel endLabel = new JLabel("Час завершення:");
        endLabel.setForeground(Color.WHITE);
        endLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        wrapper.add(endLabel);
        wrapper.add(endField);
        wrapper.add(Box.createVerticalStrut(10));

        JLabel statusLabel = new JLabel("Статус:");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        wrapper.add(statusLabel);
        wrapper.add(statusBox);
        wrapper.add(Box.createVerticalStrut(20));

        wrapper.add(saveButton);


        currentContentPanel = wrapper;
        employeesPanel.add(currentContentPanel, BorderLayout.CENTER);
        employeesPanel.revalidate();
        employeesPanel.repaint();
        isSchedulePanelVisible = true;
    }

    private void showEditEmployeePanel() {
        if (isEditEmployeeVisible) {
            employeesPanel.remove(currentContentPanel);
            employeesPanel.revalidate();
            employeesPanel.repaint();
            isEditEmployeeVisible = false;
            return;
        }

        employeesPanel.remove(currentContentPanel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(52, 73, 94));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        JLabel title = new JLabel("Редагування інформації про працівника");
        title.setFont(new Font("Roboto", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(title);
        wrapper.add(topPanel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(44, 62, 80));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<String> employeeNames = UserDAO.getAllEmployeeNames();
        JComboBox<String> employeeBox = new JComboBox<>(employeeNames.toArray(new String[0]));
        employeeBox.setFont(new Font("Roboto", Font.PLAIN, 18));
        employeeBox.setMaximumSize(new Dimension(300, 36));
        leftPanel.add(employeeBox);
        leftPanel.add(Box.createVerticalStrut(20));

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBackground(new Color(44, 62, 80));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(infoPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(44, 62, 80));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel editLabel = new JLabel("Оберіть атрибут для редагування:");
        editLabel.setForeground(Color.WHITE);
        editLabel.setFont(new Font("Roboto", Font.BOLD, 16));

        JComboBox<String> attributeBox = new JComboBox<>(new String[]{
                "ПІБ", "Дата народження", "Стать", "Контактна інформація", "Посада"
        });
        attributeBox.setFont(new Font("Roboto", Font.PLAIN, 16));
        attributeBox.setMaximumSize(new Dimension(250, 36));

        JTextField newValueField = new JTextField(20);
        newValueField.setFont(new Font("Roboto", Font.PLAIN, 16));
        newValueField.setMaximumSize(new Dimension(250, 36));

        JButton saveBtn = new JButton("Зберегти");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Roboto", Font.BOLD, 18));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        saveBtn.addActionListener(e -> {
            String selectedEmp = (String) employeeBox.getSelectedItem();
            String attribute = (String) attributeBox.getSelectedItem();
            String newValue = newValueField.getText().trim();

            if (selectedEmp == null || newValue.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введіть нове значення", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String columnName = switch (attribute) {
                case "ПІБ" -> "full_name";
                case "Дата народження" -> "birthday";
                case "Стать" -> "sex";
                case "Контактна інформація" -> "contact_info";
                case "Посада" -> "rank";
                default -> null;
            };

            if (columnName != null) {
                UserDAO.updateEmployeeField(selectedEmp, columnName, newValue);
                JOptionPane.showMessageDialog(this, "Дані оновлено", "Успіх", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        rightPanel.add(editLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(attributeBox);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(newValueField);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(saveBtn);

        employeeBox.addActionListener(e -> {
            String selectedName = (String) employeeBox.getSelectedItem();
            if (selectedName != null) {
                Map<String, String> info = UserDAO.getEmployeeInfoByName(selectedName);
                infoPanel.removeAll();

                for (Map.Entry<String, String> entry : info.entrySet()) {
                    JLabel keyLabel = new JLabel(entry.getKey() + ":");
                    keyLabel.setForeground(Color.WHITE);
                    keyLabel.setFont(new Font("Roboto", Font.BOLD, 16));
                    JLabel valueLabel = new JLabel(entry.getValue());
                    valueLabel.setForeground(Color.WHITE);
                    valueLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
                    infoPanel.add(keyLabel);
                    infoPanel.add(valueLabel);
                }

                wrapper.revalidate();
                wrapper.repaint();
            }
        });

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        wrapper.add(centerPanel, BorderLayout.CENTER);

        currentContentPanel = wrapper;
        employeesPanel.add(currentContentPanel, BorderLayout.CENTER);
        employeesPanel.revalidate();
        employeesPanel.repaint();
        isEditEmployeeVisible = true;
    }

    private void showAddEmployeePanel() {
        if (currentContentPanel != null) {
            employeesPanel.remove(currentContentPanel);
            employeesPanel.revalidate();
            employeesPanel.repaint();
            if (isAddEmployeeVisible) {
                isAddEmployeeVisible = false;
                return;
            }
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        JLabel title = new JLabel("Новий працівник");
        title.setFont(new Font("Roboto", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        List<String> availablePositions = UserDAO.getAvailablePositionNames();
        if (availablePositions.isEmpty()) {
            JLabel message = new JLabel("Усі посади зайняті.");
            message.setForeground(Color.WHITE);
            message.setFont(new Font("Roboto", Font.BOLD, 18));
            panel.add(message);
        } else {
            JComboBox<String> positionBox = new JComboBox<>(availablePositions.toArray(new String[0]));
            positionBox.setFont(new Font("Roboto", Font.PLAIN, 18));
            positionBox.setMaximumSize(new Dimension(300, 36));
            JLabel positionLabel = new JLabel("Оберіть посаду:");
            positionLabel.setForeground(Color.WHITE);
            panel.add(positionLabel);
            panel.add(positionBox);
            panel.add(Box.createVerticalStrut(15));

            JTextField nameField = new JTextField(20);
            nameField.setFont(new Font("Roboto", Font.PLAIN, 18));
            nameField.setMaximumSize(new Dimension(300, 36));
            nameField.setToolTipText("Введіть повне ім’я працівника");
            JLabel nameLabel = new JLabel("ПІБ:");
            nameLabel.setForeground(Color.WHITE);
            panel.add(nameLabel);
            panel.add(nameField);
            panel.add(Box.createVerticalStrut(10));

            JTextField birthdayField = new JTextField();
            birthdayField.setFont(new Font("Roboto", Font.PLAIN, 18));
            birthdayField.setMaximumSize(new Dimension(300, 36));
            birthdayField.setToolTipText("Формат: yyyy-MM-dd");
            JLabel birthdayLabel = new JLabel("Дата народження (yyyy-MM-dd):");
            birthdayLabel.setForeground(Color.WHITE);
            panel.add(birthdayLabel);
            panel.add(birthdayField);
            panel.add(Box.createVerticalStrut(10));

            String[] sexes = {"Чоловіча", "Жіноча"};
            JComboBox<String> sexBox = new JComboBox<>(sexes);
            sexBox.setFont(new Font("Roboto", Font.PLAIN, 18));
            sexBox.setMaximumSize(new Dimension(300, 36));
            JLabel sexLabel = new JLabel("Стать:");
            sexLabel.setForeground(Color.WHITE);
            panel.add(sexLabel);
            panel.add(sexBox);
            panel.add(Box.createVerticalStrut(10));

            JTextField contactField = new JTextField(20);
            contactField.setFont(new Font("Roboto", Font.PLAIN, 18));
            contactField.setMaximumSize(new Dimension(300, 36));
            contactField.setToolTipText("Номер телефону або email");
            JLabel contactLabel = new JLabel("Контактна інформація:");
            contactLabel.setForeground(Color.WHITE);
            panel.add(contactLabel);
            panel.add(contactField);
            panel.add(Box.createVerticalStrut(10));

            String[] ranks = {"Старший", "Середній", "Молодший"};
            JComboBox<String> rankBox = new JComboBox<>(ranks);
            rankBox.setFont(new Font("Roboto", Font.PLAIN, 18));
            rankBox.setMaximumSize(new Dimension(300, 36));
            JLabel rankLabel = new JLabel("Ранг:");
            rankLabel.setForeground(Color.WHITE);
            panel.add(rankLabel);
            panel.add(rankBox);
            panel.add(Box.createVerticalStrut(10));

            JButton saveButton = new JButton("Зберегти");
            saveButton.setBackground(new Color(46, 204, 113));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFont(new Font("Roboto", Font.BOLD, 18));
            panel.add(saveButton);

            saveButton.addActionListener(e -> {
                String fullName = nameField.getText().trim();
                String birthday = birthdayField.getText().trim();
                String sex = (String) sexBox.getSelectedItem();
                String contact = contactField.getText().trim();
                String rank = (String) rankBox.getSelectedItem();
                String position = (String) positionBox.getSelectedItem();

                if (fullName.isEmpty() || birthday.isEmpty() || sex == null || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Заповніть усі поля.", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    LocalDate.parse(birthday);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Невірний формат дати.", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                int employeeId = UserDAO.insertEmployee(fullName, birthday, sex, contact, rank);
                if (employeeId == -1) {
                    JOptionPane.showMessageDialog(this, "Помилка при додаванні працівника.", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int positionId = UserDAO.getPositionIdByName(position);
                if (positionId == -1) {
                    JOptionPane.showMessageDialog(this, "Помилка отримання id посади.", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                UserDAO.assignPositionToEmployee(employeeId, position);

                String login = UserDAO.generateLogin(fullName);
                String password = UserDAO.generatePassword();

                UserDAO.createUserAccount(login, password, employeeId, positionId);

                JOptionPane.showMessageDialog(this, "Працівника створено!\nЛогін: " + login + "\nПароль: " + password, "Успіх", JOptionPane.INFORMATION_MESSAGE);

                toggleEmployees();
            });
        }

        currentContentPanel = panel;
        employeesPanel.add(currentContentPanel, BorderLayout.CENTER);
        employeesPanel.revalidate();
        employeesPanel.repaint();
        isAddEmployeeVisible = true;
    }
}
