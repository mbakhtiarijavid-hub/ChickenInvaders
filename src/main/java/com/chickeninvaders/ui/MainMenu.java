package com.chickeninvaders.ui;

import com.chickeninvaders.GameMain;
import com.chickeninvaders.data.User;
import com.chickeninvaders.util.ResourceManager;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends ImagePanel {

    private final GameMain gameMain;
    private final JLabel statusLabel;

    public MainMenu(GameMain gameMain) {
        super(ResourceManager.menuBackground());
        this.gameMain = gameMain;
        setPreferredSize(new Dimension(GamePanel.PANEL_WIDTH, GamePanel.PANEL_HEIGHT));
        setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("CHICKEN INVADERS");
        title.setFont(new Font("Arial", Font.BOLD, 34));
        title.setForeground(Color.YELLOW);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(title);
        inner.add(Box.createVerticalStrut(6));
        inner.add(statusLabel);
        inner.add(Box.createVerticalStrut(30));

        inner.add(menuButton("New Game", e -> gameMain.requestNewGame()));
        inner.add(Box.createVerticalStrut(10));
        inner.add(menuButton("High Scores", e -> gameMain.showCard(GameMain.CARD_HIGHSCORES)));
        inner.add(Box.createVerticalStrut(10));
        inner.add(menuButton("Settings", e -> gameMain.showCard(GameMain.CARD_SETTINGS)));
        inner.add(Box.createVerticalStrut(10));
        inner.add(menuButton("How to Play", e -> gameMain.showCard(GameMain.CARD_HOWTO)));
        inner.add(Box.createVerticalStrut(10));
        inner.add(menuButton("Store", e -> gameMain.showCard(GameMain.CARD_STORE)));
        inner.add(Box.createVerticalStrut(10));
        inner.add(menuButton("Exit", e -> System.exit(0)));

        add(inner);
    }

    private JButton menuButton(String text, java.awt.event.ActionListener l) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 18));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(240, 40));
        b.setFocusPainted(false);
        b.addActionListener(l);
        return b;
    }

    public void refreshLoginState() {
        User u = gameMain.getCurrentUser();
        statusLabel.setText(u == null ? "Not logged in" : "Logged in as: " + u.getUsername());
    }
}
