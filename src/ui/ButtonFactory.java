package ui;

import javax.swing.*;
import java.awt.*;

public class ButtonFactory {

    public static JButton createStyledButton(
            String text,
            Color backgroundColor,
            Color hoverColor,
            Dimension size,
            Runnable action
    ) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 24));
        button.setBackground(backgroundColor != null ? backgroundColor : UIConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(
                (backgroundColor != null ? backgroundColor.darker() : UIConstants.PRIMARY_COLOR.darker()), 2
        ));

        if (size != null) {
            button.setPreferredSize(size);
        } else {
            button.setPreferredSize(new Dimension(UIConstants.BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT));
        }

        if (action != null) {
            button.addActionListener(e -> action.run());
        }

        Color effectiveHoverColor = hoverColor != null ? hoverColor : backgroundColor;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(effectiveHoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(backgroundColor != null ? backgroundColor : UIConstants.PRIMARY_COLOR);
            }
        });

        return button;
    }
}