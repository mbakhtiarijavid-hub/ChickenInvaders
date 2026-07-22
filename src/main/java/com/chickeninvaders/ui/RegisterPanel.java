package com.chickeninvaders.ui;

import com.chickeninvaders.GameMain;
import com.chickeninvaders.data.DatabaseManager;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {

    private final GameMain gameMain;
    private final DatabaseManager db;
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JPasswordField confirmField = new JPasswordField(16);
    private final JLabel errorLabel = new JLabel(" ");

    public RegisterPanel(GameMain gameMain, DatabaseManager db) {
        this.gameMain = gameMain;
        this.db = db;
        setPreferredSize(new Dimension(GamePanel.PANEL_WIDTH, GamePanel.PANEL_HEIGHT));
        setBackground(new Color(10, 10, 40));
        setLayout(new GridBagLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        JLabel title = new JLabel("Register");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        form.add(title, c);

        c.gridwidth = 1;
        c.gridy = 1; c.gridx = 0;
        JLabel l1 = new JLabel("Username:"); l1.setForeground(Color.WHITE);
        form.add(l1, c);
        c.gridx = 1;
        form.add(usernameField, c);

        c.gridy = 2; c.gridx = 0;
        JLabel l2 = new JLabel("Password:"); l2.setForeground(Color.WHITE);
        form.add(l2, c);
        c.gridx = 1;
        form.add(passwordField, c);

        c.gridy = 3; c.gridx = 0;
        JLabel l3 = new JLabel("Confirm:"); l3.setForeground(Color.WHITE);
        form.add(l3, c);
        c.gridx = 1;
        form.add(confirmField, c);

        c.gridy = 4; c.gridx = 0; c.gridwidth = 2;
        errorLabel.setForeground(Color.RED);
        form.add(errorLabel, c);

        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> doRegister());
        JButton backBtn = new JButton("Back to Login");
        backBtn.addActionListener(e -> gameMain.showCard(GameMain.CARD_LOGIN));

        c.gridy = 5;
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(registerBtn);
        buttons.add(backBtn);
        form.add(buttons, c);

        add(form);
    }

    private void doRegister() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        String p2 = new String(confirmField.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
            return;
        }
        if (!p.equals(p2)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }
        if (db.usernameExists(u)) {
            errorLabel.setText("Username already taken.");
            return;
        }
        db.registerUser(u, p);
        errorLabel.setForeground(Color.GREEN);
        errorLabel.setText("Registered! You can now log in.");
        usernameField.setText("");
        passwordField.setText("");
        confirmField.setText("");
    }
}
