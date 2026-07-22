package com.chickeninvaders.entities;

public class BossLevel4 extends Boss {
    private double vBase;
    private double vPhase = 0;

    public BossLevel4(double x, double y) {
        super(x, y, 90, 1.0);
        this.vBase = y;
    }

    @Override public int scoreValue() { return 500; }
    @Override public long attackIntervalMs() { return 1500; }
    @Override public double eggSpeed() { return 2.6; }
    @Override public String name() { return "Boss - Level 4"; }

    @Override public double[][] attackDirections() {

        return new double[][]{{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
    }

    @Override
    public void updateMovement(int panelWidth) {
        x += direction * hMoveSpeed;
        if (x <= 20 || x + width >= panelWidth - 20) direction *= -1;
        vPhase += 0.03;
        y = vBase + Math.sin(vPhase) * 15;
    }
}
