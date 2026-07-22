package com.chickeninvaders.ui;

import com.chickeninvaders.GameMain;
import com.chickeninvaders.audio.SoundManager;
import com.chickeninvaders.data.User;
import com.chickeninvaders.entities.*;
import com.chickeninvaders.util.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 600;
    private static final int ROWS = 5;
    private static final int COLS = 8;
    private static final int CELL_W = 78;
    private static final int CELL_H = 58;
    private static final int GRID_LEFT_MARGIN = 60;
    private static final int GRID_TOP_MARGIN = 60;

    private final javax.swing.Timer timer;
    private final GameMain gameMain;
    private final User user;
    private final SoundManager soundManager;

    private Plane plane;
    private int level;
    private int score = 0;
    private boolean paused = false;
    private boolean settingsOverlayOpen = false;

    private Cell[][] grid;
    private double gridBaseX, gridBaseY;
    private int gridDirection = 1;
    private double gridHSpeed;
    private int gridVStep;
    private long eggIntervalMs;
    private long lastGridEggTime = System.currentTimeMillis();
    private final List<Class<? extends Enemy>> levelEnemyTypes = new ArrayList<>();
    private int levelSpawnCounter;

    private Boss boss;

    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Egg> eggs = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();

    private long freezeUntil = 0;

    private final Set<Integer> keysDown = new HashSet<>();
    private boolean gameEnded = false;

    public GamePanel(GameMain gameMain, User user, SoundManager soundManager) {
        this.gameMain = gameMain;
        this.user = user;
        this.soundManager = soundManager;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        timer = new javax.swing.Timer(16, this);
    }

    public void startNewGame() {
        gameEnded = false;
        score = 0;
        level = 1;
        Plane.PlaneType type = Plane.PlaneType.fromLabel(user.getSelectedPlane());
        plane = new Plane(PANEL_WIDTH / 2.0 - Plane.WIDTH / 2.0, PANEL_HEIGHT - 90, type, type.initialLives);
        bullets.clear(); eggs.clear(); powerUps.clear(); explosions.clear();
        loadLevel(level);
        soundManager.startMusic();
        requestFocusInWindow();
        timer.start();
    }

    private static class LevelConfig {
        List<Class<? extends Enemy>> types;
        int counter;
        double hSpeed;
        int vStep;
        double eggIntervalSec;
        LevelConfig(List<Class<? extends Enemy>> types, int counter, double hSpeed, int vStep, double eggIntervalSec) {
            this.types = types; this.counter = counter; this.hSpeed = hSpeed; this.vStep = vStep; this.eggIntervalSec = eggIntervalSec;
        }
    }

    @SuppressWarnings("unchecked")
    private LevelConfig configFor(int lvl) {

        return switch (lvl) {
            case 1 -> new LevelConfig(List.of(NormalEnemy.class), 2, 0.6, 16, 3.5);
            case 2 -> new LevelConfig(List.of(NormalEnemy.class, FastEnemy.class), 2, 0.9, 16, 2.5);
            case 3 -> new LevelConfig(List.of(NormalEnemy.class, ZigzagEnemy.class), 3, 1.2, 20, 1.8);
            case 5 -> new LevelConfig(List.of(ShooterEnemy.class, FastEnemy.class), 3, 1.5, 20, 1.3);
            case 6 -> new LevelConfig(List.of(ZigzagEnemy.class, ShooterEnemy.class), 4, 1.8, 24, 1.0);
            case 7 -> new LevelConfig(List.of(NormalEnemy.class, FastEnemy.class, ZigzagEnemy.class, ShooterEnemy.class), 4, 2.1, 24, 0.9);
            default -> new LevelConfig(List.of(NormalEnemy.class), 2, 0.6, 16, 3.5);
        };
    }

    private void loadLevel(int lvl) {
        bullets.clear(); eggs.clear(); powerUps.clear();
        boss = null;
        grid = null;
        gridBaseX = GRID_LEFT_MARGIN;
        gridBaseY = GRID_TOP_MARGIN;
        gridDirection = 1;

        if (lvl == 4 || lvl == 8) {
            if (lvl == 4) {
                boss = new BossLevel4(PANEL_WIDTH / 2.0 - 80, 70);
            } else {
                boss = new BossLevel8(PANEL_WIDTH / 2.0 - 100, 60);
            }
            return;
        }

        LevelConfig cfg = configFor(lvl);
        levelEnemyTypes.clear();
        levelEnemyTypes.addAll(cfg.types);
        levelSpawnCounter = cfg.counter;
        gridHSpeed = cfg.hSpeed;
        gridVStep = cfg.vStep;
        eggIntervalMs = (long) (cfg.eggIntervalSec * 1000);
        lastGridEggTime = System.currentTimeMillis();

        grid = new Cell[ROWS][COLS];
        Random rnd = new Random();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Class<? extends Enemy> t = levelEnemyTypes.get(rnd.nextInt(levelEnemyTypes.size()));
                Cell cell = new Cell(r, c, levelSpawnCounter - 1, t);
                cell.currentEnemy = instantiate(t, gridBaseX + c * CELL_W, gridBaseY + r * CELL_H, r, c);
                cell.currentEnemy.state = Enemy.State.IN_GRID;
                grid[r][c] = cell;
            }
        }
    }

    private Enemy instantiate(Class<? extends Enemy> type, double x, double y, int row, int col) {
        try {
            if (type == NormalEnemy.class) return new NormalEnemy(x, y, row, col);
            if (type == FastEnemy.class) return new FastEnemy(x, y, row, col);
            if (type == ZigzagEnemy.class) return new ZigzagEnemy(x, y, row, col);
            if (type == ShooterEnemy.class) return new ShooterEnemy(x, y, row, col);
        } catch (Exception ignored) { }
        return new NormalEnemy(x, y, row, col);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused && !gameEnded) {
            update();
        }
        repaint();
    }

    private void update() {
        handleInput();
        updatePlaneShooting();
        updateBullets();
        if (grid != null) updateGrid(); else if (boss != null) updateBoss();
        updateEggs();
        updatePowerUps();
        updateExplosions();
        checkCollisions();
        checkLevelClear();
    }

    private void handleInput() {
        int speed = plane.getType().speed;
        if (keysDown.contains(KeyEvent.VK_LEFT) || keysDown.contains(KeyEvent.VK_A)) plane.x -= speed;
        if (keysDown.contains(KeyEvent.VK_RIGHT) || keysDown.contains(KeyEvent.VK_D)) plane.x += speed;
        if (keysDown.contains(KeyEvent.VK_UP) || keysDown.contains(KeyEvent.VK_W)) plane.y -= speed;
        if (keysDown.contains(KeyEvent.VK_DOWN) || keysDown.contains(KeyEvent.VK_S)) plane.y += speed;
        plane.x = Math.max(0, Math.min(PANEL_WIDTH - Plane.WIDTH, plane.x));
        plane.y = Math.max(0, Math.min(PANEL_HEIGHT - Plane.HEIGHT, plane.y));
    }

    private void updatePlaneShooting() {
        if (keysDown.contains(KeyEvent.VK_SPACE) && plane.canShoot()) {
            int n = plane.getSimultaneousShots();
            double centerX = plane.x + Plane.WIDTH / 2.0;
            double spacing = 14;
            double startX = centerX - (n - 1) * spacing / 2.0;
            boolean doubleDmg = plane.getBossDamageMultiplier() > 1.0;
            for (int i = 0; i < n; i++) {
                bullets.add(new Bullet(startX + i * spacing - Bullet.WIDTH / 2.0, plane.y - Bullet.HEIGHT, doubleDmg));
            }
            plane.markShot();
            soundManager.playShot();
        }
    }

    private void updateBullets() {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update();
            if (!b.isAlive()) it.remove();
        }
    }

    private boolean isFrozen() { return System.currentTimeMillis() < freezeUntil; }

    private void updateGrid() {
        if (isFrozen()) return;
        gridBaseX += gridDirection * gridHSpeed;
        if (gridBaseX <= GRID_LEFT_MARGIN - 40 || gridBaseX + (COLS - 1) * CELL_W >= PANEL_WIDTH - GRID_LEFT_MARGIN + 40) {
            gridDirection *= -1;
            gridBaseY += gridVStep;
        }
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                Enemy en = cell.currentEnemy;
                if (en == null) continue;
                if (en.state == Enemy.State.IN_GRID) {
                    en.x = gridBaseX + cell.col * CELL_W + en.extraOffsetX();
                    en.y = gridBaseY + cell.row * CELL_H;
                    if (en.canShootAtPlane() && Math.random() < 0.0018) {
                        double dx = (plane.x + Plane.WIDTH / 2.0) - en.x;
                        double dy = (plane.y) - en.y;
                        double dist = Math.hypot(dx, dy);
                        eggs.add(new Egg(en.x + Enemy.WIDTH / 2.0, en.y + Enemy.HEIGHT,
                                dx / dist * 3.0, dy / dist * 3.0));
                    }
                } else {
                    en.targetX = gridBaseX + cell.col * CELL_W;
                    en.targetY = gridBaseY + cell.row * CELL_H;
                    en.updateFlyIn();
                }
                if (en.y > PANEL_HEIGHT) {

                    explosions.add(new Explosion(en.x + Enemy.WIDTH / 2.0, PANEL_HEIGHT - 10, 30));
                    onEnemyRemoved(cell);
                    loseLife();
                }
            }
        }

        if (System.currentTimeMillis() - lastGridEggTime >= eggIntervalMs) {
            lastGridEggTime = System.currentTimeMillis();
            dropRandomEgg();
        }
    }

    private void dropRandomEgg() {
        List<Enemy> alive = new ArrayList<>();
        for (Cell[] row : grid) for (Cell c : row) if (c.currentEnemy != null && c.currentEnemy.state == Enemy.State.IN_GRID) alive.add(c.currentEnemy);
        if (alive.isEmpty()) return;
        Enemy chosen = alive.get(new Random().nextInt(alive.size()));
        eggs.add(new Egg(chosen.x + Enemy.WIDTH / 2.0 - Egg.SIZE / 2.0, chosen.y + Enemy.HEIGHT, 0, 2.6));
    }

    private void updateBoss() {
        if (boss == null) return;
        if (!isFrozen()) {
            boss.updateMovement(PANEL_WIDTH);
            if (boss.readyToShoot()) {
                for (double[] dir : boss.attackDirections()) {
                    eggs.add(new Egg(boss.x + boss.width / 2.0, boss.y + boss.height / 2.0,
                            dir[0] * boss.eggSpeed(), dir[1] * boss.eggSpeed()));
                }
            }
        }
    }

    private void updateEggs() {
        boolean frozen = isFrozen();
        Iterator<Egg> it = eggs.iterator();
        while (it.hasNext()) {
            Egg eg = it.next();
            eg.frozen = frozen;
            eg.update(PANEL_WIDTH, PANEL_HEIGHT);
            if (!eg.isAlive()) it.remove();
        }
    }

    private void updatePowerUps() {
        Iterator<PowerUp> it = powerUps.iterator();
        while (it.hasNext()) {
            PowerUp p = it.next();
            p.update(PANEL_HEIGHT);
            if (!p.isAlive()) it.remove();
        }
    }

    private void updateExplosions() {
        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion ex = it.next();
            ex.update();
            if (ex.isDone()) it.remove();
        }
    }

    private void checkCollisions() {
        Rectangle planeBounds = plane.getBounds();

        checkBulletsVsGridEnemies();
        checkBulletsVsBoss();
        checkGridEnemiesVsPlane(planeBounds);
        if (checkBossVsPlane(planeBounds)) return;
        checkEggsVsPlane(planeBounds);
        checkPowerUpsVsPlane(planeBounds);
    }

    private void checkBulletsVsGridEnemies() {
        if (grid == null) return;
        Iterator<Bullet> bit = bullets.iterator();
        bulletLoop:
        while (bit.hasNext()) {
            Bullet b = bit.next();
            for (Cell[] row : grid) {
                for (Cell cell : row) {
                    Enemy en = cell.currentEnemy;
                    if (en == null || !en.isAlive()) continue;
                    if (b.getBounds().intersects(en.getBounds())) {
                        killGridEnemy(cell, en, true);
                        bit.remove();
                        continue bulletLoop;
                    }
                }
            }
        }
    }

    private void killGridEnemy(Cell cell, Enemy en, boolean scoreAndDrop) {
        explosions.add(new Explosion(en.x + Enemy.WIDTH / 2.0, en.y + Enemy.HEIGHT / 2.0, 26));
        soundManager.playCrash();
        if (scoreAndDrop) {
            score += en.scoreValue();
            maybeDropPowerUp(en.x, en.y);
        }
        onEnemyRemoved(cell);
    }

    private void checkBulletsVsBoss() {
        if (boss == null) return;
        Iterator<Bullet> bit = bullets.iterator();
        while (bit.hasNext()) {
            Bullet b = bit.next();
            if (!b.getBounds().intersects(boss.getBounds())) continue;
            int dmg = (int) Math.round(plane.getBossDamageMultiplier());
            boss.damage(dmg);
            explosions.add(new Explosion(b.x, b.y, 14));
            soundManager.playCrash();
            bit.remove();
            if (boss.isDead()) {
                score += boss.scoreValue();
                explosions.add(new Explosion(boss.x + boss.width / 2.0, boss.y + boss.height / 2.0, 90));
                boss = null;
                break;
            }
        }
    }

    private void checkGridEnemiesVsPlane(Rectangle planeBounds) {
        if (grid == null) return;
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                Enemy en = cell.currentEnemy;
                if (en == null || !en.isAlive()) continue;
                if (!planeBounds.intersects(en.getBounds())) continue;
                killGridEnemy(cell, en, false);
                if (plane.isShieldActive()) {
                    plane.consumeShield();
                } else {
                    loseLife();
                }
            }
        }
    }

    private boolean checkBossVsPlane(Rectangle planeBounds) {
        if (boss == null || !planeBounds.intersects(boss.getBounds())) return false;
        explosions.add(new Explosion(plane.x + Plane.WIDTH / 2.0, plane.y + Plane.HEIGHT / 2.0, 40));
        plane.setLives(0);
        endGame(false);
        return true;
    }

    private void checkEggsVsPlane(Rectangle planeBounds) {
        Iterator<Egg> eit = eggs.iterator();
        while (eit.hasNext()) {
            Egg eg = eit.next();
            if (!planeBounds.intersects(eg.getBounds())) continue;
            eit.remove();
            explosions.add(new Explosion(eg.x, eg.y, 20));
            if (!plane.isShieldActive()) {
                soundManager.playCrash();
                loseLife();
            }
        }
    }

    private void checkPowerUpsVsPlane(Rectangle planeBounds) {
        Iterator<PowerUp> pit = powerUps.iterator();
        while (pit.hasNext()) {
            PowerUp p = pit.next();
            if (planeBounds.intersects(p.getBounds())) {
                applyPowerUp(p.type);
                pit.remove();
            }
        }
    }

    private void onEnemyRemoved(Cell cell) {
        cell.currentEnemy.kill();
        if (cell.remainingSpawns > 0) {
            cell.remainingSpawns--;
            boolean fromLeft = new Random().nextBoolean();
            double spawnX = fromLeft ? -Enemy.WIDTH : PANEL_WIDTH;
            double spawnY = -Enemy.HEIGHT;
            Enemy replacement = instantiate(cell.enemyClass, spawnX, spawnY, cell.row, cell.col);
            replacement.state = Enemy.State.FLYING_IN;
            replacement.targetX = gridBaseX + cell.col * CELL_W;
            replacement.targetY = gridBaseY + cell.row * CELL_H;
            cell.currentEnemy = replacement;
        } else {
            cell.currentEnemy = null;
        }
    }

    private static final double POWERUP_DROP_CHANCE = 0.08;

    private void maybeDropPowerUp(double x, double y) {
        if (Math.random() < POWERUP_DROP_CHANCE) {
            powerUps.add(new PowerUp(x, y, PowerUp.randomType()));
        }
    }

    private void applyPowerUp(PowerUp.Type type) {
        switch (type) {
            case ADD_SHOT -> plane.addShot();
            case RAPID_FIRE -> plane.activateRapidFire(8000);
            case EXTRA_LIFE -> plane.addLife();
            case SHIELD -> plane.activateShield(10000);
            case FREEZE_BOMB -> freezeUntil = System.currentTimeMillis() + 3000;
        }
    }

    private void loseLife() {
        plane.loseLife();
        if (plane.isDead()) {
            endGame(false);
        }
    }

    private void checkLevelClear() {
        if (gameEnded) return;
        boolean cleared;
        if (grid != null) {
            cleared = true;
            outer:
            for (Cell[] row : grid) {
                for (Cell c : row) {
                    if (!(c.currentEnemy == null && c.remainingSpawns <= 0)) { cleared = false; break outer; }
                }
            }
        } else {
            cleared = boss == null;
        }
        if (cleared) {

            if (level == 8) {
                endGame(true);
                return;
            }
            if (level != 4) {
                score += 200;
            }
            level++;
            loadLevel(level);
        }
    }

    private void endGame(boolean won) {
        if (gameEnded) return;
        gameEnded = true;
        timer.stop();
        soundManager.stopMusic();
        if (won) soundManager.playWin(); else soundManager.playGameOver();
        gameMain.onGameEnded(score, level, won);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Image bg = ResourceManager.background(level - 1);
        if (bg != null) g.drawImage(bg, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null);
        else { g.setColor(new Color(10, 10, 40)); g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT); }

        if (grid != null) {
            for (Cell[] row : grid) {
                for (Cell cell : row) {
                    Enemy en = cell.currentEnemy;
                    if (en != null) en.draw(g, ResourceManager.chicken(en.typeName()));
                }
            }
        }
        if (boss != null) {
            Image bossImg = level == 8 ? ResourceManager.boss2() : ResourceManager.boss1();
            boss.draw(g, bossImg);
        }

        for (Egg eg : eggs) eg.draw(g, ResourceManager.egg());
        for (Bullet b : bullets) b.draw(g, ResourceManager.bullet());
        for (PowerUp p : powerUps) p.draw(g, ResourceManager.powerUp(p.type));
        for (Explosion ex : explosions) ex.draw(g, ResourceManager.explosionSmall());

        if (plane != null) plane.draw(g, ResourceManager.plane(plane.getType()));

        drawHUD(g);

        if (paused) drawCenteredBanner(g, "PAUSED - Press P to resume");
        if (isFrozen()) drawFreezeOverlay(g);
    }

    private void drawHUD(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("User: " + user.getUsername(), 10, 20);
        g.drawString("Score: " + score, 10, 40);
        g.drawString("Level: " + level + "/8", 10, 60);
        g.drawString("Lives: " + (plane != null ? plane.getLives() : 0), 10, 80);
        g.drawString("Shots: " + (plane != null ? plane.getSimultaneousShots() : 1), 10, 100);

        int y = 120;
        if (plane != null && plane.isShieldActive()) {
            g.setColor(Color.CYAN);
            g.drawString("Shield: " + (plane.shieldRemainingMs() / 1000 + 1) + "s", 10, y);
            y += 20;
        }
        if (plane != null && plane.isRapidFireActive()) {
            g.setColor(Color.MAGENTA);
            g.drawString("Rapid Fire: " + (plane.rapidFireRemainingMs() / 1000 + 1) + "s", 10, y);
            y += 20;
        }
        if (isFrozen()) {
            g.setColor(Color.WHITE);
            g.drawString("Freeze active", 10, y);
        }
    }

    private void drawFreezeOverlay(Graphics2D g) {
        g.setColor(new Color(150, 220, 255, 60));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
    }

    private void drawCenteredBanner(Graphics2D g, String text) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, PANEL_HEIGHT / 2 - 30, PANEL_WIDTH, 60);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text);
        g.drawString(text, (PANEL_WIDTH - tw) / 2, PANEL_HEIGHT / 2 + 8);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysDown.add(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_P) {
            paused = !paused;
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            timer.stop();
            soundManager.stopMusic();
            gameMain.onGameEnded(score, level, false);
        } else if (e.getKeyCode() == KeyEvent.VK_M) {
            settingsOverlayOpen = !settingsOverlayOpen;
            gameMain.toggleInGameSettings(settingsOverlayOpen);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { keysDown.remove(e.getKeyCode()); }

    @Override
    public void keyTyped(KeyEvent e) { }

    public void stopLoop() { timer.stop(); }
}
