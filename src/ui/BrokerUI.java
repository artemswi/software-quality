package ui;

import dao.DeclarationDAO;
import dao.UserDAO;
import dao.OwnerDAO;
import dao.ReferenceDataDAO;
import dao.TariffDAO;

import javax.swing.*;
import java.awt.*;

public class BrokerUI extends JFrame {
    private JLabel scheduleLabel;
    private JPanel schedulePanel;
    private String currentUsername;
    private boolean isScheduleVisible = false;
    private boolean isDeclarationsVisible = false;
    private JPanel declarationPanel;
    private JComboBox<Integer> declarationSelector;
    private JTable declarationTable;
    private int currentEmployeeId;
    private JPanel contentPanel;
    private boolean isAddDeclarationVisible = false;
    private JPanel addDeclarationPanel;
    private JTextField rnokppField;
    private JTextField nameField;
    private JTextField contactField;
    private JTextField productNameField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField weightField;

    private JComboBox<String> productTypeBox;
    private JComboBox<String> directionBox;
    private JComboBox<String> originCountry;
    private JComboBox<String> fromCountry;
    private JComboBox<String> toCountry;
    private JComboBox<String> transportBox;



    public BrokerUI(String username, int employeeId) {
        super("Меню брокера");

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

        JLabel titleLabel = new JLabel("МЕНЮ БРОКЕРА", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
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
        scheduleLabel.setPreferredSize(new Dimension(600, 200));

        schedulePanel.add(scheduleLabel);

        contentPanel.add(schedulePanel, BorderLayout.CENTER);
        schedulePanel.setVisible(false);


        sidePanel.add(createButton("Додати декларацію", this::toggleAddDeclaration));

        sidePanel.add(createButton("Переглянути декларації", this::toggleDeclarations));

        sidePanel.add(createButton("Графік роботи", this::toggleSchedule));


        JButton exitButton = new JButton("Вийти");
        exitButton.setFont(new Font("Roboto", Font.BOLD, 26));
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

        sidePanel.add(exitButton);

        setVisible(true);
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
        if (addDeclarationPanel != null) addDeclarationPanel.setVisible(false);
        if (declarationPanel != null) declarationPanel.setVisible(false);
        if (schedulePanel != null) schedulePanel.setVisible(false);

        isAddDeclarationVisible = false;
        isDeclarationsVisible = false;
        isScheduleVisible = false;
    }


    private void toggleSchedule() {
        if (!isScheduleVisible) {
            hideAllPanels();

            String scheduleText = UserDAO.getScheduleForUser(currentUsername);

            if (scheduleText == null || scheduleText.isEmpty()) {
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





    private void toggleDeclarations() {
        boolean willBeVisible = !isDeclarationsVisible;
        hideAllPanels();

        if (!willBeVisible) return;
        if (declarationPanel == null) {
            declarationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            declarationPanel.setOpaque(false);
            declarationPanel.setBorder(BorderFactory.createEmptyBorder(100, 30, 30, 30));

            JPanel innerPanel = new JPanel();
            innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
            innerPanel.setOpaque(false);

            declarationSelector = new JComboBox<>();
            declarationSelector.setFont(new Font("Roboto", Font.PLAIN, 18));
            declarationSelector.setBackground(new Color(44, 62, 80));
            declarationSelector.setForeground(Color.WHITE);
            declarationSelector.setCursor(new Cursor(Cursor.HAND_CURSOR));
            declarationSelector.setFocusable(false);
            declarationSelector.setMaximumSize(new Dimension(250, 36));
            declarationSelector.setPreferredSize(new Dimension(250, 36));
            declarationSelector.setMaximumRowCount(5);


            java.util.List<Integer> ids = DeclarationDAO.getDeclarationIdsForUser(currentEmployeeId);
            for (int id : ids) {
                declarationSelector.addItem(id);
            }

            declarationSelector.addActionListener(e -> {
                Integer selectedId = (Integer) declarationSelector.getSelectedItem();
                if (selectedId != null) showDeclaration(selectedId);
            });

            JPanel comboWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            comboWrapper.setOpaque(false);
            comboWrapper.add(declarationSelector);
            innerPanel.add(comboWrapper);

            innerPanel.add(Box.createVerticalStrut(20));

            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            separator.setMaximumSize(new Dimension(600, 2));
            separator.setForeground(Color.LIGHT_GRAY);
            innerPanel.add(separator);
            innerPanel.add(Box.createVerticalStrut(20));

            declarationTable = new JTable();
            declarationTable.setFont(new Font("Roboto", Font.PLAIN, 16));
            declarationTable.setRowHeight(25);

            JScrollPane tableScroll = new JScrollPane(declarationTable);
            tableScroll.setPreferredSize(new Dimension(1000, 200));
            innerPanel.add(tableScroll);

            declarationPanel.add(innerPanel);
            contentPanel.add(declarationPanel, BorderLayout.CENTER);
        }

        declarationPanel.setVisible(true);
        isDeclarationsVisible = true;

        revalidate();
        repaint();
    }



    private void toggleAddDeclaration() {
        boolean willBeVisible = !isAddDeclarationVisible;
        hideAllPanels();

        if (!willBeVisible) return;

        if (addDeclarationPanel == null) {
            addDeclarationPanel = new JPanel(new BorderLayout());
            addDeclarationPanel.setOpaque(false);
            addDeclarationPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            JPanel innerPanel = new JPanel();
            innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
            innerPanel.setOpaque(false);

            JPanel rnokppRow = new JPanel();
            rnokppRow.setOpaque(false);
            rnokppRow.setLayout(new BoxLayout(rnokppRow, BoxLayout.X_AXIS));
            JLabel rnokppLabel = new JLabel("РНОКПП власника товару:");
            rnokppLabel.setFont(new Font("Roboto", Font.BOLD, 18));
            rnokppLabel.setForeground(Color.WHITE);
            rnokppLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

            rnokppField = new JTextField(10);
            rnokppField.setFont(new Font("Roboto", Font.PLAIN, 18));
            rnokppField.setMaximumSize(new Dimension(150, 26));
            rnokppField.setAlignmentY(Component.CENTER_ALIGNMENT);

            JButton checkButton = new JButton("Перевірити");
            checkButton.setFont(new Font("Roboto", Font.BOLD, 18));
            checkButton.setBackground(new Color(52, 152, 219));
            checkButton.setForeground(Color.WHITE);
            checkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            checkButton.setAlignmentY(Component.CENTER_ALIGNMENT);

            rnokppRow.add(rnokppLabel);
            rnokppRow.add(Box.createRigidArea(new Dimension(10, 0)));
            rnokppRow.add(rnokppField);
            rnokppRow.add(Box.createRigidArea(new Dimension(10, 0)));
            rnokppRow.add(checkButton);

            innerPanel.add(rnokppRow);
            innerPanel.add(Box.createVerticalStrut(20));

            JPanel ownerInfoPanel = new JPanel();
            ownerInfoPanel.setOpaque(false);
            ownerInfoPanel.setLayout(new BoxLayout(ownerInfoPanel, BoxLayout.Y_AXIS));
            innerPanel.add(ownerInfoPanel);

            checkButton.addActionListener(e -> {
                String rnokpp = rnokppField.getText().trim();
                if (rnokpp.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Введіть РНОКПП.", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ownerInfoPanel.removeAll();

                var owner = OwnerDAO.findOwnerByRNOKPP(rnokpp);
                if (owner != null) {
                    JLabel nameLabel = new JLabel("Ім’я: " + owner.getName());
                    nameLabel.setFont(new Font("Roboto", Font.PLAIN, 18));
                    nameLabel.setForeground(Color.WHITE);

                    JLabel contactLabel = new JLabel("Контакт: " + owner.getContact());
                    contactLabel.setFont(new Font("Roboto", Font.PLAIN, 18));
                    contactLabel.setForeground(Color.WHITE);

                    ownerInfoPanel.add(nameLabel);
                    ownerInfoPanel.add(contactLabel);

                    addDeclarationFields(ownerInfoPanel);
                } else {
                    nameField = new JTextField(20);
                    nameField.setFont(new Font("Roboto", Font.PLAIN, 16));
                    Dimension nameSize = new Dimension(250, 36);
                    nameField.setPreferredSize(nameSize);
                    nameField.setMaximumSize(nameSize);
                    nameField.setMinimumSize(nameSize);


                    contactField = new JTextField(20);
                    contactField.setFont(new Font("Roboto", Font.PLAIN, 16));
                    contactField.setPreferredSize(new Dimension(250, 36));
                    contactField.setMaximumSize(new Dimension(250, 36));
                    contactField.setMinimumSize(new Dimension(250, 36));


                    ownerInfoPanel.add(createLabeledComponent("Ім’я:", nameField));
                    ownerInfoPanel.add(Box.createVerticalStrut(10));
                    ownerInfoPanel.add(createLabeledComponent("Контактна інформація (email):", contactField));

                    addDeclarationFields(ownerInfoPanel);
                }

                ownerInfoPanel.revalidate();
                ownerInfoPanel.repaint();
            });

            JScrollPane scrollPane = new JScrollPane(innerPanel);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            addDeclarationPanel.add(scrollPane, BorderLayout.CENTER);
            contentPanel.add(addDeclarationPanel, BorderLayout.CENTER);
        }

        clearDeclarationForm();
        addDeclarationPanel.setVisible(true);
        isAddDeclarationVisible = true;

        revalidate();
        repaint();
    }



    private void clearDeclarationForm() {
        if (rnokppField != null) rnokppField.setText("");
        if (nameField != null) nameField.setText("");
        if (contactField != null) contactField.setText("");
        if (productNameField != null) productNameField.setText("");
        if (quantityField != null) quantityField.setText("");
        if (priceField != null) priceField.setText("");
        if (weightField != null) weightField.setText("");

        if (directionBox != null) directionBox.setSelectedIndex(0);
        if (fromCountry != null) fromCountry.setSelectedIndex(0);
        if (toCountry != null) toCountry.setSelectedIndex(0);
        if (productTypeBox != null) productTypeBox.setSelectedIndex(0);
        if (originCountry != null) originCountry.setSelectedIndex(0);
        if (transportBox != null) transportBox.setSelectedIndex(0);
    }



    private void showDeclaration(Integer id) {
        if (id == null) return;
        Object[][] data = DeclarationDAO.getDeclarationById(id);
        String[] columns = DeclarationDAO.getColumnNames();
        declarationTable.setModel(new javax.swing.table.DefaultTableModel(data, columns));
    }

    private JPanel createLabeledComponent(String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Roboto", Font.PLAIN, 18));
        label.setForeground(Color.WHITE);
        field.setFont(new Font("Roboto", Font.PLAIN, 18));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(field);

        return panel;
    }

    private void addDeclarationFields(JPanel parentPanel) {
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));

        Dimension fieldSize = new Dimension(250, 36);

        directionBox = new JComboBox<>(ReferenceDataDAO.getItemsByType("Напрямки").toArray(new String[0]));
        directionBox.setPreferredSize(fieldSize);
        directionBox.setMaximumSize(fieldSize);
        directionBox.setMinimumSize(fieldSize);
        parentPanel.add(createLabeledComponent("Тип операції:", directionBox));

        fromCountry = new JComboBox<>(ReferenceDataDAO.getItemsByType("Країни").toArray(new String[0]));
        fromCountry.setPreferredSize(fieldSize);
        fromCountry.setMaximumSize(fieldSize);
        fromCountry.setMinimumSize(fieldSize);
        parentPanel.add(createLabeledComponent("Країна відправлення:", fromCountry));

        toCountry = new JComboBox<>(ReferenceDataDAO.getItemsByType("Країни").toArray(new String[0]));
        toCountry.setPreferredSize(fieldSize);
        toCountry.setMaximumSize(fieldSize);
        toCountry.setMinimumSize(fieldSize);
        parentPanel.add(createLabeledComponent("Країна призначення:", toCountry));

        productNameField = new JTextField(20);
        productNameField.setPreferredSize(fieldSize);
        productNameField.setMaximumSize(fieldSize);
        productNameField.setMinimumSize(fieldSize);
        parentPanel.add(createLabeledComponent("Назва товару:", productNameField));

        productTypeBox = new JComboBox<>(ReferenceDataDAO.getItemsByType("Категорії").toArray(new String[0]));
        productTypeBox.setPreferredSize(fieldSize);
        productTypeBox.setMaximumSize(fieldSize);
        productTypeBox.setMinimumSize(fieldSize);
        parentPanel.add(createLabeledComponent("Тип товару:", productTypeBox));

        originCountry = new JComboBox<>(ReferenceDataDAO.getItemsByType("Країни").toArray(new String[0]));
        originCountry.setPreferredSize(fieldSize);
        originCountry.setMaximumSize(fieldSize);
        originCountry.setMinimumSize(fieldSize);
        parentPanel.add(createLabeledComponent("Країна походження:", originCountry));


        transportBox = new JComboBox<>(new String[]{"Морський", "Наземний", "Повітряний"});
        transportBox.setPreferredSize(fieldSize);
        transportBox.setMaximumSize(fieldSize);
        transportBox.setMinimumSize(fieldSize);
        parentPanel.add(createLabeledComponent("Спосіб транспортування:", transportBox));

        quantityField = new JTextField(10);
        quantityField.setPreferredSize(fieldSize);
        quantityField.setMaximumSize(fieldSize);
        quantityField.setMinimumSize(fieldSize);
        parentPanel.add(createLabeledComponent("Кількість:", quantityField));

        priceField = new JTextField(10);
        priceField.setPreferredSize(fieldSize);
        priceField.setMaximumSize(fieldSize);
        priceField.setMinimumSize(fieldSize);
        parentPanel.add(createLabeledComponent("Ціна за одиницю:", priceField));

        weightField = new JTextField(10);
        weightField.setPreferredSize(fieldSize);
        weightField.setMaximumSize(fieldSize);
        weightField.setMinimumSize(fieldSize);
        parentPanel.add(createLabeledComponent("Вага одиниці (кг):", weightField));

        parentPanel.add(Box.createVerticalStrut(20));

        JButton createButton = getJButton();
        parentPanel.add(createButton);
    }


    private JButton getJButton() {
        JButton createButton = new JButton("Створити декларацію");
        createButton.setFont(new Font("Roboto", Font.BOLD, 20));
        createButton.setBackground(new Color(46, 204, 113));
        createButton.setForeground(Color.WHITE);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        createButton.addActionListener(e -> {
            try {
                String rnokpp = rnokppField.getText().trim();
                String fullName = nameField != null ? nameField.getText().trim() : "";
                String contact = contactField != null ? contactField.getText().trim() : "";

                int quantity = Integer.parseInt(quantityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());

                String productName = productNameField.getText().trim();
                String category = (String) productTypeBox.getSelectedItem();
                String direction = (String) directionBox.getSelectedItem();
                String origin = (String) originCountry.getSelectedItem();
                String from = (String) fromCountry.getSelectedItem();
                String to = (String) toCountry.getSelectedItem();
                String transport = (String) transportBox.getSelectedItem();

                int ownerId = OwnerDAO.findOwnerIdByRNOKPP(rnokpp);
                if (ownerId == -1) {
                    ownerId = OwnerDAO.insertNewOwner(rnokpp, fullName, contact);
                }

                double customsValue = calculateCustomsValueFromDB(quantity, price, category, direction, origin);
                int declarationId = DeclarationDAO.insertDeclaration(
                        ownerId, currentEmployeeId, customsValue, direction, from, to, transport
                );

                dao.ProductDAO.insertProduct(productName, category, origin, quantity, price, weight, declarationId);

                JOptionPane.showMessageDialog(this, "Декларацію створено успішно!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Перевірте числові поля.", "Помилка", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Помилка при створенні декларації.", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        });
        return createButton;
    }

    public static double calculateCustomsValueFromDB(
            int quantity,
            double unitPrice,
            String category,
            String direction,
            String originCountry
    ) {
        double rateCategory = TariffDAO.getRateByCategory(category);
        double rateDirection = TariffDAO.getRateByDirection(direction);
        double rateCountry = TariffDAO.getRateByCountry(originCountry);

        double base = quantity * unitPrice;
        double customsValue = (base * rateCategory * rateDirection * rateCountry) / 10.0;

        return Math.round(customsValue * 100.0) / 100.0;
    }
}
