package com.chickeninvaders.entities;

import java.awt.*;

public class Egg {
    public double x, y;
    public double dx, dy;
    public static final int SIZE = 14;
    private boolean alive = true;
    public boolean frozen = false;

    public Egg(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void update(int panelWidth, int panelHeight) {
        if (frozen) return;
        x += dx;
        y += dy;
        if (y > panelHeight + 20 || y < -20 || x < -20 || x > panelWidth + 20) alive = false;
    }

    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public Rectangle getBounds() { return new Rectangle((int) x, (int) y, SIZE, SIZE); }

    public void draw(Graphics2D g, Image img) {
        if (img != null) {
            g.drawImage(img, (int) x, (int) y, SIZE, SIZE, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillOval((int) x, (int) y, SIZE, SIZE);
        }
    }
}
