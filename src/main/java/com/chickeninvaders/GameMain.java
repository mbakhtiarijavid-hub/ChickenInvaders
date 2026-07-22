package com.chickeninvaders;

import com.chickeninvaders.audio.SoundManager;
import com.chickeninvaders.data.DatabaseManager;
import com.chickeninvaders.data.User;
import com.chickeninvaders.ui.*;

import javax.swing.*;
import java.awt.*;

public class GameMain extends JFrame {

    public static final String CARD_MENU = "menu";
    public static final String CARD_LOGIN = "login";
    public static final String CARD_REGISTER = "register";
    public static final String CARD_GAME = "game";
    public static final String CARD_HIGHSCORES = "highscores";
    public static final String CARD_SETTINGS = "settings";
    public static final String CARD_HOWTO = "howto";
    public static final String CARD_STORE = "store";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel();

    private final DatabaseManager db = new DatabaseManager();
    private final SoundManager soundManager = new SoundManager();

    private User currentUser;

    private MainMenu mainMenu;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private GamePanel gamePanel;
    private HighScorePanel highScorePanel;
    private SettingsPanel settingsPanel;
    private HowToPlayPanel howToPlayPanel;
    private StorePanel storePanel;

    public GameMain() {
        super("Chicken Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cards.setLayout(cardLayout);

        mainMenu = new MainMenu(this);
        loginPanel = new LoginPanel(this, db);
        registerPanel = new RegisterPanel(this, db);
        gamePanel = new GamePanel(this, null, soundManager);
        highScorePanel = new HighScorePanel(this, db);
        settingsPanel = new SettingsPanel(this, db, soundManager);
        howToPlayPanel = new HowToPlayPanel(this);
        storePanel = new StorePanel(this, db);

        cards.add(mainMenu, CARD_MENU);
        cards.add(loginPanel, CARD_LOGIN);
        cards.add(registerPanel, CARD_REGISTER);
        cards.add(gamePanel, CARD_GAME);
        cards.add(highScorePanel, CARD_HIGHSCORES);
        cards.add(settingsPanel, CARD_SETTINGS);
        cards.add(howToPlayPanel, CARD_HOWTO);
        cards.add(storePanel, CARD_STORE);

        add(cards);
        pack();
        setLocationRelativeTo(null);

        soundManager.startMusic();
        showCard(CARD_MENU);
    }

    public void showCard(String name) {
        mainMenu.refreshLoginState();
        cardLayout.show(cards, name);
        if (name.equals(CARD_GAME)) {
            gamePanel.requestFocusInWindow();
        } else {
            soundManager.ensureMusicPlaying();
        }
        if (name.equals(CARD_HIGHSCORES)) {
            highScorePanel.refresh();
        }
        if (name.equals(CARD_SETTINGS)) {
            settingsPanel.refresh();
        }
        if (name.equals(CARD_STORE)) {
            storePanel.refresh();
        }
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User u) { this.currentUser = u; }
    public DatabaseManager getDb() { return db; }
    public SoundManager getSoundManager() { return soundManager; }

    public void requestNewGame() {
        if (currentUser == null) {
            showCard(CARD_LOGIN);
            return;
        }

        cards.remove(gamePanel);
        gamePanel = new GamePanel(this, currentUser, soundManager);
        cards.add(gamePanel, CARD_GAME);
        gamePanel.startNewGame();
        showCard(CARD_GAME);
    }

    public void onGameEnded(int score, int lastLevel, boolean won) {
        if (currentUser != null) {
            db.recordGame(currentUser.getUsername(), score, lastLevel,
                    soundManager.isEnabled(SoundManager.Category.MUSIC),
                    soundManager.isEnabled(SoundManager.Category.SHOT),
                    soundManager.isEnabled(SoundManager.Category.CRASH),
                    soundManager.isEnabled(SoundManager.Category.END));
            currentUser = db.login(currentUser.getUsername(), currentUser.getPassword());
        }
        String msg = won ? "You won! Final score: " + score : "Game Over. Final score: " + score;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, msg, won ? "Victory!" : "Game Over", JOptionPane.INFORMATION_MESSAGE);
            showCard(CARD_MENU);
        });
    }

    public void toggleInGameSettings(boolean open) {
        if (open) {
            showCard(CARD_SETTINGS);
        } else {
            showCard(CARD_GAME);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameMain().setVisible(true));
    }
}
