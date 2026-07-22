package com.chickeninvaders.entities;

import java.awt.*;

public abstract class Boss {
    public double x, y;
    protected int maxHealth;
    protected int health;
    public double hMoveSpeed;
    public int direction = 1;
    protected long lastShotTime = 0;

    public int width = 160;
    public int height = 140;

    public Boss(double x, double y, int maxHealth, double hMoveSpeed) {
        this.x = x;
        this.y = y;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.hMoveSpeed = hMoveSpeed;
    }

    public abstract int scoreValue();
    public abstract long attackIntervalMs();

    public abstract double[][] attackDirections();
    public abstract double eggSpeed();
    public abstract String name();

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isDead() { return health <= 0; }

    public void damage(int amount) { health = Math.max(0, health - amount); }

    public void updateMovement(int panelWidth) {
        x += direction * hMoveSpeed;
        if (x <= 20 || x + width >= panelWidth - 20) direction *= -1;
    }

    public boolean readyToShoot() {
        if (System.currentTimeMillis() - lastShotTime >= attackIntervalMs()) {
            lastShotTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public Rectangle getBounds() { return new Rectangle((int) x, (int) y, width, height); }

    public void draw(Graphics2D g, Image img) {
        if (img != null) {
            g.drawImage(img, (int) x, (int) y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval((int) x, (int) y, width, height);
        }
        drawHealthBar(g);
    }

    protected void drawHealthBar(Graphics2D g) {
        int barW = 220, barH = 18;
        int bx = (int) (x + width / 2.0 - barW / 2.0);
        int by = (int) y - 28;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(bx, by, barW, barH);
        double pct = health / (double) maxHealth;
        g.setColor(pct > 0.5 ? Color.GREEN : (pct > 0.2 ? Color.ORANGE : Color.RED));
        g.fillRect(bx, by, (int) (barW * pct), barH);
        g.setColor(Color.WHITE);
        g.drawRect(bx, by, barW, barH);
        g.drawString(name() + "  " + health + "/" + maxHealth, bx + 4, by + barH - 4);
    }
}
