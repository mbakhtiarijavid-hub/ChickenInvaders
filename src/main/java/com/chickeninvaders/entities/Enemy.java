package com.chickeninvaders.entities;

import java.awt.*;

public abstract class Enemy {

    public enum State { FLYING_IN, IN_GRID }

    public double x, y;
    public int row, col;
    protected boolean alive = true;
    public State state = State.FLYING_IN;
    public double targetX, targetY;
    public double zigzagPhase = Math.random() * Math.PI * 2;
    public long lastEggTime = System.currentTimeMillis() + (long) (Math.random() * 1500);

    public static final int WIDTH = 44;
    public static final int HEIGHT = 34;

    public Enemy(double x, double y, int row, int col) {
        this.x = x;
        this.y = y;
        this.row = row;
        this.col = col;
        this.targetX = x;
        this.targetY = y;
    }

    public abstract int scoreValue();
    public abstract String typeName();

    public double extraOffsetX() { return 0; }

    public boolean canShootAtPlane() { return false; }

    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public void updateFlyIn() {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.hypot(dx, dy);
        double speed = 4.0;
        if (dist < speed) {
            x = targetX;
            y = targetY;
            state = State.IN_GRID;
        } else {
            x += dx / dist * speed;
            y += dy / dist * speed;
        }
    }

    public Rectangle getBounds() { return new Rectangle((int) x, (int) y, WIDTH, HEIGHT); }

    public void draw(Graphics2D g, Image img) {
        if (img != null) {
            g.drawImage(img, (int) x, (int) y, WIDTH, HEIGHT, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval((int) x, (int) y, WIDTH, HEIGHT);
        }
    }
}
