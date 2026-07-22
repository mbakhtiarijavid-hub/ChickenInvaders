package com.chickeninvaders.ui;

import com.chickeninvaders.GameMain;
import com.chickeninvaders.data.DatabaseManager;
import com.chickeninvaders.data.User;
import com.chickeninvaders.entities.Plane;
import com.chickeninvaders.util.ResourceManager;

import javax.swing.*;
import java.awt.*;

public class StorePanel extends JPanel {

    private final GameMain gameMain;
    private final DatabaseManager db;
    private final JLabel pointsLabel = new JLabel();
    private final JLabel currentPlaneLabel = new JLabel();

    public StorePanel(GameMain gameMain, DatabaseManager db) {
        this.gameMain = gameMain;
        this.db = db;
        setPreferredSize(new Dimension(GamePanel.PANEL_WIDTH, GamePanel.PANEL_HEIGHT));
        setBackground(new Color(10, 10, 40));
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Store - Buy a Plane", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.YELLOW);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        pointsLabel.setForeground(Color.WHITE);
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentPlaneLabel.setForeground(Color.GREEN);
        currentPlaneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(pointsLabel);
        center.add(currentPlaneLabel);
        center.add(Box.createVerticalStrut(20));

        for (Plane.PlaneType t : Plane.PlaneType.values()) {
            center.add(planeRow(t));
            center.add(Box.createVerticalStrut(10));
        }

        add(center, BorderLayout.CENTER);

        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> gameMain.showCard(GameMain.CARD_MENU));
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(back);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel planeRow(Plane.PlaneType t) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        JLabel icon = new JLabel();
        Image img = ResourceManager.plane(t);
        if (img != null) {
            icon.setIcon(new ImageIcon(img.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        } else {
            icon.setPreferredSize(new Dimension(40, 40));
        }
        JLabel info = new JLabel(String.format("%-8s | speed %d | fire rate %dms | lives %d | cost %d%s",
                t.label, t.speed, t.baseFireRateMs, t.initialLives, t.cost,
                t.bossDamageMultiplier > 1 ? " | 2x boss damage" : ""));
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JButton buy = new JButton(t.cost == 0 ? "Select" : "Buy (" + t.cost + " pts)");
        buy.addActionListener(e -> buy(t));
        row.add(icon);
        row.add(info);
        row.add(buy);
        return row;
    }

    private void buy(Plane.PlaneType t) {
        User u = gameMain.getCurrentUser();
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Please log in first.");
            return;
        }
        if (u.getHighScore() < t.cost) {
            JOptionPane.showMessageDialog(this, "Not enough points. You need " + t.cost + " points.");
            return;
        }
        u.setSelectedPlane(t.label);
        db.updateUser(u);
        refresh();
        JOptionPane.showMessageDialog(this, "Plane set to " + t.label + " for your next game.");
    }

    public void refresh() {
        User u = gameMain.getCurrentUser();
        if (u == null) {
            pointsLabel.setText("Log in to use the store.");
            currentPlaneLabel.setText("");
        } else {
            pointsLabel.setText("Your high score (points): " + u.getHighScore());
            currentPlaneLabel.setText("Active plane: " + u.getSelectedPlane());
        }
    }
}
