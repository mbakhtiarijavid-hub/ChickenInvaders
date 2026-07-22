package com.chickeninvaders.ui;

import com.chickeninvaders.GameMain;
import com.chickeninvaders.audio.SoundManager;
import com.chickeninvaders.data.DatabaseManager;
import com.chickeninvaders.data.User;
import com.chickeninvaders.util.ResourceManager;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends ImagePanel {

    private final GameMain gameMain;
    private final DatabaseManager db;
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JLabel errorLabel = new JLabel(" ");

    public LoginPanel(GameMain gameMain, DatabaseManager db) {
        super(ResourceManager.loginBackground());
        this.gameMain = gameMain;
        this.db = db;
        setPreferredSize(new Dimension(GamePanel.PANEL_WIDTH, GamePanel.PANEL_HEIGHT));
        setLayout(new GridBagLayout());

        JPanel form = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 140));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
        };
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        JLabel title = new JLabel("Login");
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

        c.gridy = 3; c.gridx = 0; c.gridwidth = 2;
        errorLabel.setForeground(Color.RED);
        form.add(errorLabel, c);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());
        JButton registerBtn = new JButton("Go to Register");
        registerBtn.addActionListener(e -> gameMain.showCard(GameMain.CARD_REGISTER));
        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> gameMain.showCard(GameMain.CARD_MENU));

        c.gridy = 4;
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(loginBtn);
        buttons.add(registerBtn);
        buttons.add(backBtn);
        form.add(buttons, c);

        add(form);
    }

    private void doLogin() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        User user = db.login(u, p);
        if (user == null) {
            errorLabel.setText("Invalid username or password.");
            return;
        }
        errorLabel.setText(" ");
        gameMain.setCurrentUser(user);
        gameMain.getSoundManager().setEnabled(SoundManager.Category.MUSIC, user.isMusicOn());
        gameMain.getSoundManager().setEnabled(SoundManager.Category.SHOT, user.isShotSoundOn());
        gameMain.getSoundManager().setEnabled(SoundManager.Category.CRASH, user.isCrashSoundOn());
        gameMain.getSoundManager().setEnabled(SoundManager.Category.END, user.isEndSoundOn());
        usernameField.setText("");
        passwordField.setText("");
        gameMain.requestNewGame();
    }
}
