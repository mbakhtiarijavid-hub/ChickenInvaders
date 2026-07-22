package com.chickeninvaders.entities;

import java.awt.*;

public class Bullet {
    public double x, y;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 26;
    public static final double SPEED = 6.5;
    public boolean bossDoubleDamage;
    private boolean alive = true;

    public Bullet(double x, double y, boolean bossDoubleDamage) {
        this.x = x;
        this.y = y;
        this.bossDoubleDamage = bossDoubleDamage;
    }

    public void update() {
        y -= SPEED;
        if (y + HEIGHT < 0) alive = false;
    }

    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public Rectangle getBounds() { return new Rectangle((int) x, (int) y, WIDTH, HEIGHT); }

    public void draw(Graphics2D g, Image img) {
        if (img != null) {
            g.drawImage(img, (int) x, (int) y, WIDTH, HEIGHT, null);
        } else {

            g.setColor(new Color(255, 235, 60));
            g.fillRoundRect((int) x, (int) y, WIDTH, HEIGHT, 6, 6);
            g.setColor(new Color(255, 90, 0));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect((int) x, (int) y, WIDTH, HEIGHT, 6, 6);
        }
    }
}
