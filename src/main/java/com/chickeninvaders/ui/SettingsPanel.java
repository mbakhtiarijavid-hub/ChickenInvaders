package com.chickeninvaders.ui;

import com.chickeninvaders.GameMain;
import com.chickeninvaders.audio.SoundManager;
import com.chickeninvaders.data.DatabaseManager;
import com.chickeninvaders.data.User;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final GameMain gameMain;
    private final DatabaseManager db;
    private final SoundManager soundManager;

    private final JCheckBox musicBox = new JCheckBox("Background Music");
    private final JCheckBox shotBox = new JCheckBox("Shot Sound Effect");
    private final JCheckBox crashBox = new JCheckBox("Crash / Explosion Sound Effect");
    private final JCheckBox endBox = new JCheckBox("Game Over / Win Sound");

    public SettingsPanel(GameMain gameMain, DatabaseManager db, SoundManager soundManager) {
        this.gameMain = gameMain;
        this.db = db;
        this.soundManager = soundManager;
        setPreferredSize(new Dimension(GamePanel.PANEL_WIDTH, GamePanel.PANEL_HEIGHT));
        setBackground(new Color(10, 10, 40));
        setLayout(new GridBagLayout());

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.YELLOW);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(title);
        box.add(Box.createVerticalStrut(20));

        for (JCheckBox cb : new JCheckBox[]{musicBox, shotBox, crashBox, endBox}) {
            cb.setForeground(Color.WHITE);
            cb.setOpaque(false);
            cb.setFont(new Font("Arial", Font.PLAIN, 16));
            cb.setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(cb);
            box.add(Box.createVerticalStrut(10));
        }

        musicBox.addActionListener(e -> apply(SoundManager.Category.MUSIC, musicBox.isSelected()));
        shotBox.addActionListener(e -> apply(SoundManager.Category.SHOT, shotBox.isSelected()));
        crashBox.addActionListener(e -> apply(SoundManager.Category.CRASH, crashBox.isSelected()));
        endBox.addActionListener(e -> apply(SoundManager.Category.END, endBox.isSelected()));

        JButton back = new JButton("Back");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.addActionListener(e -> {
            if (gameMain.getCurrentUser() != null) {

            }
            returnFromSettings();
        });
        box.add(Box.createVerticalStrut(20));
        box.add(back);

        add(box);
    }

    private void apply(SoundManager.Category cat, boolean value) {
        soundManager.setEnabled(cat, value);
        if (cat == SoundManager.Category.MUSIC && value) soundManager.startMusic();
        User u = gameMain.getCurrentUser();
        if (u != null) {
            switch (cat) {
                case MUSIC -> u.setMusicOn(value);
                case SHOT -> u.setShotSoundOn(value);
                case CRASH -> u.setCrashSoundOn(value);
                case END -> u.setEndSoundOn(value);
            }
            db.updateUser(u);
        }
    }

    private void returnFromSettings() {
        gameMain.showCard(GameMain.CARD_MENU);
    }

    public void refresh() {
        musicBox.setSelected(soundManager.isEnabled(SoundManager.Category.MUSIC));
        shotBox.setSelected(soundManager.isEnabled(SoundManager.Category.SHOT));
        crashBox.setSelected(soundManager.isEnabled(SoundManager.Category.CRASH));
        endBox.setSelected(soundManager.isEnabled(SoundManager.Category.END));
    }
}
