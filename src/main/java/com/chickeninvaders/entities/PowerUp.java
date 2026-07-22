package com.chickeninvaders.entities;

import java.awt.*;

public class PowerUp {
    public enum Type { ADD_SHOT, RAPID_FIRE, EXTRA_LIFE, SHIELD, FREEZE_BOMB }

    public double x, y;
    public Type type;
    public static final int SIZE = 30;
    public static final double SPEED = 1.2;
    private boolean alive = true;

    public PowerUp(double x, double y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void update(int panelHeight) {
        y += SPEED;
        if (y > panelHeight + 20) alive = false;
    }

    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public Rectangle getBounds() { return new Rectangle((int) x, (int) y, SIZE, SIZE); }

    public void draw(Graphics2D g, Image img) {
        if (img != null) {
            g.drawImage(img, (int) x, (int) y, SIZE, SIZE, null);
            return;
        }
        Color c = switch (type) {
            case ADD_SHOT -> Color.CYAN;
            case RAPID_FIRE -> Color.MAGENTA;
            case EXTRA_LIFE -> Color.PINK;
            case SHIELD -> Color.BLUE;
            case FREEZE_BOMB -> Color.WHITE;
        };
        g.setColor(c);
        g.fillRoundRect((int) x, (int) y, SIZE, SIZE, 8, 8);
        g.setColor(Color.BLACK);
        g.drawRoundRect((int) x, (int) y, SIZE, SIZE, 8, 8);
    }

    public static Type randomType() {
        Type[] vals = Type.values();
        return vals[(int) (Math.random() * vals.length)];
    }
}
