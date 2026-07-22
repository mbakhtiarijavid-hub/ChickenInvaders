package com.chickeninvaders.ui;

import com.chickeninvaders.GameMain;
import com.chickeninvaders.data.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HighScorePanel extends JPanel {

    private final GameMain gameMain;
    private final DatabaseManager db;
    private final DefaultTableModel model;

    public HighScorePanel(GameMain gameMain, DatabaseManager db) {
        this.gameMain = gameMain;
        this.db = db;
        setPreferredSize(new Dimension(GamePanel.PANEL_WIDTH, GamePanel.PANEL_HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(10, 10, 40));

        JLabel title = new JLabel("High Scores", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.YELLOW);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Username", "Score", "Level Reached", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setEnabled(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> gameMain.showCard(GameMain.CARD_MENU));
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(back);
        add(south, BorderLayout.SOUTH);
    }

    public void refresh() {
        model.setRowCount(0);
        List<String[]> rows = db.getHighScores();
        for (String[] row : rows) {
            model.addRow(new Object[]{row[0], row[1], row[2], row[3]});
        }
    }
}
