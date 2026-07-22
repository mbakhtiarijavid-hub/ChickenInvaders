package com.chickeninvaders.ui;

import com.chickeninvaders.GameMain;

import javax.swing.*;
import java.awt.*;

public class HowToPlayPanel extends JPanel {

    public HowToPlayPanel(GameMain gameMain) {
        setPreferredSize(new Dimension(GamePanel.PANEL_WIDTH, GamePanel.PANEL_HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(10, 10, 40));

        JLabel title = new JLabel("How to Play", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.YELLOW);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        String html = "<html><body style='color:white;font-family:Arial;font-size:13px;padding:20px;'>"
                + "<b style='color:#ffd54f;font-size:15px;'>Controls</b><br>"
                + "&larr; / A : move left &nbsp;&nbsp; &rarr; / D : move right<br>"
                + "&uarr; / W : move up &nbsp;&nbsp; &darr; / S : move down<br>"
                + "Space : shoot (hold to keep firing) &nbsp;&nbsp; P : pause / resume<br>"
                + "Esc : quit to main menu (ends the run) &nbsp;&nbsp; M : open sound settings mid-game<br><br>"

                + "<b style='color:#ffd54f;font-size:15px;'>Goal</b><br>"
                + "Clear the grid of chickens on levels 1, 2, 3, 5, 6 and 7, then defeat the mid-boss "
                + "on level 4 and the final boss on level 8 to win the game.<br><br>"

                + "<b style='color:#ffd54f;font-size:15px;'>Enemy types</b><br>"
                + "&bull; <b>Normal</b> - standard grid movement, drops eggs straight down (10 pts).<br>"
                + "&bull; <b>Fast</b> - moves faster than Normal (15 pts).<br>"
                + "&bull; <b>Zigzag</b> - weaves side to side while in formation (20 pts).<br>"
                + "&bull; <b>Shooter</b> - occasionally fires an egg aimed straight at your plane, "
                + "in addition to the usual vertical drop (25 pts).<br>"
                + "When a chicken is destroyed and its cell still has more chickens queued up, a "
                + "replacement flies in from a top corner to take its place.<br><br>"

                + "<b style='color:#ffd54f;font-size:15px;'>Power-ups (about 8% drop chance per kill)</b><br>"
                + "&bull; <b>Add Shot</b> - permanent, stacks: fires one more bullet at a time (caps at 6).<br>"
                + "&bull; <b>Rapid Fire</b> - temporarily shoots faster (8 seconds).<br>"
                + "&bull; <b>Extra Life</b> - adds one life, up to a maximum of 5.<br>"
                + "&bull; <b>Shield</b> - 10 seconds of protection: blocks damage from eggs without breaking, "
                + "but a direct collision with a chicken destroys the shield instead of costing a life.<br>"
                + "&bull; <b>Freeze Bomb</b> - freezes every enemy and egg on screen for 3 seconds.<br><br>"

                + "<b style='color:#ffd54f;font-size:15px;'>Bosses</b><br>"
                + "&bull; <b>Level 4 (mid-boss)</b> - 90 hit points, fires eggs in 4 directions.<br>"
                + "&bull; <b>Level 8 (final boss)</b> - 170 hit points, fires eggs in 8 directions and "
                + "moves more erratically. Defeating it wins the game.<br>"
                + "Colliding with a boss (in any state, shield or not) destroys your plane instantly "
                + "and ends the game - always keep your distance from bosses.<br><br>"

                + "<b style='color:#ffd54f;font-size:15px;'>Important rules</b><br>"
                + "&bull; If a chicken flies off the bottom of the screen, you lose one life and it is destroyed.<br>"
                + "&bull; Colliding with a chicken destroys it and costs one life, unless your shield is "
                + "active (the shield absorbs the hit and is destroyed instead).<br>"
                + "&bull; Each bullet is destroyed the instant it hits something - it never passes through "
                + "to hit a chicken behind it.<br>"
                + "&bull; Lives never refresh between levels - if they reach zero, it's Game Over.<br><br>"

                + "<b style='color:#ffd54f;font-size:15px;'>Sound settings</b><br>"
                + "Background music, shot sound effect, crash/explosion sound effect and the game-over/win "
                + "sound can each be turned on or off independently from the Settings screen (or by pressing "
                + "M during a run).<br><br>"

                + "<b style='color:#ffd54f;font-size:15px;'>Store</b><br>"
                + "Spend your all-time high score on a new plane with different speed, fire rate, starting "
                + "lives, or even double damage against bosses (Sniper). Only one plane can be active at a time."
                + "</body></html>";
        JLabel body = new JLabel(html);
        add(new JScrollPane(body), BorderLayout.CENTER);

        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> gameMain.showCard(GameMain.CARD_MENU));
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(back);
        add(south, BorderLayout.SOUTH);
    }
}
