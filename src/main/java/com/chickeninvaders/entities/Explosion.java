package com.chickeninvaders.entities;

import java.awt.*;

public class Explosion {
    public double x, y;
    private int life = 0;
    private final int maxLife = 20;
    private final int maxRadius;

    public Explosion(double x, double y, int maxRadius) {
        this.x = x;
        this.y = y;
        this.maxRadius = maxRadius;
    }

    public void update() { life++; }
    public boolean isDone() { return life >= maxLife; }

    public void draw(Graphics2D g, Image img) {
        float progress = life / (float) maxLife;
        int radius = (int) (maxRadius * progress);
        if (img != null) {
            int size = (int) (maxRadius * 2 * (0.5f + progress));
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, 1f - progress)));
            g.drawImage(img, (int) (x - size / 2.0), (int) (y - size / 2.0), size, size, null);
            g.setComposite(old);
        } else {
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, 1f - progress)));
            g.setColor(Color.ORANGE);
            g.fillOval((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);
            g.setColor(Color.RED);
            g.drawOval((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);
            g.setComposite(old);
        }
    }
}
