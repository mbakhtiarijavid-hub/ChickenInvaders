package com.chickeninvaders.entities;

import java.awt.*;

public class Plane {

    public enum PlaneType {
        DEFAULT("Default", 4, 450, 3, 0, 1.0),
        FAST("Fast", 5, 380, 3, 5000, 1.0),
        HEAVY("Heavy", 3, 320, 5, 8000, 1.0),
        SNIPER("Sniper", 4, 260, 3, 10000, 2.0);

        public final String label;
        public final int speed;
        public final int baseFireRateMs;
        public final int initialLives;
        public final int cost;
        public final double bossDamageMultiplier;

        PlaneType(String label, int speed, int baseFireRateMs, int initialLives, int cost, double bossDamageMultiplier) {
            this.label = label;
            this.speed = speed;
            this.baseFireRateMs = baseFireRateMs;
            this.initialLives = initialLives;
            this.cost = cost;
            this.bossDamageMultiplier = bossDamageMultiplier;
        }

        public static PlaneType fromLabel(String label) {
            for (PlaneType t : values()) if (t.label.equalsIgnoreCase(label)) return t;
            return DEFAULT;
        }
    }

    public double x, y;
    public static final int WIDTH = 60;
    public static final int HEIGHT = 60;
    public static final int MAX_LIVES = 5;

    private final PlaneType type;
    private int lives;
    private int simultaneousShots = 1;
    private long lastShotTime = 0;

    private long rapidFireEndTime = 0;
    private long shieldEndTime = 0;
    private long freezeEndTime = 0;

    public Plane(double x, double y, PlaneType type, int startingLives) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.lives = Math.min(startingLives, MAX_LIVES);
    }

    public PlaneType getType() { return type; }
    public int getLives() { return lives; }
    public void setLives(int l) { lives = Math.max(0, Math.min(MAX_LIVES, l)); }
    public void loseLife() { lives = Math.max(0, lives - 1); }
    public void addLife() { lives = Math.min(MAX_LIVES, lives + 1); }
    public boolean isDead() { return lives <= 0; }

    public static final int MAX_SIMULTANEOUS_SHOTS = 6;

    public int getSimultaneousShots() { return simultaneousShots; }
    public void addShot() { simultaneousShots = Math.min(MAX_SIMULTANEOUS_SHOTS, simultaneousShots + 1); }
    public boolean isAtMaxShots() { return simultaneousShots >= MAX_SIMULTANEOUS_SHOTS; }

    public boolean isShieldActive() { return System.currentTimeMillis() < shieldEndTime; }
    public void activateShield(long durationMs) { shieldEndTime = System.currentTimeMillis() + durationMs; }
    public void consumeShield() { shieldEndTime = 0; }
    public long shieldRemainingMs() { return Math.max(0, shieldEndTime - System.currentTimeMillis()); }

    public boolean isRapidFireActive() { return System.currentTimeMillis() < rapidFireEndTime; }
    public void activateRapidFire(long durationMs) { rapidFireEndTime = System.currentTimeMillis() + durationMs; }
    public long rapidFireRemainingMs() { return Math.max(0, rapidFireEndTime - System.currentTimeMillis()); }

    public int currentFireRateMs() {
        return isRapidFireActive() ? Math.max(120, (int) (type.baseFireRateMs / 1.8)) : type.baseFireRateMs;
    }

    public boolean canShoot() {
        return System.currentTimeMillis() - lastShotTime >= currentFireRateMs();
    }

    public void markShot() { lastShotTime = System.currentTimeMillis(); }

    public double getBossDamageMultiplier() { return type.bossDamageMultiplier; }

    public Rectangle getBounds() { return new Rectangle((int) x, (int) y, WIDTH, HEIGHT); }

    public void draw(Graphics2D g, Image img) {
        if (img != null) {
            g.drawImage(img, (int) x, (int) y, WIDTH, HEIGHT, null);
        } else {
            g.setColor(Color.GREEN);
            int[] xs = {(int) x + WIDTH / 2, (int) x, (int) x + WIDTH};
            int[] ys = {(int) y, (int) y + HEIGHT, (int) y + HEIGHT};
            g.fillPolygon(xs, ys, 3);
        }
        if (isShieldActive()) {
            g.setColor(new Color(80, 180, 255, 150));
            g.setStroke(new BasicStroke(3));
            g.drawOval((int) x - 8, (int) y - 8, WIDTH + 16, HEIGHT + 16);
        }
    }
}
